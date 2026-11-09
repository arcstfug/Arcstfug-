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
    val subjects: List<SubjectDetail>,
    val bannerDrawableRes: Int? = null,
    val availablePlans: List<String> = emptyList(),
    val featureBadge: String? = null,
    val storageBucketUrl: String? = null
)

object ExternalStudyLinks {
    const val ARCSTFUG_WEB_PORTAL_URL = "https://arcstfug-app.netlify.app/"
    const val PHYSICS_DRIVE_URL = "https://drive.google.com/drive/folders/1-Gii-7kSrT4Do8x6TkO3mhy4IxeP2o57"
    const val ALL_INDIA_TEST_SERIES_URL = "https://deltastudy.fun/missionjeet/251"
    const val FIREBASE_STORAGE_BUCKET = "gs://gen-lang-client-0387332814.appspot.com"
    const val FIREBASE_STORAGE_CONSOLE_URL = "https://console.firebase.google.com/project/gen-lang-client-0387332814/storage"

    // ⚡ JEE PHYSICS VAULT | DROP #01
    const val DC_PANDEY_DRIVE_URL = "https://drive.google.com/drive/folders/1yK-e7N86xzFWaZHkuHXwQvBqRE985dQk"
    const val HC_VERMA_DRIVE_URL = "https://drive.google.com/drive/folders/1oj32-UtrZ7d9fbqLKfQiMUIcR78MLMJG"
    const val IRODOV_DRIVE_URL = "https://drive.google.com/drive/folders/13dIBfy8JfFQbsSdDwrrNUgC-M3N_Dnmx"

    // 📚 EJEE Books PDF – Google Drive Links (All in One)
    const val IOC_OM_PANDEY_URL = "https://drive.google.com/file/d/17bxjAdU-19r89ltfi8QF3ZXzX1kgzUUA/view?usp=drivesdk"
    const val OC_ROHIT_AGARWAL_URL = "https://drive.google.com/file/d/1E-o1T40i1Ye0jR2Bokw5evMOVZNi_p0W/view?usp=drivesdk"
    const val PHYSICAL_CHEM_URL = "https://drive.google.com/file/d/12q2sssGl1BBKxrJAPnk49yUhSHCYlRZJ/view?usp=drivesdk"
    const val MATHS_SACHIN_SIR_URL = "https://drive.google.com/file/d/1Cfz8smlM6J-9FHOFK7UKnMtphpmVJvEV/view?usp=drivesdk"
    const val SKC_CLASS_11_BOOK_URL = "https://drive.google.com/file/d/1oUNCzu35kblPyq46b7tf0R58ivlYDbqC/view?usp=drivesdk"

    // Allen Modules & Study Material
    const val ALLEN_MODULES_URL = "https://drive.google.com/drive/folders/1SynXg8rH1-RKSSElHvKDlNCorX27CyB1"
    const val ALLEN_MATERIAL_URL = "https://drive.google.com/drive/folders/1GO-X-fMuEH6grA86t7_jxUUfqC8NZL60"

    // 📚 JEE Advanced Previous Year Question Papers (2010–2025)
    const val JEE_ADV_PYQ_2010_2025_URL = "https://drive.google.com/drive/folders/10uT5xt16uO3DViOxIB5x3HpfLr1cCimy"
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

