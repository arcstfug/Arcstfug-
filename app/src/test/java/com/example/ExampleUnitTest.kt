package com.example

import com.example.data.EduRepository
import com.example.model.DppStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testEduRepositoryDataAndDppSubmission() {
    val repo = EduRepository()
    val courses = repo.courses.value
    assertEquals(3, courses.size)

    val dpps = repo.getAllDpps()
    assertNotNull(dpps)
    val firstDpp = dpps.first()

    // Submit DPP test answers
    repo.submitDpp(firstDpp.id, mapOf(0 to 1, 1 to 0, 2 to 2), 240)
    val submittedDpp = repo.findDppById(firstDpp.id)
    assertEquals(DppStatus.COMPLETED, submittedDpp?.status)
    assertEquals(3, submittedDpp?.score)

    // Reattempt DPP
    repo.reattemptDpp(firstDpp.id)
    val reattemptedDpp = repo.findDppById(firstDpp.id)
    assertEquals(DppStatus.PENDING, reattemptedDpp?.status)
    assertEquals(0, reattemptedDpp?.score)
  }
}
