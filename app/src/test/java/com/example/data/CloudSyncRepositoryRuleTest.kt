package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class CloudSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndObserveStudentProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CloudSyncRepository(firestore)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createInitialProfileIfMissing(
                name = "Arun Sharma",
                email = ALICE_EMAIL,
                targetExam = "JEE Main & Advanced 2026"
            )
        }
        assertTrue("Create profile failed: ${createResult.exceptionOrNull()}", createResult.isSuccess)

        val emittedProfile = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeStudentProfile(uid).first { it != null }
        }
        assertNotNull(emittedProfile)
        assertEquals(uid, emittedProfile?.userId)
        assertEquals("Arun Sharma", emittedProfile?.name)
    }

    @Test
    fun saveAndObserveDppAttempt_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CloudSyncRepository(firestore)
        val dppId = "dpp_hcv_${UUID.randomUUID().toString().take(8)}"

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveDppAttempt(
                dppId = dppId,
                title = "DPP 01: Units, Dimensions & Measurement",
                subject = "PHYSICS",
                score = 4,
                totalQuestions = 4,
                timeSpentSeconds = 300,
                status = "COMPLETED"
            )
        }
        assertTrue("Save DPP failed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)

        val emittedAttempts = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeDppAttempts(uid).first { list -> list.any { it.dppId == dppId } }
        }
        assertTrue(emittedAttempts.any { it.dppId == dppId && it.score == 4L })
    }

    @Test
    fun getDppAttemptById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CloudSyncRepository(firestore)
        val dppId = "dpp_secret_${UUID.randomUUID().toString().take(8)}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveDppAttempt(
                dppId = dppId,
                title = "DPP Secret",
                subject = "PHYSICS",
                score = 3,
                totalQuestions = 4,
                timeSpentSeconds = 250,
                status = "COMPLETED"
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = CloudSyncRepository(firestore)
        val crossUserResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getDppAttemptById(aliceUid, dppId)
        }
        assertTrue(crossUserResult.isFailure)
        val firestoreEx = crossUserResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun observeDppAttempts_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = CloudSyncRepository(firestore)

        var caughtException: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeDppAttempts("unauth_user_123").first()
            }
        } catch (e: Throwable) {
            caughtException = e
        }
        assertNotNull(caughtException)
        val firestoreException = checkNotNull(caughtException?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caughtException"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@arcstfug.com"
        const val BOB_EMAIL = "bob@arcstfug.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
