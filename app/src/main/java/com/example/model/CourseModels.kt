package com.example.model

enum class SubjectType(val displayName: String) {
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    MATHEMATICS("Mathematics"),
    BIOLOGY("Biology")
}

data class Question(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val hint: String,
    val topic: String,
    val correctOptionIndices: Set<Int> = setOf(correctOptionIndex),
    val isMultiSelect: Boolean = correctOptionIndices.size > 1
)

enum class DppStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class Dpp(
    val id: String,
    val number: Int,
    val title: String,
    val subject: SubjectType,
    val chapterTitle: String,
    val courseId: String,
    val questions: List<Question>,
    val timeLimitMinutes: Int = 15,
    val status: DppStatus = DppStatus.PENDING,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> chosenOptionIndex
    val userMultiAnswers: Map<Int, Set<Int>> = emptyMap(), // questionIndex -> chosenOptionIndices
    val score: Int = 0,
    val timeSpentSeconds: Int = 0,
    val completedAt: Long? = null,
    val sourceBook: String? = null
) {
    fun selectedOptionsFor(questionIndex: Int): Set<Int> {
        val multi = userMultiAnswers[questionIndex]
        if (!multi.isNullOrEmpty()) return multi
        val single = userAnswers[questionIndex]
        return if (single != null) setOf(single) else emptySet()
    }
}

data class LectureTimestamp(
    val timeSec: Int,
    val timeLabel: String,
    val title: String
)

data class Lecture(
    val id: String,
    val courseId: String,
    val subject: SubjectType,
    val chapterId: String,
    val chapterTitle: String,
    val title: String,
    val lectureNumber: Int,
    val instructor: String,
    val durationMinutes: Int,
    val progressSec: Int = 0,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val notesSummary: List<String> = emptyList(),
    val timestamps: List<LectureTimestamp> = emptyList(),
    val dppId: String? = null,
    val videoUrl: String? = null
)

data class Chapter(
    val id: String,
    val subject: SubjectType,
    val title: String,
    val order: Int,
    val lectures: List<Lecture>,
    val dpps: List<Dpp>
)

data class SubjectDetail(
    val type: SubjectType,
    val chapters: List<Chapter>
)

data class CourseBatch(
    val id: String,
    val title: String,
    val category: String,
    val tag: String,
    val rating: Float,
    val enrolledCount: String,
    val price: String,
    val originalPrice: String,
    val instructorTeam: String,
    val description: String,
    val syllabusCoveragePercent: Int,
    val isEnrolled: Boolean,
    val bannerColorHex: Long,
    val subjects: List<SubjectDetail>
)

object ExternalStudyLinks {
    const val PHYSICS_DRIVE_URL = "https://drive.google.com/drive/folders/1-Gii-7kSrT4Do8x6TkO3mhy4IxeP2o57"
    const val ALL_INDIA_TEST_SERIES_URL = "https://deltastudy.fun/missionjeet/251"
}

data class StudyMaterial(
    val id: String,
    val title: String,
    val subject: SubjectType,
    val chapterTitle: String,
    val courseId: String = "c_arjuna_jee",
    val category: String,
    val description: String,
    val resourceUrl: String,
    val authorName: String = "ARCSTFUG Faculty",
    val uploadedAtLabel: String = "Official Vault",
    val isTeacherUpload: Boolean = false
)

data class TeacherUploadItem(
    val id: String,
    val authorId: String,
    val authorName: String,
    val uploadType: String, // "VIDEO", "DPP", "MATERIAL"
    val title: String,
    val subject: SubjectType,
    val chapterTitle: String,
    val courseId: String,
    val resourceUrl: String,
    val materialCategory: String,
    val description: String,
    val durationMinutes: Int
)

data class TestExam(
    val id: String,
    val title: String,
    val pattern: String,
    val questionsCount: Int,
    val durationMinutes: Int,
    val totalMarks: Int,
    val status: String,
    val scheduledDate: String,
    val externalUrl: String? = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
)

data class StudyBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

data class UserProfile(
    val name: String,
    val targetExam: String,
    val classLevel: String = "Class 12th",
    val studentGoal: String = "Aim: Top 100 AIR",
    val bio: String = "ARCSTFUG Scholar • Consistent Daily Problem Solver",
    val currentStreakDays: Int,
    val totalDppsSolved: Int,
    val overallAccuracy: Int,
    val lecturesWatched: Int,
    val studyHours: Float,
    val xp: Int,
    val badges: List<StudyBadge>
)

data class DoubtQuery(
    val id: String,
    val studentName: String,
    val questionText: String,
    val subject: SubjectType,
    val status: String,
    val facultyReply: String?,
    val timestamp: String
)

