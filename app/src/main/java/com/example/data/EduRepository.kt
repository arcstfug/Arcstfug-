package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EduRepository {

    private val _courses = MutableStateFlow<List<CourseBatch>>(createInitialCourses())
    val courses: StateFlow<List<CourseBatch>> = _courses.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Arun Sharma",
            targetExam = "JEE Main & Advanced 2026",
            currentStreakDays = 14,
            totalDppsSolved = 28,
            overallAccuracy = 82,
            lecturesWatched = 42,
            studyHours = 56.5f,
            xp = 3450,
            badges = listOf(
                StudyBadge("b1", "14-Day Streak Fire", "Studied 14 days without skipping a day", "🔥", true),
                StudyBadge("b2", "DPP Ace", "Solved 25+ Daily Practice Problem sets", "🎯", true),
                StudyBadge("b3", "Physics Prodigy", "Scored 100% on Kinematics DPP", "⚡", true),
                StudyBadge("b4", "Night Owl Scholar", "Completed 5 lectures post 10 PM", "🦉", true),
                StudyBadge("b5", "Grandmaster", "Complete 50 DPPs and 10 Full Mock tests", "👑", false)
            )
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _mockTests = MutableStateFlow(createInitialMockTests())
    val mockTests: StateFlow<List<TestExam>> = _mockTests.asStateFlow()

    private val _doubts = MutableStateFlow(createInitialDoubts())
    val doubts: StateFlow<List<DoubtQuery>> = _doubts.asStateFlow()

    fun updateUserProfile(
        name: String,
        targetExam: String,
        classLevel: String,
        studentGoal: String,
        bio: String
    ) {
        _userProfile.update { current ->
            current.copy(
                name = name.trim().ifEmpty { current.name },
                targetExam = targetExam.trim().ifEmpty { current.targetExam },
                classLevel = classLevel.trim().ifEmpty { current.classLevel },
                studentGoal = studentGoal.trim().ifEmpty { current.studentGoal },
                bio = bio.trim().ifEmpty { current.bio }
            )
        }
    }

    fun enrollCourse(courseId: String) {
        _courses.update { list ->
            list.map { course ->
                if (course.id == courseId) course.copy(isEnrolled = !course.isEnrolled) else course
            }
        }
    }

    fun updateLectureProgress(lectureId: String, progressSec: Int, isCompleted: Boolean) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    lectures = chap.lectures.map { lec ->
                                        if (lec.id == lectureId) {
                                            lec.copy(progressSec = progressSec, isCompleted = isCompleted)
                                        } else lec
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
        if (isCompleted) {
            _userProfile.update { current ->
                current.copy(
                    lecturesWatched = current.lecturesWatched + 1,
                    studyHours = current.studyHours + 0.8f,
                    xp = current.xp + 50
                )
            }
        }
    }

    fun toggleLectureBookmark(lectureId: String) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    lectures = chap.lectures.map { lec ->
                                        if (lec.id == lectureId) {
                                            lec.copy(isBookmarked = !lec.isBookmarked)
                                        } else lec
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun submitDpp(dppId: String, userAnswers: Map<Int, Int>, timeSpentSeconds: Int) {
        var correctCount = 0
        var totalCount = 0

        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    dpps = chap.dpps.map { dpp ->
                                        if (dpp.id == dppId) {
                                            totalCount = dpp.questions.size
                                            correctCount = dpp.questions.mapIndexed { index, q ->
                                                if (userAnswers[index] == q.correctOptionIndex) 1 else 0
                                            }.sum()

                                            dpp.copy(
                                                status = DppStatus.COMPLETED,
                                                userAnswers = userAnswers,
                                                score = correctCount,
                                                timeSpentSeconds = timeSpentSeconds,
                                                completedAt = System.currentTimeMillis()
                                            )
                                        } else dpp
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }

        val accuracyScore = if (totalCount > 0) ((correctCount.toFloat() / totalCount) * 100).toInt() else 0
        _userProfile.update { current ->
            current.copy(
                totalDppsSolved = current.totalDppsSolved + 1,
                xp = current.xp + (correctCount * 30) + 40,
                overallAccuracy = ((current.overallAccuracy + accuracyScore) / 2)
            )
        }
    }

    fun reattemptDpp(dppId: String) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    dpps = chap.dpps.map { dpp ->
                                        if (dpp.id == dppId) {
                                            dpp.copy(
                                                status = DppStatus.PENDING,
                                                userAnswers = emptyMap(),
                                                score = 0,
                                                timeSpentSeconds = 0,
                                                completedAt = null
                                            )
                                        } else dpp
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun postDoubt(questionText: String, subject: SubjectType) {
        val newDoubt = DoubtQuery(
            id = "d_${System.currentTimeMillis()}",
            studentName = _userProfile.value.name,
            questionText = questionText,
            subject = subject,
            status = "Answered by AI Tutor",
            facultyReply = null,
            timestamp = "Just now"
        )
        _doubts.update { listOf(newDoubt) + it }
    }

    fun upsertDoubt(doubt: DoubtQuery) {
        _doubts.update { currentList ->
            val exists = currentList.any { it.id == doubt.id }
            if (exists) {
                currentList.map { if (it.id == doubt.id) doubt else it }
            } else {
                listOf(doubt) + currentList
            }
        }
    }

    fun addCustomDpp(dpp: Dpp) {
        _courses.update { courseList ->
            var added = false
            val updated = courseList.map { course ->
                if (!added && course.subjects.any { it.type == dpp.subject }) {
                    course.copy(
                        subjects = course.subjects.map { subj ->
                            if (!added && subj.type == dpp.subject && subj.chapters.isNotEmpty()) {
                                added = true
                                subj.copy(
                                    chapters = subj.chapters.mapIndexed { idx, chap ->
                                        if (idx == 0) chap.copy(dpps = listOf(dpp) + chap.dpps) else chap
                                    }
                                )
                            } else subj
                        }
                    )
                } else course
            }
            if (!added && updated.isNotEmpty()) {
                val firstCourse = updated.first()
                val firstSubj = firstCourse.subjects.firstOrNull()
                if (firstSubj != null && firstSubj.chapters.isNotEmpty()) {
                    updated.mapIndexed { cIdx, c ->
                        if (cIdx == 0) {
                            c.copy(
                                subjects = c.subjects.mapIndexed { sIdx, s ->
                                    if (sIdx == 0) {
                                        s.copy(
                                            chapters = s.chapters.mapIndexed { chIdx, ch ->
                                                if (chIdx == 0) ch.copy(dpps = listOf(dpp) + ch.dpps) else ch
                                            }
                                        )
                                    } else s
                                }
                            )
                        } else c
                    }
                } else updated
            } else updated
        }
    }

    // Helper functions to get aggregated items
    fun getAllLectures(): List<Lecture> {
        return _courses.value.flatMap { course ->
            course.subjects.flatMap { subj ->
                subj.chapters.flatMap { chap -> chap.lectures }
            }
        }
    }

    fun getAllDpps(): List<Dpp> {
        return _courses.value.flatMap { course ->
            course.subjects.flatMap { subj ->
                subj.chapters.flatMap { chap -> chap.dpps }
            }
        }
    }

    fun findDppById(dppId: String): Dpp? {
        return getAllDpps().find { it.id == dppId }
    }

    fun findLectureById(lectureId: String): Lecture? {
        return getAllLectures().find { it.id == lectureId }
    }

    private fun createInitialCourses(): List<CourseBatch> {
        return listOf(
            CourseBatch(
                id = "c_arjuna_jee",
                title = "Arjuna JEE Main & Advanced 2026",
                category = "JEE Main & Adv",
                tag = "Class 11th + 12th Comprehensive",
                rating = 4.9f,
                enrolledCount = "48,200+ Students",
                price = "₹3,999",
                originalPrice = "₹12,000",
                instructorTeam = "Prof. Verma (Physics) • Dr. Sneha (Chemistry) • Amit Sir (Maths)",
                description = "Master JEE Main & Advanced with India's top faculty. Includes daily live classes, recorded 4K lectures, structured daily DPPs with video solutions, all India mock tests, and 24/7 doubts discussion.",
                syllabusCoveragePercent = 42,
                isEnrolled = true,
                bannerColorHex = 0xFF4F46E5,
                subjects = listOf(
                    SubjectDetail(
                        type = SubjectType.PHYSICS,
                        chapters = listOf(
                            Chapter(
                                id = "phy_ch1",
                                subject = SubjectType.PHYSICS,
                                title = "Kinematics in 1D & 2D",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_phy_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 01: Frame of Reference, Distance & Displacement",
                                        lectureNumber = 1,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 52,
                                        progressSec = 3120,
                                        isCompleted = true,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Displacement is the shortest straight line vector connecting initial and final points.",
                                            "Distance >= |Displacement|. Equality holds ONLY during unidirectional straight line motion without turning.",
                                            "Average velocity v_avg = Δr / Δt. Instantaneous velocity v = dr/dt.",
                                            "Rate of change of speed is |a_tangential| = d|v|/dt, which is different from |dv/dt|."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Introduction & Vectors Recap"),
                                            LectureTimestamp(620, "10:20", "1D Motion & Position Vector"),
                                            LectureTimestamp(1540, "25:40", "Derivation of Calculus Equations"),
                                            LectureTimestamp(2480, "41:20", "JEE Advanced Multi-Step Question")
                                        ),
                                        dppId = "dpp_phy_1"
                                    ),
                                    Lecture(
                                        id = "lec_phy_2",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 02: Projectile Motion & Trajectory Equation",
                                        lectureNumber = 2,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 58,
                                        progressSec = 1800,
                                        isCompleted = false,
                                        isBookmarked = true,
                                        notesSummary = listOf(
                                            "Time of Flight: T = (2 u sin θ) / g",
                                            "Maximum Height: H_max = (u² sin² θ) / (2g)",
                                            "Horizontal Range: R = (u² sin 2θ) / g. Max range occurs at θ = 45°.",
                                            "Equation of trajectory: y = x tan θ - (g x²)/(2 u² cos² θ) = x tan θ (1 - x/R).",
                                            "Radius of curvature at apex: ρ = (u cos θ)² / g."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Horizontal & Vertical Independence"),
                                            LectureTimestamp(720, "12:00", "Derivation of Range & Max Height"),
                                            LectureTimestamp(1800, "30:00", "Trajectory Formula (y = x tan θ (1 - x/R))"),
                                            LectureTimestamp(2880, "48:00", "Inclined Plane Projectile Motion")
                                        ),
                                        dppId = "dpp_phy_2"
                                    ),
                                    Lecture(
                                        id = "lec_phy_3",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 03: Relative Velocity & Rain-Man Problems",
                                        lectureNumber = 3,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 64,
                                        progressSec = 0,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Relative velocity formula: v_AB = v_A - v_B.",
                                            "River-Swimmer: Shortest path when swimmer angles upstream at sin α = v_river / v_swimmer.",
                                            "Rain-Man: Velocity of rain wrt man v_rm = v_rain - v_man."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Concept of Relative Frame"),
                                            LectureTimestamp(840, "14:00", "Rain-Man Vector Diagrams"),
                                            LectureTimestamp(1920, "32:00", "River Boat Crossing Time vs Drift"),
                                            LectureTimestamp(3100, "51:40", "Shortest Distance of Approach")
                                        ),
                                        dppId = "dpp_phy_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_phy_1",
                                        number = 1,
                                        title = "DPP 01: Distance, Displacement & Acceleration",
                                        subject = SubjectType.PHYSICS,
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 12,
                                        status = DppStatus.COMPLETED,
                                        score = 3,
                                        timeSpentSeconds = 540,
                                        userAnswers = mapOf(0 to 1, 1 to 0, 2 to 2),
                                        questions = listOf(
                                            Question(
                                                id = "q_p1_1",
                                                questionText = "A particle moves along a semicircular path of radius R from point A to point B. What is the ratio of distance traveled to the magnitude of displacement?",
                                                options = listOf("2 : π", "π : 2", "1 : 1", "π : 1"),
                                                correctOptionIndex = 1,
                                                explanation = "Distance traveled along the semicircle is πR. The straight-line displacement between diameter endpoints is 2R. Hence the ratio is πR / 2R = π : 2.",
                                                hint = "Calculate arc length for distance, and diameter length for displacement.",
                                                topic = "Displacement vs Distance"
                                            ),
                                            Question(
                                                id = "q_p1_2",
                                                questionText = "The displacement x of a particle varies with time t as x = 4t² - 16t + 12. At what time does the particle momentarily come to rest?",
                                                options = listOf("t = 2 s", "t = 4 s", "t = 1 s", "t = 0.5 s"),
                                                correctOptionIndex = 0,
                                                explanation = "Velocity v = dx/dt = d(4t² - 16t + 12)/dt = 8t - 16. Setting v = 0 gives 8t = 16 => t = 2 seconds.",
                                                hint = "Differentiate displacement with respect to time to get velocity.",
                                                topic = "Instantaneous Velocity"
                                            ),
                                            Question(
                                                id = "q_p1_3",
                                                questionText = "A car accelerates uniformly from rest to 72 km/h in 10 seconds. What is the distance covered during this interval?",
                                                options = listOf("50 m", "200 m", "100 m", "150 m"),
                                                correctOptionIndex = 2,
                                                explanation = "Initial velocity u = 0. Final velocity v = 72 * (5/18) = 20 m/s. Acceleration a = (v - u)/t = 20/10 = 2 m/s². Distance s = ut + 0.5 a t² = 0 + 0.5 * 2 * 10² = 100 m.",
                                                hint = "First convert speed from km/h to m/s by multiplying by 5/18.",
                                                topic = "Equations of Motion"
                                            )
                                        )
                                    ),
                                    Dpp(
                                        id = "dpp_phy_2",
                                        number = 2,
                                        title = "DPP 02: Projectile Motion at Angles",
                                        subject = SubjectType.PHYSICS,
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_p2_1",
                                                questionText = "A projectile is thrown with initial speed u = 40 m/s at an angle θ = 30° with the horizontal (g = 10 m/s²). What is its maximum height achieved?",
                                                options = listOf("10 m", "20 m", "40 m", "15 m"),
                                                correctOptionIndex = 1,
                                                explanation = "H = (u² sin² θ) / (2g) = (40² * sin² 30°) / (2 * 10) = (1600 * 0.25) / 20 = 400 / 20 = 20 meters.",
                                                hint = "Use H = u_y² / (2g).",
                                                topic = "Projectile Maximum Height"
                                            ),
                                            Question(
                                                id = "q_p2_2",
                                                questionText = "For two complementary angles of projection θ and (90° - θ) with the same initial launch speed u, the horizontal ranges R1 and R2 satisfy:",
                                                options = listOf("R1 > R2", "R1 < R2", "R1 = R2", "R1 + R2 = u²/g"),
                                                correctOptionIndex = 2,
                                                explanation = "Range R = (u² sin 2θ)/g. For 90° - θ, sin(2(90° - θ)) = sin(180° - 2θ) = sin 2θ. Hence R1 = R2.",
                                                hint = "Recall the trigonometric identity sin(180° - 2θ) = sin 2θ.",
                                                topic = "Complementary Angles Range"
                                            ),
                                            Question(
                                                id = "q_p2_3",
                                                questionText = "At the highest point of its trajectory, what is the angle between the velocity vector and acceleration vector of a projectile?",
                                                options = listOf("0°", "45°", "90°", "180°"),
                                                correctOptionIndex = 2,
                                                explanation = "At the peak of trajectory, the vertical velocity component is zero, so velocity is purely horizontal (u cos θ î). Gravity is always vertically downward (-g ĵ). The angle between them is exactly 90°.",
                                                hint = "Consider the horizontal direction of motion and the downward gravity.",
                                                topic = "Trajectory Kinematics"
                                            )
                                        )
                                    )
                                )
                            )
                        ) + HcVermaPhysicsDpps.getHcVermaChapters()
                    ),
                    SubjectDetail(
                        type = SubjectType.CHEMISTRY,
                        chapters = listOf(
                            Chapter(
                                id = "chem_ch1",
                                subject = SubjectType.CHEMISTRY,
                                title = "Chemical Bonding & Molecular Structure",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_chem_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.CHEMISTRY,
                                        chapterId = "chem_ch1",
                                        chapterTitle = "Chemical Bonding & Molecular Structure",
                                        title = "Lec 01: VSEPR Theory, Hybridization & Shapes",
                                        lectureNumber = 1,
                                        instructor = "Dr. Sneha Sen",
                                        durationMinutes = 60,
                                        progressSec = 3600,
                                        isCompleted = true,
                                        isBookmarked = true,
                                        notesSummary = listOf(
                                            "Steric Number = (Number of σ bonds) + (Number of lone pairs on central atom).",
                                            "SN = 2: sp (Linear, 180°), SN = 3: sp² (Trigonal Planar, 120°).",
                                            "SN = 4: sp³ (Tetrahedral 109.5°, Pyramidal 107° for NH3, Bent 104.5° for H2O).",
                                            "Repulsion Order: Lone Pair-Lone Pair > Lone Pair-Bond Pair > Bond Pair-Bond Pair.",
                                            "SN = 5: sp³d (Trigonal Bipyramidal, lone pairs prefer equatorial positions)."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Lewis Structures Limitations"),
                                            LectureTimestamp(900, "15:00", "VSEPR Postulates & Repulsion"),
                                            LectureTimestamp(2100, "35:00", "Determining Hybridization via Steric Number"),
                                            LectureTimestamp(3000, "50:00", "Bent Rule & Axial Bond Lengths")
                                        ),
                                        dppId = "dpp_chem_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_chem_1",
                                        number = 1,
                                        title = "DPP 01: Hybridization & Molecular Geometries",
                                        subject = SubjectType.CHEMISTRY,
                                        chapterTitle = "Chemical Bonding & Molecular Structure",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_c1_1",
                                                questionText = "What is the hybridization and molecular geometry of Xenon Tetrafluoride (XeF4)?",
                                                options = listOf("sp³d, See-saw", "sp³d², Square Planar", "sp³d², Octahedral", "sp³, Tetrahedral"),
                                                correctOptionIndex = 1,
                                                explanation = "In XeF4, Xenon has 8 valence electrons. 4 form σ-bonds with F atoms, leaving 4 non-bonding electrons (2 lone pairs). Steric number = 4 + 2 = 6 (sp³d²). With 2 lone pairs opposite each other, the shape is Square Planar.",
                                                hint = "Count total valence electrons of Xe (8) and subtract bonding pairs.",
                                                topic = "VSEPR and Noble Gas Compounds"
                                            ),
                                            Question(
                                                id = "q_c1_2",
                                                questionText = "Which of the following molecules possesses a zero dipole moment (non-polar)?",
                                                options = listOf("NH3", "BF3", "H2O", "SO2"),
                                                correctOptionIndex = 1,
                                                explanation = "BF3 has trigonal planar geometry with 120° bond angles. The three identical B-F dipole vectors cancel each other out symmetrically, resulting in a net dipole moment μ = 0.",
                                                hint = "Check for symmetrical cancellation in planar structures.",
                                                topic = "Dipole Moment"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    SubjectDetail(
                        type = SubjectType.MATHEMATICS,
                        chapters = listOf(
                            Chapter(
                                id = "math_ch1",
                                subject = SubjectType.MATHEMATICS,
                                title = "Quadratic Equations & Complex Roots",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_math_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.MATHEMATICS,
                                        chapterId = "math_ch1",
                                        chapterTitle = "Quadratic Equations & Complex Roots",
                                        title = "Lec 01: Relations between Roots & Coefficients",
                                        lectureNumber = 1,
                                        instructor = "Amit Sir",
                                        durationMinutes = 55,
                                        progressSec = 1200,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "For ax² + bx + c = 0, Sum of roots (α + β) = -b/a, Product (αβ) = c/a.",
                                            "Discriminant D = b² - 4ac determines nature of roots (D > 0 real distinct, D = 0 equal, D < 0 imaginary conjugate pairs).",
                                            "Formation of equation: x² - (Sum)x + (Product) = 0.",
                                            "Condition for common root between two quadratics."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Quadratic Basics & Vieta Formulas"),
                                            LectureTimestamp(780, "13:00", "Symmetric Expressions of Roots (α³ + β³)"),
                                            LectureTimestamp(1860, "31:00", "Location of Roots & Graph Transformations"),
                                            LectureTimestamp(2700, "45:00", "JEE Advanced 2024 Past Year Problem")
                                        ),
                                        dppId = "dpp_math_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_math_1",
                                        number = 1,
                                        title = "DPP 01: Roots, Vieta & Quadratic Graphs",
                                        subject = SubjectType.MATHEMATICS,
                                        chapterTitle = "Quadratic Equations & Complex Roots",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_m1_1",
                                                questionText = "If α and β are the roots of the equation x² - 5x + 6 = 0, find the value of α² + β².",
                                                options = listOf("13", "19", "25", "12"),
                                                correctOptionIndex = 0,
                                                explanation = "Here a=1, b=-5, c=6. α + β = 5 and αβ = 6. Now α² + β² = (α + β)² - 2αβ = (5)² - 2(6) = 25 - 12 = 13.",
                                                hint = "Express α² + β² in terms of (α + β) and αβ.",
                                                topic = "Symmetric Roots"
                                            ),
                                            Question(
                                                id = "q_m1_2",
                                                questionText = "For what value of k does the quadratic equation 4x² + kx + 9 = 0 have real and equal roots?",
                                                options = listOf("± 6", "± 12", "± 36", "± 18"),
                                                correctOptionIndex = 1,
                                                explanation = "For real and equal roots, discriminant D = b² - 4ac = 0. Here k² - 4(4)(9) = 0 => k² - 144 = 0 => k = ±12.",
                                                hint = "Set the discriminant D = 0.",
                                                topic = "Nature of Roots"
                                            )
                                        )
                                    )
                                )
                            )
                        ) + CengageAlgebraDpps.getCengageChapters()
                    )
                )
            ),
            CourseBatch(
                id = "c_lakshya_neet",
                title = "Lakshya NEET-UG 2025/26",
                category = "NEET-UG",
                tag = "Medical Rankers Target",
                rating = 4.8f,
                enrolledCount = "39,500+ Students",
                price = "₹3,499",
                originalPrice = "₹10,000",
                instructorTeam = "Dr. R.K. Mishra (Bio) • Dr. Sneha (Chem) • Prof. Verma (Phy)",
                description = "Strictly NCERT-aligned medical entrance course. Detailed 3D biological animations, line-by-line NCERT breakdowns, assertion-reason drills, and 100+ topic-wise DPP sets.",
                syllabusCoveragePercent = 65,
                isEnrolled = false,
                bannerColorHex = 0xFF0284C7,
                subjects = listOf(
                    SubjectDetail(
                        type = SubjectType.BIOLOGY,
                        chapters = listOf(
                            Chapter(
                                id = "bio_ch1",
                                subject = SubjectType.BIOLOGY,
                                title = "Cell Cycle & Cell Division",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_bio_1",
                                        courseId = "c_lakshya_neet",
                                        subject = SubjectType.BIOLOGY,
                                        chapterId = "bio_ch1",
                                        chapterTitle = "Cell Cycle & Cell Division",
                                        title = "Lec 01: Mitosis, Interphase (G1, S, G2) & Karyokinesis",
                                        lectureNumber = 1,
                                        instructor = "Dr. R.K. Mishra",
                                        durationMinutes = 48,
                                        progressSec = 0,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Interphase constitutes >95% of cell cycle duration.",
                                            "S Phase (Synthesis): DNA replication occurs, amount of DNA doubles (2C to 4C) but chromosome number remains 2n.",
                                            "G0 Phase (Quiescent stage): Cells remain metabolically active but do not proliferate (e.g. heart cells).",
                                            "Colchicine inhibits spindle fiber formation at Metaphase."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Cell Division Significance"),
                                            LectureTimestamp(600, "10:00", "Interphase Stages Breakdown"),
                                            LectureTimestamp(1500, "25:00", "DNA Content vs Chromosome Count"),
                                            LectureTimestamp(2300, "38:20", "Mitosis Prophase & Metaphase Plates")
                                        ),
                                        dppId = "dpp_bio_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_bio_1",
                                        number = 1,
                                        title = "DPP 01: Cell Cycle Stages & DNA Replication",
                                        subject = SubjectType.BIOLOGY,
                                        chapterTitle = "Cell Cycle & Cell Division",
                                        courseId = "c_lakshya_neet",
                                        timeLimitMinutes = 10,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_b1_1",
                                                questionText = "During which phase of the cell cycle does DNA replication and duplication of centriole occur in animal cells?",
                                                options = listOf("G1 phase", "S phase", "G2 phase", "M phase"),
                                                correctOptionIndex = 1,
                                                explanation = "In the S (synthesis) phase, DNA replication occurs inside the nucleus, and the centriole duplicates in the cytoplasm.",
                                                hint = "S stands for synthesis of genetic material.",
                                                topic = "Interphase S Phase"
                                            ),
                                            Question(
                                                id = "q_b1_2",
                                                questionText = "Which stage of mitosis is best suited to study the morphology, size, and shape of chromosomes?",
                                                options = listOf("Prophase", "Metaphase", "Anaphase", "Telophase"),
                                                correctOptionIndex = 1,
                                                explanation = "At metaphase, chromosomes are condensed to the maximum extent and aligned along the equatorial metaphase plate, making their morphology easiest to examine under a microscope.",
                                                hint = "Look for chromosomes arranged along the equatorial plate.",
                                                topic = "Metaphase Characteristics"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            CourseBatch(
                id = "c_udaan_10th",
                title = "Udaan Class 10th Board Booster",
                category = "Class 10 Board",
                tag = "100/100 Board Target",
                rating = 4.9f,
                enrolledCount = "62,000+ Students",
                price = "₹1,499",
                originalPrice = "₹5,000",
                instructorTeam = "Master Faculty Panel (Science & Maths)",
                description = "Complete CBSE & State Board syllabus preparation with sample question papers, formula sheets, chapter revision marathons, and weekly subjective tests.",
                syllabusCoveragePercent = 80,
                isEnrolled = false,
                bannerColorHex = 0xFF059669,
                subjects = emptyList()
            )
        )
    }

    private fun createInitialMockTests(): List<TestExam> {
        return listOf(
            TestExam(
                id = "test_0",
                title = "All-India Test Series (Mission JEET Portal)",
                pattern = "Live Online Test Series • DeltaStudy",
                questionsCount = 75,
                durationMinutes = 180,
                totalMarks = 300,
                status = "Live Portal",
                scheduledDate = "Open Now",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_1",
                title = "ARCSTFUG All-India JEE Main Mock 01",
                pattern = "JEE Main (NTA Pattern)",
                questionsCount = 75,
                durationMinutes = 180,
                totalMarks = 300,
                status = "Live Now",
                scheduledDate = "Active Today",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_2",
                title = "NEET Speed Test - Full Class 11th Syllabus",
                pattern = "NEET-UG (200 Questions)",
                questionsCount = 200,
                durationMinutes = 200,
                totalMarks = 720,
                status = "Upcoming",
                scheduledDate = "Sunday 10:00 AM",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_3",
                title = "Kinematics & Newton's Laws Chapter Test",
                pattern = "Subject Minor Test",
                questionsCount = 30,
                durationMinutes = 60,
                totalMarks = 120,
                status = "Available Anytime",
                scheduledDate = "Self-Paced",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            )
        )
    }

    private fun createInitialDoubts(): List<DoubtQuery> {
        return listOf(
            DoubtQuery(
                id = "d1",
                studentName = "Rohan Gupta",
                questionText = "Why does a body rolling down an inclined plane have both rotational and translational kinetic energy?",
                subject = SubjectType.PHYSICS,
                status = "Answered by Faculty",
                facultyReply = "When a body rolls without slipping, every point undergoes translation of center of mass (1/2 M v_cm²) and rotation about CM (1/2 I_cm ω²). Since v_cm = Rω, Total KE = 1/2 M v_cm² (1 + k²/R²).",
                timestamp = "2 hours ago"
            ),
            DoubtQuery(
                id = "d2",
                studentName = "Pooja Verma",
                questionText = "How do we distinguish between sp³d hybrid shapes like See-saw, T-shaped, and Linear?",
                subject = SubjectType.CHEMISTRY,
                status = "Answered by Faculty",
                facultyReply = "In sp³d, lone pairs strictly occupy equatorial positions to minimize 90° LP-BP repulsions: 1 LP -> See-saw (SF4), 2 LP -> T-shaped (ClF3), 3 LP -> Linear (XeF2).",
                timestamp = "5 hours ago"
            )
        )
    }
}
