package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class StudentProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "Arun Sharma",
    val email: String? = null,
    val targetExam: String = "JEE Main & Advanced 2026",
    val classLevel: String = "Class 12th",
    val studentGoal: String = "Aim: Top 100 AIR",
    val bio: String = "ARCSTFUG Scholar • Consistent Daily Problem Solver",
    val currentStreakDays: Long = 14L,
    val totalDppsSolved: Long = 28L,
    val overallAccuracy: Long = 82L,
    val lecturesWatched: Long = 42L,
    val studyHours: Double = 56.5,
    val xp: Long = 3450L,
    val enrolledCourseIds: List<String> = listOf("c_arjuna_jee"),
    val bookmarkedLectureIds: List<String> = emptyList(),
    val completedLectureIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DppAttemptDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dppId: String = "",
    val title: String = "",
    val subject: String = "PHYSICS",
    val score: Long = 0L,
    val totalQuestions: Long = 1L,
    val timeSpentSeconds: Long = 0L,
    val status: String = "COMPLETED",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudentDoubtDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val questionText: String = "",
    val subject: String = "PHYSICS",
    val status: String = "Solved by Gemini AI Tutor ✨",
    val facultyReply: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
