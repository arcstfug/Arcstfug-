package com.example.data

import android.content.Context
import com.example.R
import com.example.model.DppAttemptDoc
import com.example.model.StudentDoubtDoc
import com.example.model.StudentProfileDoc
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CloudSyncRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth get() = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // -------------------------------------------------------------------------
    // 1. Student Profile Operations (/users/{userId})
    // -------------------------------------------------------------------------

    fun observeStudentProfile(userId: String): Flow<StudentProfileDoc?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(StudentProfileDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null) {
                    handleFirestoreError(exceptionToLog, OperationType.GET, path)
                }
                throw error
            }
    }

    suspend fun getStudentProfileById(targetUserId: String): Result<StudentProfileDoc?> {
        val docRef = db.collection("users").document(targetUserId)
        return try {
            val snapshot = docRef.get().await()
            val profile = if (snapshot.exists()) {
                snapshot.toObject(StudentProfileDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else {
                null
            }
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun createInitialProfileIfMissing(
        name: String,
        email: String?,
        targetExam: String = "JEE Main & Advanced 2026",
        classLevel: String = "Class 12th",
        studentGoal: String = "Aim: Top 100 AIR",
        bio: String = "ARCSTFUG Scholar • Consistent Daily Problem Solver"
    ): Result<String> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val existingSnap = docRef.get().await()
            if (!existingSnap.exists()) {
                val payload = mapOf<String, Any?>(
                    "userId" to uid,
                    "name" to name.trim().ifEmpty { "ARCSTFUG Student" }.take(100),
                    "email" to email?.take(200),
                    "targetExam" to targetExam.take(100),
                    "classLevel" to classLevel.take(50),
                    "studentGoal" to studentGoal.take(150),
                    "bio" to bio.take(300),
                    "currentStreakDays" to 14L,
                    "totalDppsSolved" to 28L,
                    "overallAccuracy" to 82L,
                    "lecturesWatched" to 42L,
                    "studyHours" to 56.5,
                    "xp" to 3450L,
                    "enrolledCourseIds" to listOf("c_arjuna_jee"),
                    "bookmarkedLectureIds" to emptyList<String>(),
                    "completedLectureIds" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(uid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateStudentProfile(
        name: String,
        targetExam: String,
        classLevel: String,
        studentGoal: String,
        bio: String,
        currentStreakDays: Int,
        totalDppsSolved: Int,
        overallAccuracy: Int,
        lecturesWatched: Int,
        studyHours: Float,
        xp: Int,
        enrolledCourseIds: List<String>,
        bookmarkedLectureIds: List<String>,
        completedLectureIds: List<String>
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updates = mapOf<String, Any>(
                    "name" to name.trim().ifEmpty { "ARCSTFUG Student" }.take(100),
                    "targetExam" to targetExam.trim().ifEmpty { "JEE Main & Advanced 2026" }.take(100),
                    "classLevel" to classLevel.trim().ifEmpty { "Class 12th" }.take(50),
                    "studentGoal" to studentGoal.trim().ifEmpty { "Aim: Top 100 AIR" }.take(150),
                    "bio" to bio.trim().ifEmpty { "ARCSTFUG Scholar" }.take(300),
                    "currentStreakDays" to currentStreakDays.toLong().coerceAtLeast(0L),
                    "totalDppsSolved" to totalDppsSolved.toLong().coerceAtLeast(0L),
                    "overallAccuracy" to overallAccuracy.toLong().coerceIn(0L, 100L),
                    "lecturesWatched" to lecturesWatched.toLong().coerceAtLeast(0L),
                    "studyHours" to studyHours.toDouble().coerceAtLeast(0.0),
                    "xp" to xp.toLong().coerceAtLeast(0L),
                    "enrolledCourseIds" to enrolledCourseIds.take(50),
                    "bookmarkedLectureIds" to bookmarkedLectureIds.take(200),
                    "completedLectureIds" to completedLectureIds.take(500),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            } else {
                val payload = mapOf<String, Any?>(
                    "userId" to uid,
                    "name" to name.trim().ifEmpty { "ARCSTFUG Student" }.take(100),
                    "email" to auth.currentUser?.email?.take(200),
                    "targetExam" to targetExam.trim().ifEmpty { "JEE Main & Advanced 2026" }.take(100),
                    "classLevel" to classLevel.trim().ifEmpty { "Class 12th" }.take(50),
                    "studentGoal" to studentGoal.trim().ifEmpty { "Aim: Top 100 AIR" }.take(150),
                    "bio" to bio.trim().ifEmpty { "ARCSTFUG Scholar" }.take(300),
                    "currentStreakDays" to currentStreakDays.toLong().coerceAtLeast(0L),
                    "totalDppsSolved" to totalDppsSolved.toLong().coerceAtLeast(0L),
                    "overallAccuracy" to overallAccuracy.toLong().coerceIn(0L, 100L),
                    "lecturesWatched" to lecturesWatched.toLong().coerceAtLeast(0L),
                    "studyHours" to studyHours.toDouble().coerceAtLeast(0.0),
                    "xp" to xp.toLong().coerceAtLeast(0L),
                    "enrolledCourseIds" to enrolledCourseIds.take(50),
                    "bookmarkedLectureIds" to bookmarkedLectureIds.take(200),
                    "completedLectureIds" to completedLectureIds.take(500),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 2. DPP Attempt Operations (/users/{userId}/dpp_attempts/{dppId})
    // -------------------------------------------------------------------------

    fun observeDppAttempts(userId: String): Flow<List<DppAttemptDoc>> {
        val path = "users/$userId/dpp_attempts"
        return db.collection("users").document(userId)
            .collection("dpp_attempts")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(DppAttemptDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun saveDppAttempt(
        dppId: String,
        title: String,
        subject: String,
        score: Int,
        totalQuestions: Int,
        timeSpentSeconds: Int,
        status: String
    ): Result<String> {
        val uid = requireUserId()
        val safeId = dppId.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_").take(100).ifEmpty { "dpp_default" }
        val docRef = db.collection("users").document(uid).collection("dpp_attempts").document(safeId)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updates = mapOf<String, Any>(
                    "title" to title.trim().ifEmpty { "DPP Practice" }.take(200),
                    "subject" to subject.trim().ifEmpty { "PHYSICS" }.take(50),
                    "score" to score.toLong().coerceIn(0L, 1000L),
                    "totalQuestions" to totalQuestions.toLong().coerceIn(1L, 500L),
                    "timeSpentSeconds" to timeSpentSeconds.toLong().coerceIn(0L, 86400L),
                    "status" to if (status == "PENDING") "PENDING" else "COMPLETED",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            } else {
                val payload = mapOf<String, Any>(
                    "userId" to uid,
                    "dppId" to safeId,
                    "title" to title.trim().ifEmpty { "DPP Practice" }.take(200),
                    "subject" to subject.trim().ifEmpty { "PHYSICS" }.take(50),
                    "score" to score.toLong().coerceIn(0L, 1000L),
                    "totalQuestions" to totalQuestions.toLong().coerceIn(1L, 500L),
                    "timeSpentSeconds" to timeSpentSeconds.toLong().coerceIn(0L, 86400L),
                    "status" to if (status == "PENDING") "PENDING" else "COMPLETED",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(safeId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getDppAttemptById(ownerUserId: String, dppId: String): Result<DppAttemptDoc?> {
        val docRef = db.collection("users").document(ownerUserId).collection("dpp_attempts").document(dppId)
        return try {
            val snap = docRef.get().await()
            val item = if (snap.exists()) {
                snap.toObject(DppAttemptDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else {
                null
            }
            Result.success(item)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 3. Student Doubts Operations (/users/{userId}/doubts/{doubtId})
    // -------------------------------------------------------------------------

    fun observeStudentDoubts(userId: String): Flow<List<StudentDoubtDoc>> {
        val path = "users/$userId/doubts"
        return db.collection("users").document(userId)
            .collection("doubts")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(StudentDoubtDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun saveStudentDoubt(
        doubtId: String,
        studentName: String,
        questionText: String,
        subject: String,
        status: String,
        facultyReply: String?
    ): Result<String> {
        val uid = requireUserId()
        val safeId = doubtId.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_").take(100).ifEmpty { "d_default" }
        val docRef = db.collection("users").document(uid).collection("doubts").document(safeId)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updates = mapOf<String, Any?>(
                    "studentName" to studentName.trim().ifEmpty { "Student" }.take(100),
                    "questionText" to questionText.trim().ifEmpty { "Doubt question" }.take(2000),
                    "subject" to subject.trim().ifEmpty { "PHYSICS" }.take(50),
                    "status" to status.trim().ifEmpty { "Solved" }.take(100),
                    "facultyReply" to facultyReply?.take(15000),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.update(updates).await()
            } else {
                val payload = mapOf<String, Any?>(
                    "userId" to uid,
                    "studentName" to studentName.trim().ifEmpty { "Student" }.take(100),
                    "questionText" to questionText.trim().ifEmpty { "Doubt question" }.take(2000),
                    "subject" to subject.trim().ifEmpty { "PHYSICS" }.take(50),
                    "status" to status.trim().ifEmpty { "Solved" }.take(100),
                    "facultyReply" to facultyReply?.take(15000),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(safeId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
