package com.example.data

import com.example.model.*

/**
 * Independent Chapter-wise Daily Practice Problems (DPP) sets derived from
 * "Concepts of Physics - Volume 1" by Dr. H. C. Verma, PhD (IIT Kanpur).
 *
 * Distinct Chapters:
 * Chapter 1: Introduction to Physics (Units, Dimensions, Dimensional Analysis)
 * Chapter 2: Physics and Mathematics (Vectors, Dot & Cross Product, Calculus)
 * Chapter 3: Rest and Motion: Kinematics (1D Motion, Projectiles, Relative Velocity)
 * Chapter 4: The Forces (Fundamental Forces, Tension, Normal Force)
 * Chapter 5: Newton's Laws of Motion (Inertial Frames, Free Body Diagrams, Pulleys)
 * Chapter 6: Friction (Static, Kinetic, Limiting Friction, Two-Block Systems)
 * Chapter 7: Circular Motion (Centripetal Acceleration, Banking of Roads, Vertical Circle)
 * Chapter 8: Work and Energy (Work-Energy Theorem, Conservative Forces, Potential Energy)
 * Chapter 9: Centre of Mass, Linear Momentum, Collision (CM, Impulse, Collisions)
 * Chapter 10: Rotational Mechanics (Torque, Moment of Inertia, Angular Momentum, Pure Rolling)
 * Chapter 11: Gravitation (Kepler's Laws, Gravitational Potential, Satellites, Escape Speed)
 * Chapter 12: Simple Harmonic Motion (SHM Equations, Energy, Pendulums, Oscillations)
 * Chapter 13: Fluid Mechanics (Pascal's Principle, Archimedes' Principle, Bernoulli's Theorem)
 * Chapter 14: Mechanical Properties of Matter (Stress-Strain, Young's Modulus, Surface Tension, Viscosity)
 * Chapter 15: Wave Motion and Waves on a String (Wave Equation, Standing Waves, Sonometer)
 * Chapter 16: Sound Waves (Speed of Sound, Organ Pipes, Beats, Doppler Effect)
 * Chapter 17: Light Waves (Wave Optics, Huygens' Principle, YDSE, Polarization)
 * Chapter 18: Geometrical Optics (Mirrors, Lenses, Refraction at Spherical Surfaces, Prisms)
 * Chapter 19: Optical Instruments (Microscopes, Telescopes, Defects of Vision)
 * Chapter 20: Dispersion, Spectra & Photometry (Dispersive Power, Luminous Flux, Illuminance)
 */
object HcVermaPhysicsDpps {

    const val HCV_BOOK_NAME = "Concepts of Physics (H.C. Verma Vol 1)"

    fun getHcVermaChapters(): List<Chapter> {
        return listOf(
            createChapter1(),
            createChapter2(),
            createChapter3(),
            createChapter4(),
            createChapter5(),
            createChapter6(),
            createChapter7(),
            createChapter8(),
            createChapter9(),
            createChapter10(),
            createChapter11(),
            createChapter12(),
            createChapter13(),
            createChapter14(),
            createChapter15(),
            createChapter16(),
            createChapter17(),
            createChapter18(),
            createChapter19(),
            createChapter20()
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 1: Introduction to Physics
    // -------------------------------------------------------------
    private fun createChapter1(): Chapter {
        return Chapter(
            id = "phy_hcv_ch1",
            subject = SubjectType.PHYSICS,
            title = "Chapter 1: Introduction to Physics",
            order = 1,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch1",
                    chapterTitle = "Chapter 1: Introduction to Physics",
                    title = "Lec 01: Dimensions, Units & Error Analysis",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 55,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Fundamental quantities in SI: Length, Mass, Time, Electric Current, Temperature, Amount of Substance, Luminous Intensity.",
                        "Principle of Homogeneity: In any physical equation, the dimensions of each term on both sides must be identical.",
                        "Dimensionless quantities can still have units (e.g. angle in radians, solid angle in steradians), but quantities without units are always dimensionless.",
                        "Dimensional analysis cannot determine dimensionless numerical constants like 2π or 1/2."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Base and Derived Physical Quantities"),
                        LectureTimestamp(720, "12:00", "Dimensional Formulas of Universal Constants (G, h, ε₀)"),
                        LectureTimestamp(1800, "30:00", "Principle of Homogeneity & Limitations"),
                        LectureTimestamp(2700, "45:00", "HCV Objective I & II Discussion")
                    ),
                    dppId = "dpp_hcv_ch1"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch1",
                    number = 1,
                    title = "DPP 01: Units, Dimensions & Measurement",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 1: Introduction to Physics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch1_q1",
                            questionText = "Which of the following sets cannot enter into the list of fundamental quantities in any system of units?",
                            options = listOf(
                                "length, mass and velocity",
                                "length, time and velocity",
                                "mass, time and velocity",
                                "length, time and mass"
                            ),
                            correctOptionIndex = 1,
                            explanation = "Fundamental quantities must be mutually independent. Since velocity = length / time, velocity can be derived directly from length and time. Therefore, length, time and velocity are linearly dependent and cannot simultaneously form a set of fundamental quantities.",
                            hint = "Look for the triplet where one quantity is directly expressed as a combination of the other two.",
                            topic = "Fundamental & Derived Units"
                        ),
                        Question(
                            id = "hcv_ch1_q2",
                            questionText = "The dimensional formula of universal gravitational constant G is:",
                            options = listOf(
                                "[M⁻¹ L³ T⁻²]",
                                "[M L² T⁻²]",
                                "[M⁻¹ L² T⁻¹]",
                                "[M⁰ L³ T⁻²]"
                            ),
                            correctOptionIndex = 0,
                            explanation = "From Newton's law of gravitation, F = G(m₁m₂)/r².\nTherefore, G = (F · r²) / (m₁ · m₂).\nDimensions: [F] = [M L T⁻²], [r²] = [L²], [m₁m₂] = [M²].\n[G] = [M L T⁻²][L²] / [M²] = [M⁻¹ L³ T⁻²].",
                            hint = "Rearrange F = G m₁ m₂ / r² for G and substitute dimensions.",
                            topic = "Dimensional Formula of Constants"
                        ),
                        Question(
                            id = "hcv_ch1_q3",
                            questionText = "A dimensionless quantity:",
                            options = listOf(
                                "never has a unit",
                                "always has a unit",
                                "may have a unit",
                                "does not exist"
                            ),
                            correctOptionIndex = 2,
                            explanation = "A dimensionless quantity may have a unit. For instance, plane angle is defined as arc/radius (dimensionless, [M⁰L⁰T⁰]) but has the SI unit radian (rad). Similarly, solid angle has the unit steradian (sr).",
                            hint = "Consider the unit of plane angle or solid angle.",
                            topic = "Dimensionless Quantities"
                        ),
                        Question(
                            id = "hcv_ch1_q4",
                            questionText = "The position of a particle at time t is given by the relation x(t) = (v₀ / α)(1 - e^(-αt)), where v₀ is a constant and α > 0. The dimensions of v₀ and α are respectively:",
                            options = listOf(
                                "[L T⁻¹] and [T⁻¹]",
                                "[L] and [T⁻¹]",
                                "[L T⁻¹] and [T]",
                                "[L T⁻²] and [T⁻¹]"
                            ),
                            correctOptionIndex = 0,
                            explanation = "The argument of an exponent must be dimensionless: [αt] = 1 => [α] = [T⁻¹].\nAlso, [x] = [v₀ / α] => [L] = [v₀] / [T⁻¹] => [v₀] = [L][T⁻¹] = [L T⁻¹].",
                            hint = "Exponential powers e^(...) must be dimensionless.",
                            topic = "Principle of Homogeneity"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 2: Physics and Mathematics
    // -------------------------------------------------------------
    private fun createChapter2(): Chapter {
        return Chapter(
            id = "phy_hcv_ch2",
            subject = SubjectType.PHYSICS,
            title = "Chapter 2: Physics and Mathematics",
            order = 2,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch2",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch2",
                    chapterTitle = "Chapter 2: Physics and Mathematics",
                    title = "Lec 01: Vectors, Dot & Cross Products & Calculus",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Triangle and Parallelogram laws of vector addition: R = √(A² + B² + 2AB cos θ).",
                        "Dot product A · B = AB cos θ gives scalar projection; Cross product A × B = AB sin θ n̂ produces normal vector.",
                        "Derivatives represent instantaneous rates of change: v = dx/dt, a = dv/dt = v(dv/dx).",
                        "Definite integrals calculate area under the curve and accumulation of infinitesimal increments."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Vector Addition & Resolution into Rectangular Components"),
                        LectureTimestamp(900, "15:00", "Scalar (Dot) and Vector (Cross) Products"),
                        LectureTimestamp(2100, "35:00", "Differentiation Rules in Kinematics"),
                        LectureTimestamp(2880, "48:00", "HCV Chapter 2 Objective Questions")
                    ),
                    dppId = "dpp_hcv_ch2"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch2",
                    number = 2,
                    title = "DPP 02: Vectors, Coordinate Geometry & Calculus",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 2: Physics and Mathematics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch2_q1",
                            questionText = "A vector A points vertically upward and B points towards north. The vector product A × B points:",
                            options = listOf(
                                "along west",
                                "along east",
                                "vertically downward",
                                "along north-west"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Using the right-hand rule: Point the fingers of the right hand vertically upward (A) and curl them towards north (B). The thumb points towards the west. Therefore, A × B is directed towards west.",
                            hint = "Use the right-hand screw rule from vertical up to north.",
                            topic = "Cross Product Direction"
                        ),
                        Question(
                            id = "hcv_ch2_q2",
                            questionText = "The resultant of two vectors of equal magnitude A has magnitude A. The angle between the two vectors is:",
                            options = listOf("60°", "90°", "120°", "180°"),
                            correctOptionIndex = 2,
                            explanation = "R² = A² + B² + 2AB cos θ.\nGiven R = A and B = A:\nA² = A² + A² + 2A² cos θ\nA² = 2A²(1 + cos θ) => 1 + cos θ = 1/2 => cos θ = -1/2.\nThus, θ = 120°.",
                            hint = "Equate R² = A² + A² + 2A² cos θ to A².",
                            topic = "Vector Addition & Magnitude"
                        ),
                        Question(
                            id = "hcv_ch2_q3",
                            questionText = "If |A + B| = |A - B|, then the angle between vector A and vector B is:",
                            options = listOf("0°", "45°", "90°", "180°"),
                            correctOptionIndex = 2,
                            explanation = "|A + B|² = A² + B² + 2 A · B.\n|A - B|² = A² + B² - 2 A · B.\nSince |A + B| = |A - B|, squaring both sides gives:\nA² + B² + 2 A · B = A² + B² - 2 A · B => 4 A · B = 0 => A · B = 0.\nSince neither vector is zero, cos θ = 0 => θ = 90°.",
                            hint = "Square both sides and simplify the dot product.",
                            topic = "Orthogonal Vectors"
                        ),
                        Question(
                            id = "hcv_ch2_q4",
                            questionText = "The position of a particle moving along the x-axis is given by x = 2t³ - 9t² + 12t + 5. The acceleration of the particle is zero at time t equal to:",
                            options = listOf("t = 1.0 s", "t = 1.5 s", "t = 2.0 s", "t = 3.0 s"),
                            correctOptionIndex = 1,
                            explanation = "Velocity v = dx/dt = 6t² - 18t + 12.\nAcceleration a = dv/dt = 12t - 18.\nSetting a = 0 => 12t - 18 = 0 => 12t = 18 => t = 1.5 s.",
                            hint = "Find the second derivative of position with respect to time.",
                            topic = "Calculus in Kinematics"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 3: Rest and Motion: Kinematics
    // -------------------------------------------------------------
    private fun createChapter3(): Chapter {
        return Chapter(
            id = "phy_hcv_ch3",
            subject = SubjectType.PHYSICS,
            title = "Chapter 3: Rest and Motion: Kinematics",
            order = 3,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch3",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch3",
                    chapterTitle = "Chapter 3: Rest and Motion: Kinematics",
                    title = "Lec 01: 1D Motion, Projectiles & Relative Motion",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Distance is total path length (scalar, non-decreasing); displacement is shortest vector between endpoints.",
                        "Equations for constant acceleration: v = u + at, s = ut + 1/2 at², v² = u² + 2as.",
                        "Projectile motion: Horizontal component u_x = u cos θ remains constant; vertical motion has acceleration -g.",
                        "Relative velocity formula: v_AB = v_A - v_B."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Rectilinear Motion & Graphs"),
                        LectureTimestamp(900, "15:00", "Free Fall under Gravity"),
                        LectureTimestamp(2100, "35:00", "2D Projectile Range, Trajectory & Time of Flight"),
                        LectureTimestamp(3120, "52:00", "Rain-Man & River-Boat Problems")
                    ),
                    dppId = "dpp_hcv_ch3"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch3",
                    number = 3,
                    title = "DPP 03: Rectilinear Motion & Projectiles",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 3: Rest and Motion: Kinematics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch3_q1",
                            questionText = "A particle moves along the x-axis. Its coordinate is given as x = 2 - 5t + 6t². The initial velocity of the particle is:",
                            options = listOf("2 m/s", "-5 m/s", "6 m/s", "12 m/s"),
                            correctOptionIndex = 1,
                            explanation = "Velocity v = dx/dt = d(2 - 5t + 6t²)/dt = -5 + 12t.\nAt time t = 0 (initial velocity):\nv(0) = -5 + 12(0) = -5 m/s.",
                            hint = "Differentiate position x(t) and substitute t = 0.",
                            topic = "Instantaneous Velocity"
                        ),
                        Question(
                            id = "hcv_ch3_q2",
                            questionText = "A ball is released from the top of a tower of height h. It takes time T to reach the ground. What is the position of the ball at time T/3?",
                            options = listOf(
                                "h/9 metres from the ground",
                                "7h/9 metres from the ground",
                                "8h/9 metres from the ground",
                                "17h/18 metres from the ground"
                            ),
                            correctOptionIndex = 2,
                            explanation = "Starting from rest (u = 0), distance fallen from the top in time t is y = 1/2 g t².\nFor total height: h = 1/2 g T².\nIn time t = T/3, distance fallen from the top is y₁ = 1/2 g (T/3)² = (1/9)(1/2 g T²) = h/9.\nTherefore, height from the ground is h - y₁ = h - h/9 = 8h/9.",
                            hint = "Calculate distance fallen from the top first, then subtract from h.",
                            topic = "Free Fall Under Gravity"
                        ),
                        Question(
                            id = "hcv_ch3_q3",
                            questionText = "A projectile is fired at an angle θ with the horizontal with velocity u. The radius of curvature of the trajectory at the highest point is:",
                            options = listOf(
                                "(u² cos² θ) / g",
                                "(u² sin² θ) / g",
                                "u² / g",
                                "(u² cos θ) / g"
                            ),
                            correctOptionIndex = 0,
                            explanation = "At the highest point, the velocity is purely horizontal, v = u cos θ. The acceleration perpendicular to the velocity is the full acceleration due to gravity, a_n = g. The radius of curvature is R = v² / a_n = (u cos θ)² / g = (u² cos² θ) / g.",
                            hint = "Recall radius of curvature R = v_perp² / a_normal.",
                            topic = "Curvature of Trajectory"
                        ),
                        Question(
                            id = "hcv_ch3_q4",
                            questionText = "A river is flowing from west to east at a speed of 5 m/s. A swimmer can swim in still water at 10 m/s. To cross the river along the shortest path, in what direction should he steer?",
                            options = listOf(
                                "30° west of north",
                                "30° east of north",
                                "60° west of north",
                                "Straight towards north"
                            ),
                            correctOptionIndex = 0,
                            explanation = "For the shortest path, the swimmer's net resultant velocity must be directed straight across the river (due north). Therefore, the horizontal component of his velocity must balance the river current:\nv_swimmer sin θ = v_river => 10 sin θ = 5 => sin θ = 1/2 => θ = 30° upstream (west of north).",
                            hint = "Component opposing river flow must cancel river speed: v_m sin θ = v_r.",
                            topic = "River Swimmer Relative Motion"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 4: The Forces
    // -------------------------------------------------------------
    private fun createChapter4(): Chapter {
        return Chapter(
            id = "phy_hcv_ch4",
            subject = SubjectType.PHYSICS,
            title = "Chapter 4: The Forces",
            order = 4,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch4",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch4",
                    chapterTitle = "Chapter 4: The Forces",
                    title = "Lec 01: Fundamental Forces & Contact Forces",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 50,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Four fundamental forces in nature: Gravitational, Electromagnetic, Strong Nuclear, Weak Nuclear.",
                        "Almost all everyday forces (friction, tension, normal reaction, elastic spring force, cohesion, adhesion) are electromagnetic in origin.",
                        "Gravitational force is always attractive and obeys inverse-square law.",
                        "Strong nuclear force is charge-independent and extremely short-ranged (~10⁻¹⁵ m)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Classification of Natural Forces"),
                        LectureTimestamp(720, "12:00", "Microscopic Origin of Normal Contact & Tension"),
                        LectureTimestamp(1800, "30:00", "Inverse Square Laws Comparison"),
                        LectureTimestamp(2520, "42:00", "HCV Questions on Forces")
                    ),
                    dppId = "dpp_hcv_ch4"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch4",
                    number = 4,
                    title = "DPP 04: Nature & Origins of Forces",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 4: The Forces",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 15,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch4_q1",
                            questionText = "When we kick a stone, we get hurt. Due to which of the following properties does this happen?",
                            options = listOf("Inertia", "Velocity", "Reaction", "Momentum"),
                            correctOptionIndex = 2,
                            explanation = "According to Newton's third law of motion, when the foot exerts an action force on the stone, the stone exerts an equal and opposite reaction force on the foot, which causes pain and hurt.",
                            hint = "Newton's third law: action and reaction forces.",
                            topic = "Action and Reaction"
                        ),
                        Question(
                            id = "hcv_ch4_q2",
                            questionText = "Which of the following forces is not electromagnetic in nature?",
                            options = listOf(
                                "Tension in a string",
                                "Weight of an object",
                                "Friction between surfaces",
                                "Contact force of a table"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The weight of an object is the gravitational pull of the Earth (gravitational interaction). Tension, friction, and normal contact forces are all macroscopic manifestations of intermolecular electromagnetic forces.",
                            hint = "Weight arises from Earth's gravitational attraction.",
                            topic = "Origins of Forces"
                        ),
                        Question(
                            id = "hcv_ch4_q3",
                            questionText = "The ratio of the gravitational force to the electrostatic force between two electrons separated by a distance r is of the order of:",
                            options = listOf("10⁻⁴³", "10⁻³⁶", "10⁻²⁴", "1"),
                            correctOptionIndex = 0,
                            explanation = "F_g = G m_e² / r² and F_e = (1 / 4πε₀) e² / r².\nRatio F_g / F_e = (G m_e²) / (k e²) ≈ (6.67 × 10⁻¹¹ × (9.1 × 10⁻³¹)²) / (9 × 10⁹ × (1.6 × 10⁻¹⁹)²)\n≈ 2.4 × 10⁻⁴³, which is of order 10⁻⁴³.",
                            hint = "Gravitational interaction between subatomic particles is unimaginably weaker than electrostatic force.",
                            topic = "Relative Strength of Fundamental Forces"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 5: Newton's Laws of Motion
    // -------------------------------------------------------------
    private fun createChapter5(): Chapter {
        return Chapter(
            id = "phy_hcv_ch5",
            subject = SubjectType.PHYSICS,
            title = "Chapter 5: Newton's Laws of Motion",
            order = 5,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch5",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch5",
                    chapterTitle = "Chapter 5: Newton's Laws of Motion",
                    title = "Lec 01: Free Body Diagrams, Constraints & Pseudo Forces",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Newton's First Law defines inertial frames: frames at rest or moving with constant velocity relative to distant stars.",
                        "Newton's Second Law: F_net = dp/dt = ma (for constant mass).",
                        "Newton's Third Law: Forces always occur in action-reaction pairs acting on DIFFERENT bodies simultaneously.",
                        "In a non-inertial frame accelerating with a₀, apply a pseudo force F_pseudo = -m a₀ on every body."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Inertial and Non-Inertial Frames"),
                        LectureTimestamp(840, "14:00", "Systematic Free Body Diagrams (FBD)"),
                        LectureTimestamp(2100, "35:00", "Pulley-Block String Constraint Relations"),
                        LectureTimestamp(3000, "50:00", "Pseudo Force Method in Accelerating Lifts & Wedges")
                    ),
                    dppId = "dpp_hcv_ch5"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch5",
                    number = 5,
                    title = "DPP 05: FBD, Equilibrium, Constraints & Pulleys",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 5: Newton's Laws of Motion",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 25,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch5_q1",
                            questionText = "A body of mass 2 kg is hung on a spring balance mounted in a lift. What will be the reading of the balance when the lift ascends with an acceleration of 4.9 m/s²? (g = 9.8 m/s²)",
                            options = listOf("2.0 kg", "3.0 kg", "1.0 kg", "4.0 kg"),
                            correctOptionIndex = 1,
                            explanation = "When the lift accelerates upward with acceleration a, the apparent weight is W' = m(g + a).\nHere, W' = 2 kg × (9.8 + 4.9) m/s² = 2 × 14.7 = 29.4 N.\nThe spring balance reading in kg-wt is W' / g = 29.4 / 9.8 = 3.0 kg.",
                            hint = "Apparent weight in an upward accelerating frame is m(g + a).",
                            topic = "Apparent Weight in Lift"
                        ),
                        Question(
                            id = "hcv_ch5_q2",
                            questionText = "Two masses m₁ = 5 kg and m₂ = 3 kg are connected by a light string passing over a frictionless massless pulley. The acceleration of the system is (g = 9.8 m/s²):",
                            options = listOf("2.45 m/s²", "4.9 m/s²", "1.22 m/s²", "9.8 m/s²"),
                            correctOptionIndex = 0,
                            explanation = "For an Atwood machine:\na = ((m₁ - m₂) / (m₁ + m₂)) * g\na = ((5 - 3) / (5 + 3)) * 9.8 = (2 / 8) * 9.8 = 9.8 / 4 = 2.45 m/s².",
                            hint = "Net driving force divided by total mass: (m₁ - m₂)g / (m₁ + m₂).",
                            topic = "Atwood Machine"
                        ),
                        Question(
                            id = "hcv_ch5_q3",
                            questionText = "A plumb bob is hung from the ceiling of a train compartment. If the train accelerates uniformly with acceleration 'a' along a straight horizontal track, the angle θ made by the string with the vertical is:",
                            options = listOf(
                                "θ = tan⁻¹(a / g)",
                                "θ = tan⁻¹(g / a)",
                                "θ = sin⁻¹(a / g)",
                                "θ = cos⁻¹(a / g)"
                            ),
                            correctOptionIndex = 0,
                            explanation = "In the train's frame of reference (accelerating with 'a'), a pseudo force 'ma' acts horizontally backward on the bob. The downward force is gravity 'mg'.\nFor equilibrium: T sin θ = ma and T cos θ = mg.\nDividing the two equations gives tan θ = a / g => θ = tan⁻¹(a / g).",
                            hint = "Apply pseudo force ma backwards and balance against tension components.",
                            topic = "Pseudo Force & Accelerated Frames"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 6: Friction
    // -------------------------------------------------------------
    private fun createChapter6(): Chapter {
        return Chapter(
            id = "phy_hcv_ch6",
            subject = SubjectType.PHYSICS,
            title = "Chapter 6: Friction",
            order = 6,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch6",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch6",
                    chapterTitle = "Chapter 6: Friction",
                    title = "Lec 01: Static, Kinetic Friction & Multi-Block Systems",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Static friction is self-adjusting in both magnitude and direction: 0 ≤ f_s ≤ μ_s N.",
                        "Once relative motion begins, kinetic friction acts with constant value f_k = μ_k N opposing relative sliding.",
                        "Angle of friction λ: tan λ = μ_s. Angle of repose equals angle of friction.",
                        "Friction is not necessarily an opposing force to motion; it often provides the driving force that produces acceleration (e.g., walking, car tires)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Microscopic Interlocking and Cold Welding"),
                        LectureTimestamp(840, "14:00", "Limiting Friction and Angle of Repose"),
                        LectureTimestamp(2100, "35:00", "Two-Block Friction Systems (Tension vs Slipping)"),
                        LectureTimestamp(2880, "48:00", "HCV Chapter 6 Exercises Drill")
                    ),
                    dppId = "dpp_hcv_ch6"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch6",
                    number = 6,
                    title = "DPP 06: Static, Limiting & Kinetic Friction",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 6: Friction",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch6_q1",
                            questionText = "A block of mass 2 kg rests on a rough horizontal plane with coefficient of static friction μ_s = 0.5. A horizontal force of 6 N is applied to the block. The frictional force acting on the block is (g = 10 m/s²):",
                            options = listOf("10 N", "6 N", "5 N", "0 N"),
                            correctOptionIndex = 1,
                            explanation = "Normal reaction N = mg = 2 × 10 = 20 N.\nLimiting static friction f_max = μ_s N = 0.5 × 20 = 10 N.\nSince the applied horizontal force F = 6 N is less than f_max (10 N), the block does not slide.\nStatic friction is self-adjusting, so f_s = applied force = 6 N.",
                            hint = "Compare applied force to maximum limiting friction; static friction balances the applied force if not slipping.",
                            topic = "Self-Adjusting Static Friction"
                        ),
                        Question(
                            id = "hcv_ch6_q2",
                            questionText = "A block slides down an inclined plane of slope angle 45° with constant velocity. What is the coefficient of kinetic friction μ_k between the block and the plane?",
                            options = listOf("0.5", "1.0", "0.707", "0.25"),
                            correctOptionIndex = 1,
                            explanation = "For motion down an incline with constant velocity (zero acceleration):\nmg sin θ = f_k = μ_k N.\nSince N = mg cos θ:\nmg sin θ = μ_k mg cos θ => μ_k = tan θ.\nFor θ = 45°: μ_k = tan 45° = 1.0.",
                            hint = "Equate downhill component mg sin θ with friction force μ_k mg cos θ.",
                            topic = "Angle of Repose & Incline Motion"
                        ),
                        Question(
                            id = "hcv_ch6_q3",
                            questionText = "A block of mass m is placed on another block of mass M, which rests on a smooth horizontal table. The coefficient of friction between the two blocks is μ. The maximum horizontal force that can be applied to M so that both blocks move together without slipping is:",
                            options = listOf(
                                "μ(M + m)g",
                                "μ m g",
                                "μ M g",
                                "(M + m)g / μ"
                            ),
                            correctOptionIndex = 0,
                            explanation = "The maximum acceleration the top block 'm' can experience without slipping is provided entirely by friction between the blocks:\nf_max = μ m g => a_max = f_max / m = μ g.\nFor both blocks (total mass M + m) to move together with this maximum acceleration:\nF_max = (M + m) a_max = μ(M + m)g.",
                            hint = "Find maximum acceleration of upper block, then apply F = (M + m) a.",
                            topic = "Two-Block Systems"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 7: Circular Motion
    // -------------------------------------------------------------
    private fun createChapter7(): Chapter {
        return Chapter(
            id = "phy_hcv_ch7",
            subject = SubjectType.PHYSICS,
            title = "Chapter 7: Circular Motion",
            order = 7,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch7",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch7",
                    chapterTitle = "Chapter 7: Circular Motion",
                    title = "Lec 01: Centripetal Acceleration & Vertical Circles",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Centripetal acceleration is directed radially inward: a_r = v² / r = ω² r.",
                        "Tangential acceleration changes speed: a_t = dv/dt = r α.",
                        "Banking of roads: Optimum speed without friction v = √(r g tan θ).",
                        "Vertical circular motion with light string: Minimum bottom speed to complete loop is u = √(5gr); minimum top speed is v_top = √(gr)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Kinematics of Circular Motion"),
                        LectureTimestamp(840, "14:00", "Centripetal vs Centrifugal Force"),
                        LectureTimestamp(2100, "35:00", "Banking of Highways and Railway Tracks"),
                        LectureTimestamp(2880, "48:00", "Critical Velocity in Vertical Loop")
                    ),
                    dppId = "dpp_hcv_ch7"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch7",
                    number = 7,
                    title = "DPP 07: Centripetal Force, Banking & Vertical Loops",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 7: Circular Motion",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch7_q1",
                            questionText = "A car moves on a circular road of radius 100 m with a constant speed of 10 m/s. Its angular velocity is:",
                            options = listOf("0.1 rad/s", "1 rad/s", "10 rad/s", "0.01 rad/s"),
                            correctOptionIndex = 0,
                            explanation = "Linear speed v and angular velocity ω are related by v = r ω.\nTherefore, ω = v / r = 10 / 100 = 0.1 rad/s.",
                            hint = "Use the relation v = r ω.",
                            topic = "Angular Velocity"
                        ),
                        Question(
                            id = "hcv_ch7_q2",
                            questionText = "A heavy stone of mass m is tied to one end of a light string of length L and rotated in a vertical circle. The minimum speed at the lowest point required so that the string never goes slack is:",
                            options = listOf("√(2gL)", "√(3gL)", "√(5gL)", "√(gL)"),
                            correctOptionIndex = 2,
                            explanation = "At the highest point, tension must be T ≥ 0.\nT + mg = m v_top² / L => v_top² ≥ gL.\nApplying conservation of mechanical energy between lowest point and highest point:\n(1/2) m u² = (1/2) m v_top² + mg(2L)\nu² = v_top² + 4gL = gL + 4gL = 5gL => u = √(5gL).",
                            hint = "Use energy conservation between bottom and top where tension T ≥ 0.",
                            topic = "Vertical Circular Motion"
                        ),
                        Question(
                            id = "hcv_ch7_q3",
                            questionText = "The maximum safe speed of a vehicle on a circular track of radius R with angle of banking θ (ignoring friction) is:",
                            options = listOf("√(R g tan θ)", "√(R g / tan θ)", "R g tan θ", "√(R g sin θ)"),
                            correctOptionIndex = 0,
                            explanation = "Resolving normal reaction:\nN cos θ = mg (vertical equilibrium)\nN sin θ = m v² / R (providing centripetal force)\nDividing: tan θ = v² / (R g) => v = √(R g tan θ).",
                            hint = "Resolve the normal reaction into horizontal and vertical components.",
                            topic = "Banking of Tracks"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 8: Work and Energy
    // -------------------------------------------------------------
    private fun createChapter8(): Chapter {
        return Chapter(
            id = "phy_hcv_ch8",
            subject = SubjectType.PHYSICS,
            title = "Chapter 8: Work and Energy",
            order = 8,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch8",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch8",
                    chapterTitle = "Chapter 8: Work and Energy",
                    title = "Lec 01: Work-Energy Theorem & Conservation of Energy",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Work done by a force W = ∫ F · dr. For constant force, W = F · s = F s cos θ.",
                        "Work-Energy Theorem: Total work done by all forces (conservative, non-conservative, pseudo) equals change in kinetic energy: W_all = ΔK.",
                        "Potential energy is defined only for conservative forces: ΔU = -W_conservative => F = -dU/dr.",
                        "Stable equilibrium corresponds to minimum potential energy (d²U/dr² > 0)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Work Done by Constant & Variable Forces"),
                        LectureTimestamp(900, "15:00", "The Work-Energy Theorem Proof"),
                        LectureTimestamp(2100, "35:00", "Conservative Forces and Potential Energy Functions"),
                        LectureTimestamp(2940, "49:00", "Equilibrium States & HCV Problems")
                    ),
                    dppId = "dpp_hcv_ch8"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch8",
                    number = 8,
                    title = "DPP 08: Work-Energy Theorem, Power & Potential Energy",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 8: Work and Energy",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch8_q1",
                            questionText = "A force F = (2 + x) N acts on a particle in the x-direction. The work done by this force during a displacement from x = 1.0 m to x = 2.0 m is:",
                            options = listOf("2.5 J", "3.5 J", "4.5 J", "5.0 J"),
                            correctOptionIndex = 1,
                            explanation = "W = ∫ F dx = ∫₁² (2 + x) dx = [2x + x²/2]₁²\n= (2(2) + 2²/2) - (2(1) + 1²/2)\n= (4 + 2) - (2 + 0.5) = 6 - 2.5 = 3.5 Joules.",
                            hint = "Integrate F(x) from 1 to 2.",
                            topic = "Work Done by Variable Force"
                        ),
                        Question(
                            id = "hcv_ch8_q2",
                            questionText = "The potential energy of a particle in a force field is U = A/r² - B/r, where A and B are positive constants. The distance r for stable equilibrium is:",
                            options = listOf("2A / B", "B / 2A", "A / B", "B / A"),
                            correctOptionIndex = 0,
                            explanation = "For equilibrium, F = -dU/dr = 0.\ndU/dr = d(A r⁻² - B r⁻¹)/dr = -2A r⁻³ + B r⁻² = (-2A + B r) / r³.\nSetting dU/dr = 0 gives -2A + B r = 0 => r = 2A / B.\nAt this point, d²U/dr² = 6A r⁻⁴ - 2B r⁻³ = (6A - 2B(2A/B)) / r⁴ = 2A / r⁴ > 0 (minimum U, stable).",
                            hint = "Differentiate U(r) with respect to r and set it to 0.",
                            topic = "Stable Equilibrium & Potential Energy"
                        ),
                        Question(
                            id = "hcv_ch8_q3",
                            questionText = "A block of mass m moving with velocity v compresses a spring of spring constant k by a maximum distance x. If the mass is halved and speed doubled, the new maximum compression will be:",
                            options = listOf("x", "x √2", "2x", "4x"),
                            correctOptionIndex = 1,
                            explanation = "By conservation of energy, initial kinetic energy converts to spring potential energy:\n(1/2) k x² = (1/2) m v² => x = v √(m / k).\nWhen m' = m/2 and v' = 2v:\nx' = (2v) √((m/2) / k) = 2v √(m / (2k)) = (2 / √2) v √(m/k) = √2 · x.",
                            hint = "Recall x = v √(m / k). Substitute m/2 and 2v.",
                            topic = "Spring Energy Conservation"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 9: Centre of Mass, Linear Momentum, Collision
    // -------------------------------------------------------------
    private fun createChapter9(): Chapter {
        return Chapter(
            id = "phy_hcv_ch9",
            subject = SubjectType.PHYSICS,
            title = "Chapter 9: Centre of Mass, Momentum, Collision",
            order = 9,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch9",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch9",
                    chapterTitle = "Chapter 9: Centre of Mass, Momentum, Collision",
                    title = "Lec 01: Centre of Mass, Impulse & Collisions",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Position of Centre of Mass: R_cm = (∑ m_i r_i) / M = (1/M) ∫ r dm.",
                        "If no external force acts on the system (F_ext = 0), the velocity of the center of mass remains constant (P_total is conserved).",
                        "Coefficient of restitution e = (separation velocity) / (approach velocity).",
                        "For perfectly elastic collisions e = 1 (KE conserved); for completely inelastic collisions e = 0 (bodies stick together)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Calculation of Centre of Mass of Rigid Bodies"),
                        LectureTimestamp(900, "15:00", "Conservation of Linear Momentum & Rocket Propulsion"),
                        LectureTimestamp(2100, "35:00", "1D & 2D Elastic and Inelastic Collisions"),
                        LectureTimestamp(3120, "52:00", "HCV Chapter 9 Problems Analysis")
                    ),
                    dppId = "dpp_hcv_ch9"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch9",
                    number = 9,
                    title = "DPP 09: Centre of Mass, Impulse & Collisions",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 9: Centre of Mass, Momentum, Collision",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch9_q1",
                            questionText = "From a uniform circular disc of radius R, a circular hole of radius R/2 is cut out touching the circumference of the disc. The distance of the centre of mass of the remaining portion from the original centre is:",
                            options = listOf("R / 6", "R / 4", "R / 3", "R / 8"),
                            correctOptionIndex = 0,
                            explanation = "Let original disc have mass M and area πR².\nThe cut-out portion has area π(R/2)² = πR²/4, so mass m₁ = M/4, and its centre is located at x₁ = R/2.\nBy negative mass method: x_cm = (M(0) - m₁ x₁) / (M - m₁)\nx_cm = (-(M/4)(R/2)) / (M - M/4) = -(MR/8) / (3M/4) = -R / 6.\nThe magnitude of displacement from the centre is R / 6.",
                            hint = "Use the cavity method: original complete disc minus the removed disc.",
                            topic = "Centre of Mass of Bodies with Cavity"
                        ),
                        Question(
                            id = "hcv_ch9_q2",
                            questionText = "A bullet of mass m moving with velocity v strikes a stationary wooden block of mass M suspended by a string and gets embedded in it. The velocity of the combined system immediately after collision is:",
                            options = listOf(
                                "(m v) / (m + M)",
                                "(M v) / (m + M)",
                                "((m + M) v) / m",
                                "(m v) / M"
                            ),
                            correctOptionIndex = 0,
                            explanation = "During the brief impact, external horizontal forces are negligible, so horizontal linear momentum is conserved:\nP_initial = m v + M(0) = m v.\nP_final = (m + M) V.\nTherefore, V = (m v) / (m + M).",
                            hint = "Apply conservation of linear momentum along the bullet's direction of motion.",
                            topic = "Completely Inelastic Collision"
                        ),
                        Question(
                            id = "hcv_ch9_q3",
                            questionText = "In a head-on elastic collision of two bodies of equal mass, one moving with velocity u and the other at rest, after the collision:",
                            options = listOf(
                                "they move together with velocity u/2",
                                "the first body comes to rest and the second moves with velocity u",
                                "both rebound with velocity u/2",
                                "the first body continues with velocity u"
                            ),
                            correctOptionIndex = 1,
                            explanation = "In an elastic head-on collision (e = 1) between two identical masses (m₁ = m₂), the bodies exchange their velocities completely. The incident body comes to rest (v₁ = 0) and the target body moves forward with velocity v₂ = u.",
                            hint = "Identical masses exchange velocities in an elastic 1D collision.",
                            topic = "Elastic Collision"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 10: Rotational Mechanics
    // -------------------------------------------------------------
    private fun createChapter10(): Chapter {
        return Chapter(
            id = "phy_hcv_ch10",
            subject = SubjectType.PHYSICS,
            title = "Chapter 10: Rotational Mechanics",
            order = 10,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch10",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch10",
                    chapterTitle = "Chapter 10: Rotational Mechanics",
                    title = "Lec 01: Torque, Moment of Inertia & Pure Rolling",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 70,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Torque τ = r × F = I α.",
                        "Parallel axis theorem: I = I_cm + M d². Perpendicular axes theorem (planar laminas): I_z = I_x + I_y.",
                        "Angular momentum L = r × p = I ω. If τ_ext = 0, L is conserved.",
                        "Pure rolling without slipping: v_cm = R ω and a_cm = R α at the point of contact."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Moment of Inertia of Standard Geometries"),
                        LectureTimestamp(960, "16:00", "Parallel and Perpendicular Axes Theorems"),
                        LectureTimestamp(2160, "36:00", "Angular Momentum & Conservation Law"),
                        LectureTimestamp(3240, "54:00", "Pure Rolling on Inclined Planes")
                    ),
                    dppId = "dpp_hcv_ch10"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch10",
                    number = 10,
                    title = "DPP 10: Torque, Moment of Inertia & Pure Rolling",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 10: Rotational Mechanics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 25,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch10_q1",
                            questionText = "The moment of inertia of a uniform thin rod of mass M and length L about an axis perpendicular to its length and passing through one of its ends is:",
                            options = listOf("M L² / 12", "M L² / 3", "M L² / 2", "2 M L² / 3"),
                            correctOptionIndex = 1,
                            explanation = "About the center of mass perpendicular to rod, I_cm = (1/12) M L².\nThe distance from the center to one end is d = L/2.\nBy the parallel axis theorem:\nI_end = I_cm + M d² = (1/12) M L² + M(L/2)² = (1/12 + 1/4) M L² = (4/12) M L² = (1/3) M L².",
                            hint = "Use parallel axis theorem I = I_cm + M(L/2)².",
                            topic = "Moment of Inertia Theorems"
                        ),
                        Question(
                            id = "hcv_ch10_q2",
                            questionText = "A solid sphere and a hollow sphere of equal mass and radius roll down an inclined plane without slipping. Which one reaches the bottom first?",
                            options = listOf(
                                "Solid sphere",
                                "Hollow sphere",
                                "Both reach at the same time",
                                "Depends on inclination angle"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Acceleration in pure rolling down an incline is a = (g sin θ) / (1 + I_cm / (M R²)).\nFor solid sphere, I_cm / (M R²) = 2/5 = 0.4 => a = (g sin θ) / 1.4 = 0.714 g sin θ.\nFor hollow sphere, I_cm / (M R²) = 2/3 ≈ 0.67 => a = (g sin θ) / 1.67 = 0.6 g sin θ.\nSince the solid sphere has higher acceleration, it covers the distance in less time and reaches first.",
                            hint = "Bodies with smaller moment of inertia have larger linear acceleration in rolling.",
                            topic = "Rolling Acceleration down an Incline"
                        ),
                        Question(
                            id = "hcv_ch10_q3",
                            questionText = "A child stands on the edge of a rotating platform with his arms outstretched. When he pulls his arms in towards his chest, what happens to the angular velocity ω and kinetic energy K of the system?",
                            options = listOf(
                                "ω increases, K remains unchanged",
                                "ω increases, K increases",
                                "ω decreases, K decreases",
                                "Both ω and K remain constant"
                            ),
                            correctOptionIndex = 1,
                            explanation = "No external torque acts on the system about the rotation axis, so angular momentum L = I ω is conserved.\nWhen he pulls his arms in, his moment of inertia I decreases. Therefore, ω = L / I increases.\nRotational kinetic energy is K = L² / (2I). As I decreases while L is constant, K increases! (The extra kinetic energy comes from the biological muscular work done by the child pulling his arms in).",
                            hint = "L is conserved, so K = L² / (2I) increases as I decreases.",
                            topic = "Conservation of Angular Momentum"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 11: Gravitation
    // -------------------------------------------------------------
    private fun createChapter11(): Chapter {
        return Chapter(
            id = "phy_hcv_ch11",
            subject = SubjectType.PHYSICS,
            title = "Chapter 11: Gravitation",
            order = 11,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch11",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch11",
                    chapterTitle = "Chapter 11: Gravitation",
                    title = "Lec 01: Kepler's Laws, Gravitational Field & Satellites",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Universal law of gravitation: F = G m₁ m₂ / r².",
                        "Gravitational potential V = -G M / r, Potential energy U = -G M m / r.",
                        "Escape velocity from Earth surface: v_e = √(2 g R) ≈ 11.2 km/s.",
                        "Orbital velocity of satellite at altitude h: v_o = √(G M / (R + h)); Kepler's third law: T² ∝ r³."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Newton's Universal Law and Gravitational Field Intensity"),
                        LectureTimestamp(840, "14:00", "Gravitational Potential Inside & Outside Spheres"),
                        LectureTimestamp(2100, "35:00", "Escape Velocity Derivation"),
                        LectureTimestamp(2880, "48:00", "Geostationary and Polar Satellites")
                    ),
                    dppId = "dpp_hcv_ch11"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch11",
                    number = 11,
                    title = "DPP 11: Kepler's Laws, Fields & Escape Velocity",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 11: Gravitation",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch11_q1",
                            questionText = "If the radius of the Earth were to shrink by 1% while its mass remains constant, the acceleration due to gravity on the Earth's surface would:",
                            options = listOf(
                                "decrease by 2%",
                                "increase by 2%",
                                "increase by 1%",
                                "remain unchanged"
                            ),
                            correctOptionIndex = 1,
                            explanation = "g = G M / R².\nTaking logarithms and differentiating for small percentage changes:\nΔg / g = -2 (ΔR / R).\nGiven ΔR / R = -1% (shrink):\nΔg / g = -2 (-1%) = +2%.\nTherefore, the acceleration due to gravity increases by 2%.",
                            hint = "Use g ∝ R⁻² and differentiate: Δg/g = -2(ΔR/R).",
                            topic = "Variation of g with Radius"
                        ),
                        Question(
                            id = "hcv_ch11_q2",
                            questionText = "The escape velocity from the surface of the Earth is 11.2 km/s. If a planet has twice the radius and twice the mass of Earth, the escape velocity from its surface is:",
                            options = listOf("11.2 km/s", "22.4 km/s", "5.6 km/s", "15.8 km/s"),
                            correctOptionIndex = 0,
                            explanation = "Escape velocity is v_e = √(2 G M / R).\nFor the new planet, M' = 2M and R' = 2R.\nv_e' = √(2 G (2M) / (2R)) = √(2 G M / R) = v_e = 11.2 km/s.",
                            hint = "The ratio M/R remains unchanged.",
                            topic = "Escape Velocity"
                        ),
                        Question(
                            id = "hcv_ch11_q3",
                            questionText = "A satellite of mass m is orbiting close to Earth's surface in an orbit of radius R. Its kinetic energy K and total energy E are:",
                            options = listOf(
                                "K = G M m / (2R), E = -G M m / (2R)",
                                "K = G M m / R, E = 0",
                                "K = G M m / (2R), E = G M m / (2R)",
                                "K = G M m / R, E = -G M m / R"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Orbital velocity v_o = √(G M / R).\nKinetic energy K = (1/2) m v_o² = G M m / (2R).\nPotential energy U = -G M m / R.\nTotal energy E = K + U = G M m / (2R) - G M m / R = -G M m / (2R).",
                            hint = "Remember total energy of a bound orbit is negative of its kinetic energy: E = -K = U/2.",
                            topic = "Satellite Energy Relations"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 12: Simple Harmonic Motion
    // -------------------------------------------------------------
    private fun createChapter12(): Chapter {
        return Chapter(
            id = "phy_hcv_ch12",
            subject = SubjectType.PHYSICS,
            title = "Chapter 12: Simple Harmonic Motion",
            order = 12,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch12",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch12",
                    chapterTitle = "Chapter 12: Simple Harmonic Motion",
                    title = "Lec 01: Kinematics, Dynamics & Energy of SHM",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Definition of SHM: Acceleration is directly proportional to displacement and directed towards the mean position: a = -ω² x.",
                        "Displacement equation: x(t) = A sin(ωt + φ); Velocity: v = ω √(A² - x²).",
                        "Total energy E = (1/2) m ω² A² is constant; oscillates between purely potential at extremes and purely kinetic at mean position.",
                        "Time period of simple pendulum T = 2π √(L/g); Spring-mass system T = 2π √(m/k)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Differential Equation of SHM"),
                        LectureTimestamp(840, "14:00", "Phasor Method in Superposition of SHMs"),
                        LectureTimestamp(2100, "35:00", "Energy Graphs & Average Values"),
                        LectureTimestamp(2880, "48:00", "Physical Pendulum and Torsional Oscillator")
                    ),
                    dppId = "dpp_hcv_ch12"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch12",
                    number = 12,
                    title = "DPP 12: SHM Equations, Pendulums & Oscillations",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 12: Simple Harmonic Motion",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch12_q1",
                            questionText = "A particle executes SHM with an amplitude of 10 cm. At what distance from the mean position are its kinetic energy and potential energy equal?",
                            options = listOf("5 cm", "5 √2 cm", "7.07 cm", "Both B and C"),
                            correctOptionIndex = 3,
                            explanation = "Kinetic energy K = (1/2) m ω² (A² - x²).\nPotential energy U = (1/2) m ω² x².\nEquating K = U:\n(1/2) m ω² (A² - x²) = (1/2) m ω² x² => A² - x² = x² => 2x² = A² => x = A / √2.\nGiven A = 10 cm:\nx = 10 / √2 = 5 √2 cm ≈ 7.07 cm. Both B and C are correct.",
                            hint = "Set K = U => x = A / √2.",
                            topic = "Energy in SHM"
                        ),
                        Question(
                            id = "hcv_ch12_q2",
                            questionText = "The time period of a simple pendulum inside a stationary lift is T. If the lift accelerates downwards with an acceleration a = g/3, the new time period is:",
                            options = listOf("T √(3/2)", "T √(2/3)", "T / 2", "2 T"),
                            correctOptionIndex = 0,
                            explanation = "Time period of pendulum is T = 2π √(L / g_eff).\nIn a downward accelerating lift, effective gravity is g_eff = g - a = g - g/3 = 2g/3.\nTherefore, T' = 2π √(L / (2g/3)) = √(3/2) · 2π √(L / g) = T √(3/2).",
                            hint = "Effective gravity in a downward accelerating frame is g_eff = g - a.",
                            topic = "Simple Pendulum in Accelerated Frame"
                        ),
                        Question(
                            id = "hcv_ch12_q3",
                            questionText = "A spring of stiffness k is cut into two equal halves. What is the spring constant of each half?",
                            options = listOf("k / 2", "k", "2 k", "4 k"),
                            correctOptionIndex = 2,
                            explanation = "The spring constant k is inversely proportional to its natural length L: k ∝ 1/L => k · L = constant.\nWhen the spring is cut into two halves, each half has length L' = L/2.\nTherefore, the new spring constant is k' = k (L / L') = k (L / (L/2)) = 2k.",
                            hint = "Spring constant is inversely proportional to length (k · L = const).",
                            topic = "Cutting of Springs"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 13: Fluid Mechanics
    // -------------------------------------------------------------
    private fun createChapter13(): Chapter {
        return Chapter(
            id = "phy_hcv_ch13",
            subject = SubjectType.PHYSICS,
            title = "Chapter 13: Fluid Mechanics",
            order = 13,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch13",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch13",
                    chapterTitle = "Chapter 13: Fluid Mechanics",
                    title = "Lec 01: Hydrostatics, Archimedes & Bernoulli's Principle",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Hydrostatic pressure at depth h: P = P₀ + ρ g h.",
                        "Pascal's law: Pressure applied to an enclosed liquid is transmitted undiminished in all directions.",
                        "Archimedes' principle: Buoyant force equals weight of fluid displaced: F_B = V_displaced ρ_fluid g.",
                        "Equation of continuity: A₁ v₁ = A₂ v₂; Bernoulli's theorem: P + (1/2) ρ v² + ρ g h = constant."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Hydrostatic Pressure & Pascal's Principle"),
                        LectureTimestamp(840, "14:00", "Buoyancy & Conditions for Floatation"),
                        LectureTimestamp(2100, "35:00", "Continuity Equation & Streamline Flow"),
                        LectureTimestamp(2880, "48:00", "Bernoulli's Theorem & Torricelli's Law of Efflux")
                    ),
                    dppId = "dpp_hcv_ch13"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch13",
                    number = 13,
                    title = "DPP 13: Pressure, Buoyancy & Bernoulli's Equation",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 13: Fluid Mechanics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch13_q1",
                            questionText = "An ice cube floats in a glass beaker completely filled with water to the brim. When the ice melts completely, what happens to the water level?",
                            options = listOf(
                                "Water overflows",
                                "Water level falls",
                                "Water level remains unchanged",
                                "Depends on temperature"
                            ),
                            correctOptionIndex = 2,
                            explanation = "A floating body displaces a weight of water equal to its own weight: M_ice = ρ_water V_displaced => V_displaced = M_ice / ρ_water.\nWhen the ice melts, its mass M_ice remains unchanged and turns into water of volume V_melted = M_ice / ρ_water.\nSince V_displaced = V_melted, the melted water exactly fills the submerged volume previously occupied by the floating ice, so the water level remains unchanged.",
                            hint = "Mass of ice equals mass of water melted; compare submerged volume with melted volume.",
                            topic = "Archimedes' Principle & Floating Ice"
                        ),
                        Question(
                            id = "hcv_ch13_q2",
                            questionText = "A large open tank has a small hole at depth h below the water surface. The speed of efflux of water through the hole is:",
                            options = listOf("√(g h)", "√(2 g h)", "2 √(g h)", "g h"),
                            correctOptionIndex = 1,
                            explanation = "By Torricelli's law (derived from Bernoulli's equation):\nP₀ + ρ g h + 0 = P₀ + (1/2) ρ v² + 0\n(1/2) ρ v² = ρ g h => v = √(2 g h).",
                            hint = "Use Torricelli's theorem: v = √(2 g h).",
                            topic = "Torricelli's Law of Efflux"
                        ),
                        Question(
                            id = "hcv_ch13_q3",
                            questionText = "Water flows through a horizontal pipe of non-uniform cross-section. At the narrowest point of the pipe:",
                            options = listOf(
                                "velocity is maximum and pressure is minimum",
                                "velocity is minimum and pressure is maximum",
                                "both velocity and pressure are maximum",
                                "both velocity and pressure are minimum"
                            ),
                            correctOptionIndex = 0,
                            explanation = "From the equation of continuity A₁v₁ = A₂v₂, where area A is smallest, velocity v is highest.\nFrom Bernoulli's equation for a horizontal pipe: P + (1/2) ρ v² = constant.\nAs velocity increases, kinetic pressure (1/2) ρ v² increases, so static pressure P must decrease.",
                            hint = "Apply continuity A·v = const and Bernoulli P + 1/2 ρ v² = const.",
                            topic = "Venturi Effect"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 14: Some Mechanical Properties of Matter
    // -------------------------------------------------------------
    private fun createChapter14(): Chapter {
        return Chapter(
            id = "phy_hcv_ch14",
            subject = SubjectType.PHYSICS,
            title = "Chapter 14: Mechanical Properties of Matter",
            order = 14,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch14",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch14",
                    chapterTitle = "Chapter 14: Mechanical Properties of Matter",
                    title = "Lec 01: Elasticity, Surface Tension & Viscosity",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Hooke's law: Stress = Y × Strain; Elastic potential energy density u = (1/2) × Stress × Strain.",
                        "Surface tension T = F/L: Excess pressure inside a liquid drop ΔP = 2T/R; inside a soap bubble ΔP = 4T/R.",
                        "Capillary rise formula: h = (2 T cos θ) / (r ρ g).",
                        "Stokes' law for viscous drag: F = 6πηrv; Terminal velocity v_t = 2r²(ρ - σ)g / (9η)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Stress-Strain Curve & Moduli of Elasticity"),
                        LectureTimestamp(900, "15:00", "Surface Tension & Laplace Excess Pressure"),
                        LectureTimestamp(2100, "35:00", "Capillary Action & Contact Angle"),
                        LectureTimestamp(2880, "48:00", "Viscous Flow, Poiseuille's Law & Terminal Velocity")
                    ),
                    dppId = "dpp_hcv_ch14"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch14",
                    number = 14,
                    title = "DPP 14: Elasticity, Surface Tension & Viscosity",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 14: Mechanical Properties of Matter",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch14_q1",
                            questionText = "A steel wire of length L and cross-sectional area A is stretched by a force F. If Y is the Young's modulus, the elastic potential energy stored in the wire is:",
                            options = listOf(
                                "(F² L) / (2 A Y)",
                                "(F² L) / (A Y)",
                                "(F L) / (2 A Y)",
                                "(2 F² L) / (A Y)"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Elongation is ΔL = (F L) / (A Y).\nWork done in stretching the wire converts to stored elastic potential energy:\nU = (1/2) F ΔL = (1/2) F ((F L) / (A Y)) = (F² L) / (2 A Y).",
                            hint = "U = (1/2) × Load × Extension.",
                            topic = "Elastic Potential Energy"
                        ),
                        Question(
                            id = "hcv_ch14_q2",
                            questionText = "Excess pressure inside a soap bubble of radius R in air is ΔP₁. Excess pressure inside a water drop of same radius R in air is ΔP₂. The ratio ΔP₁ / ΔP₂ is:",
                            options = listOf("1 : 1", "2 : 1", "1 : 2", "4 : 1"),
                            correctOptionIndex = 1,
                            explanation = "A soap bubble has two free surfaces (inner and outer), so excess pressure is ΔP₁ = 4T / R.\nA liquid drop has only one free surface, so excess pressure is ΔP₂ = 2T / R.\nTherefore, ΔP₁ / ΔP₂ = (4T / R) / (2T / R) = 2 : 1.",
                            hint = "Soap bubbles have two liquid-air interfaces, drops have only one.",
                            topic = "Excess Pressure in Bubbles & Drops"
                        ),
                        Question(
                            id = "hcv_ch14_q3",
                            questionText = "A small spherical ball of radius r falls through a viscous liquid. Its terminal velocity v_t is proportional to:",
                            options = listOf("r", "r²", "r³", "1 / r"),
                            correctOptionIndex = 1,
                            explanation = "By Stokes' law, at terminal velocity, viscous drag + buoyant force = weight:\n6πηrv_t + (4/3)πr³σg = (4/3)πr³ρg\n6πηrv_t = (4/3)πr³(ρ - σ)g => v_t = (2/9) (r²(ρ - σ)g / η).\nTherefore, terminal velocity is directly proportional to r².",
                            hint = "Equate weight with buoyant force plus Stokes' viscous drag.",
                            topic = "Terminal Velocity in Viscous Medium"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 15: Wave Motion and Waves on a String
    // -------------------------------------------------------------
    private fun createChapter15(): Chapter {
        return Chapter(
            id = "phy_hcv_ch15",
            subject = SubjectType.PHYSICS,
            title = "Chapter 15: Wave Motion & Waves on a String",
            order = 15,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch15",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch15",
                    chapterTitle = "Chapter 15: Wave Motion & Waves on a String",
                    title = "Lec 01: Transverse Wave Equation, Harmonics & Sonometer",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "One-dimensional wave equation: ∂²y/∂x² = (1/v²) ∂²y/∂t².",
                        "Speed of transverse waves on a stretched string: v = √(T / μ), where μ is mass per unit length.",
                        "Standing waves on string fixed at both ends: Nodes at ends; wavelengths λ_n = 2L/n, frequencies f_n = n (v / 2L).",
                        "Reflection at fixed boundary inverts phase by π; reflection at free boundary produces zero phase change."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Harmonic Wave Equation y = A sin(kx - ωt)"),
                        LectureTimestamp(840, "14:00", "Speed of Wave on a String"),
                        LectureTimestamp(2100, "35:00", "Standing Waves & Boundary Conditions"),
                        LectureTimestamp(2880, "48:00", "Sonometer Laws & HCV Problems")
                    ),
                    dppId = "dpp_hcv_ch15"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch15",
                    number = 15,
                    title = "DPP 15: Wave Equation, Standing Waves & Harmonics",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 15: Wave Motion & Waves on a String",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch15_q1",
                            questionText = "A wave is represented by y = 0.05 sin(100 t - 2 x) in SI units. The wave velocity is:",
                            options = listOf("50 m/s", "100 m/s", "200 m/s", "25 m/s"),
                            correctOptionIndex = 0,
                            explanation = "Comparing with standard harmonic wave equation y = A sin(ωt - kx):\nω = 100 rad/s, k = 2 rad/m.\nWave velocity v = ω / k = 100 / 2 = 50 m/s.",
                            hint = "Wave velocity is v = ω / k.",
                            topic = "Harmonic Wave Equation"
                        ),
                        Question(
                            id = "hcv_ch15_q2",
                            questionText = "The tension in a string is doubled while its mass and length remain constant. The fundamental frequency of vibration becomes:",
                            options = listOf("2 times", "√2 times", "4 times", "1 / √2 times"),
                            correctOptionIndex = 1,
                            explanation = "Fundamental frequency is f = (1 / 2L) √(T / μ).\nSince L and μ are unchanged, f ∝ √T.\nWhen tension is doubled (T' = 2T), f' = √2 · f.",
                            hint = "Frequency of stretched string is proportional to square root of tension.",
                            topic = "Sonometer Frequency"
                        ),
                        Question(
                            id = "hcv_ch15_q3",
                            questionText = "In a stationary wave, the distance between any two consecutive nodes is:",
                            options = listOf("λ", "λ / 2", "λ / 4", "2 λ"),
                            correctOptionIndex = 1,
                            explanation = "In a standing wave, nodes occur where amplitude is permanently zero. Consecutive nodes are separated by half a wavelength (λ / 2). The distance between a node and adjacent antinode is λ / 4.",
                            hint = "Two nodes span half a cycle of the standing wave.",
                            topic = "Standing Waves on Strings"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 16: Sound Waves
    // -------------------------------------------------------------
    private fun createChapter16(): Chapter {
        return Chapter(
            id = "phy_hcv_ch16",
            subject = SubjectType.PHYSICS,
            title = "Chapter 16: Sound Waves",
            order = 16,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch16",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch16",
                    chapterTitle = "Chapter 16: Sound Waves",
                    title = "Lec 01: Speed of Sound, Organ Pipes, Beats & Doppler",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Laplace's corrected speed of sound in ideal gas: v = √(γ P / ρ) = √(γ R T / M).",
                        "Closed organ pipe: Produces only odd harmonics f_n = (2n - 1)(v / 4L).",
                        "Open organ pipe: Produces all harmonics f_n = n(v / 2L).",
                        "Doppler effect: Apparent frequency f' = f ((v ± v_o) / (v ∓ v_s)); Beat frequency f_b = |f₁ - f₂|."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Newton's Formula & Laplace Correction"),
                        LectureTimestamp(840, "14:00", "Standing Waves in Open & Closed Resonance Tubes"),
                        LectureTimestamp(2100, "35:00", "Acoustic Beats Phenomena"),
                        LectureTimestamp(3120, "52:00", "Doppler Effect General Formula & Applications")
                    ),
                    dppId = "dpp_hcv_ch16"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch16",
                    number = 16,
                    title = "DPP 16: Speed, Resonance, Beats & Doppler Effect",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 16: Sound Waves",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch16_q1",
                            questionText = "An open organ pipe of length L₁ and a closed organ pipe of length L₂ vibrate in their respective fundamental modes with the same frequency. What is the ratio L₁ / L₂?",
                            options = listOf("1 : 1", "2 : 1", "1 : 2", "4 : 1"),
                            correctOptionIndex = 1,
                            explanation = "Fundamental frequency of an open pipe: f_open = v / (2 L₁).\nFundamental frequency of a closed pipe: f_closed = v / (4 L₂).\nGiven f_open = f_closed:\nv / (2 L₁) = v / (4 L₂) => 2 L₁ = 4 L₂ => L₁ / L₂ = 4 / 2 = 2 : 1.",
                            hint = "Open fundamental is v / (2L), closed fundamental is v / (4L).",
                            topic = "Organ Pipes Harmonics"
                        ),
                        Question(
                            id = "hcv_ch16_q2",
                            questionText = "Two tuning forks A and B produce 5 beats per second. When fork A is loaded with a little wax, the beat frequency decreases to 2 beats per second. If the frequency of B is 300 Hz, the frequency of A before waxing was:",
                            options = listOf("305 Hz", "295 Hz", "302 Hz", "298 Hz"),
                            correctOptionIndex = 0,
                            explanation = "Possible frequencies for A are f_A = 300 ± 5 => 305 Hz or 295 Hz.\nLoading with wax increases mass and therefore DECREASES frequency of fork A (f_A decreases).\nIf f_A = 305 Hz: when it decreases (e.g. to 302 Hz), beat frequency |f_A - 300| decreases from 5 to 2. This matches!\n(If f_A were 295, decreasing it would make beats |293 - 300| = 7, which would increase beats).\nHence, initial frequency of A was 305 Hz.",
                            hint = "Waxing lowers the frequency of a tuning fork.",
                            topic = "Beats and Tuning Forks"
                        ),
                        Question(
                            id = "hcv_ch16_q3",
                            questionText = "A siren emitting sound of frequency 1000 Hz moves towards a stationary observer with speed 34 m/s. The speed of sound in air is 340 m/s. The apparent frequency heard by the observer is:",
                            options = listOf("1111 Hz", "909 Hz", "1050 Hz", "1200 Hz"),
                            correctOptionIndex = 0,
                            explanation = "By Doppler formula for source moving towards stationary observer (v_o = 0):\nf' = f · (v / (v - v_s))\nf' = 1000 × (340 / (340 - 34)) = 1000 × (340 / 306) = 1000 × (10 / 9) ≈ 1111.1 Hz.",
                            hint = "Use f' = f [v / (v - v_s)].",
                            topic = "Doppler Effect"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 17: Light Waves
    // -------------------------------------------------------------
    private fun createChapter17(): Chapter {
        return Chapter(
            id = "phy_hcv_ch17",
            subject = SubjectType.PHYSICS,
            title = "Chapter 17: Light Waves",
            order = 17,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch17",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch17",
                    chapterTitle = "Chapter 17: Light Waves",
                    title = "Lec 01: Wave Optics, Huygens' Principle & YDSE",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Huygens' Principle: Every point on a wavefront acts as a source of secondary spherical wavelets.",
                        "Coherent sources have constant phase difference; constructive interference when Δx = n λ, destructive when Δx = (n - 1/2) λ.",
                        "Young's Double Slit Experiment: Fringe width β = λ D / d; shift due to thin film of thickness t is Δy = (μ - 1) t D / d.",
                        "Brewster's Law: When light is incident at polarizing angle i_p, reflected ray is completely polarized: tan i_p = μ."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Wavefronts and Wave Nature of Light"),
                        LectureTimestamp(840, "14:00", "YDSE Fringe Width and Intensity Distribution"),
                        LectureTimestamp(2100, "35:00", "Optical Path Difference in Thin Transparent Slabs"),
                        LectureTimestamp(2880, "48:00", "Diffraction & Malus / Brewster Polarization")
                    ),
                    dppId = "dpp_hcv_ch17"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch17",
                    number = 17,
                    title = "DPP 17: Huygens, Interference, YDSE & Polarization",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 17: Light Waves",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch17_q1",
                            questionText = "In Young's double slit experiment, if the separation between the slits is halved and the distance between the slits and the screen is doubled, the fringe width will:",
                            options = listOf(
                                "become 4 times",
                                "become 2 times",
                                "be halved",
                                "remain unchanged"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Fringe width is given by β = (λ D) / d.\nWhen D' = 2D and d' = d / 2:\nβ' = (λ (2D)) / (d / 2) = 4 (λ D / d) = 4 β.\nHence, fringe width becomes 4 times.",
                            hint = "Use the formula β = λ D / d.",
                            topic = "YDSE Fringe Width"
                        ),
                        Question(
                            id = "hcv_ch17_q2",
                            questionText = "When light is incident on a transparent glass slab at the Brewster angle of polarization i_p, the angle between the reflected ray and refracted ray is:",
                            options = listOf("45°", "60°", "90°", "180°"),
                            correctOptionIndex = 2,
                            explanation = "From Brewster's law, tan i_p = μ = sin i_p / cos i_p.\nFrom Snell's law, sin i_p / sin r = μ.\nTherefore, sin r = cos i_p = sin(90° - i_p) => r = 90° - i_p => i_p + r = 90°.\nThe angle between reflected ray and refracted ray is 180° - (i_p + r) = 180° - 90° = 90°.",
                            hint = "At Brewster's angle, reflected and refracted rays are mutually perpendicular.",
                            topic = "Brewster's Law & Polarization"
                        ),
                        Question(
                            id = "hcv_ch17_q3",
                            questionText = "In YDSE with monochromatic light, when a thin transparent sheet of mica of thickness t and refractive index μ is placed in front of one of the slits, the fringe pattern:",
                            options = listOf(
                                "shifts towards the side containing the sheet",
                                "shifts away from the side containing the sheet",
                                "remains unchanged",
                                "disappears completely"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Introducing a transparent plate introduces an additional optical path difference of (μ - 1)t in that beam.\nTo compensate and maintain zero optical path difference for the central bright fringe, the rays must shift towards the slit where the sheet is placed.\nFringe shift is Δy = ((μ - 1) t D) / d towards that slit.",
                            hint = "Light takes more optical time in the medium; pattern shifts toward that slit.",
                            topic = "Fringe Shift by Transparent Sheet"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 18: Geometrical Optics
    // -------------------------------------------------------------
    private fun createChapter18(): Chapter {
        return Chapter(
            id = "phy_hcv_ch18",
            subject = SubjectType.PHYSICS,
            title = "Chapter 18: Geometrical Optics",
            order = 18,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch18",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch18",
                    chapterTitle = "Chapter 18: Geometrical Optics",
                    title = "Lec 01: Reflection, Refraction, Lenses & Prisms",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 70,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Mirror formula: 1/v + 1/u = 1/f; Magnification m = -v/u.",
                        "Snell's Law: μ₁ sin i = μ₂ sin r; Total Internal Reflection when i > θ_c where sin θ_c = 1/μ.",
                        "Refraction at single spherical surface: μ₂/v - μ₁/u = (μ₂ - μ₁)/R.",
                        "Lens Maker's formula: 1/f = (μ - 1)(1/R₁ - 1/R₂); Prism deviation δ = i + e - A; at minimum deviation μ = sin((A + δ_m)/2) / sin(A/2)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Sign Conventions & Spherical Mirrors"),
                        LectureTimestamp(960, "16:00", "Total Internal Reflection & Critical Angle"),
                        LectureTimestamp(2160, "36:00", "Lens Maker's Formula & Thin Lens Combinations"),
                        LectureTimestamp(3240, "54:00", "Refraction Through Triangular Prisms")
                    ),
                    dppId = "dpp_hcv_ch18"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch18",
                    number = 18,
                    title = "DPP 18: Mirrors, Lenses, Prisms & Total Reflection",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 18: Geometrical Optics",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 25,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch18_q1",
                            questionText = "A convex lens of glass (μ = 1.5) has a focal length of 20 cm in air. When immersed completely in water (μ = 4/3), its focal length becomes:",
                            options = listOf("20 cm", "40 cm", "80 cm", "10 cm"),
                            correctOptionIndex = 2,
                            explanation = "In air: 1 / f_air = (μ_g - 1)(1/R₁ - 1/R₂) = (1.5 - 1) K = 0.5 K => K = 1 / (0.5 × 20) = 1/10.\nIn water: 1 / f_water = (μ_g / μ_w - 1) K = (1.5 / (4/3) - 1) K = (9/8 - 1) K = (1/8) K.\nTherefore, 1 / f_water = (1/8)(1/10) = 1/80 => f_water = 80 cm.",
                            hint = "Use Lens Maker's formula taking relative refractive index μ_g / μ_w.",
                            topic = "Focal Length in Liquids"
                        ),
                        Question(
                            id = "hcv_ch18_q2",
                            questionText = "The critical angle for total internal reflection from a medium to air is 45°. The velocity of light in that medium is (c = 3 × 10⁸ m/s):",
                            options = listOf(
                                "3 × 10⁸ m/s",
                                "2.12 × 10⁸ m/s",
                                "1.5 × 10⁸ m/s",
                                "2.5 × 10⁸ m/s"
                            ),
                            correctOptionIndex = 1,
                            explanation = "sin θ_c = 1 / μ => sin 45° = 1 / μ => 1 / √2 = 1 / μ => μ = √2 ≈ 1.414.\nVelocity of light in medium v = c / μ = (3 × 10⁸) / √2 ≈ 2.12 × 10⁸ m/s.",
                            hint = "Recall sin θ_c = 1 / μ and v = c / μ.",
                            topic = "Critical Angle & Speed of Light"
                        ),
                        Question(
                            id = "hcv_ch18_q3",
                            questionText = "For an equilateral prism of refractive index √3, the angle of minimum deviation δ_m is:",
                            options = listOf("30°", "45°", "60°", "90°"),
                            correctOptionIndex = 2,
                            explanation = "For an equilateral prism, angle of prism A = 60°.\nμ = sin((A + δ_m) / 2) / sin(A / 2)\n√3 = sin((60° + δ_m) / 2) / sin(30°)\n√3 = sin((60° + δ_m) / 2) / 0.5 => sin((60° + δ_m) / 2) = √3 / 2.\nTherefore, (60° + δ_m) / 2 = 60° => 60° + δ_m = 120° => δ_m = 60°.",
                            hint = "Use μ = sin((A + δ_m)/2) / sin(A/2) with A = 60°.",
                            topic = "Prism Minimum Deviation"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 19: Optical Instruments
    // -------------------------------------------------------------
    private fun createChapter19(): Chapter {
        return Chapter(
            id = "phy_hcv_ch19",
            subject = SubjectType.PHYSICS,
            title = "Chapter 19: Optical Instruments",
            order = 19,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch19",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch19",
                    chapterTitle = "Chapter 19: Optical Instruments",
                    title = "Lec 01: Microscopes, Telescopes & Resolving Power",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 55,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Simple microscope: Magnifying power m = 1 + D/f (image at near point D = 25 cm) and m = D/f (image at infinity).",
                        "Compound microscope: Magnifying power m = m_o × m_e ≈ -(L / f_o)(D / f_e).",
                        "Astronomical telescope in normal adjustment: Magnification m = -f_o / f_e; length of tube L = f_o + f_e.",
                        "Resolving limit of telescope Δθ = 1.22 λ / a; Resolving power is inversely proportional to resolving limit."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Magnifying Power of Simple Microscope"),
                        LectureTimestamp(720, "12:00", "Compound Microscope Ray Optics"),
                        LectureTimestamp(1800, "30:00", "Astronomical & Terrestrial Telescopes"),
                        LectureTimestamp(2520, "42:00", "Resolving Power & Rayleigh Criterion")
                    ),
                    dppId = "dpp_hcv_ch19"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch19",
                    number = 19,
                    title = "DPP 19: Microscopes, Telescopes & Vision",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 19: Optical Instruments",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch19_q1",
                            questionText = "An astronomical telescope has an objective of focal length 100 cm and an eyepiece of focal length 5 cm. In normal adjustment (image at infinity), the magnifying power and length of the telescope tube are:",
                            options = listOf(
                                "m = 20, L = 105 cm",
                                "m = 25, L = 100 cm",
                                "m = 20, L = 95 cm",
                                "m = 500, L = 105 cm"
                            ),
                            correctOptionIndex = 0,
                            explanation = "In normal adjustment:\nMagnifying power m = f_o / f_e = 100 / 5 = 20.\nLength of the telescope tube L = f_o + f_e = 100 + 5 = 105 cm.",
                            hint = "m = f_o / f_e and L = f_o + f_e.",
                            topic = "Astronomical Telescope"
                        ),
                        Question(
                            id = "hcv_ch19_q2",
                            questionText = "To increase the magnifying power and resolving power of an astronomical telescope, one should:",
                            options = listOf(
                                "increase the focal length and aperture of the objective",
                                "decrease the focal length and aperture of the objective",
                                "increase the focal length of eyepiece",
                                "decrease the aperture of objective"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Magnifying power is m = f_o / f_e, so increasing the objective focal length f_o increases m.\nResolving power is RP = a / (1.22 λ), so increasing the objective aperture 'a' increases resolving power.",
                            hint = "RP ∝ aperture and m ∝ f_o.",
                            topic = "Telescope Resolving Power"
                        ),
                        Question(
                            id = "hcv_ch19_q3",
                            questionText = "A nearsighted person cannot clearly see objects beyond 1.5 m. The power of the corrective lens required to view distant objects clearly is:",
                            options = listOf("-0.67 D", "+0.67 D", "-1.5 D", "+1.5 D"),
                            correctOptionIndex = 0,
                            explanation = "For myopic (nearsighted) eye viewing an object at infinity (u = -∞), image should be formed at the far point (v = -1.5 m).\n1/f = 1/v - 1/u = -1/1.5 - 0 = -1 / 1.5 = -2/3 m⁻¹.\nPower P = 1 / f = -2/3 ≈ -0.67 Dioptres (concave lens).",
                            hint = "P = 1/f with u = -∞ and v = -1.5 m.",
                            topic = "Defects of Vision (Myopia)"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 20: Dispersion, Spectra & Photometry
    // -------------------------------------------------------------
    private fun createChapter20(): Chapter {
        return Chapter(
            id = "phy_hcv_ch20",
            subject = SubjectType.PHYSICS,
            title = "Chapter 20: Dispersion, Spectra & Photometry",
            order = 20,
            lectures = listOf(
                Lecture(
                    id = "lec_hcv_ch20",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.PHYSICS,
                    chapterId = "phy_hcv_ch20",
                    chapterTitle = "Chapter 20: Dispersion, Spectra & Photometry",
                    title = "Lec 01: Dispersive Power, Chromatic Aberration & Photometry",
                    lectureNumber = 1,
                    instructor = "Prof. H.C. Verma Masterclass",
                    durationMinutes = 50,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Angular dispersion θ = δ_v - δ_r = (μ_v - μ_r) A.",
                        "Dispersive power ω = (μ_v - μ_r) / (μ_y - 1).",
                        "Condition for dispersion without average deviation and deviation without dispersion.",
                        "Inverse square law of illuminance: E = I cos θ / r²; Luminous flux unit is lumen (lm)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Angular Dispersion & Dispersive Power"),
                        LectureTimestamp(720, "12:00", "Achromatic Combination of Prisms"),
                        LectureTimestamp(1800, "30:00", "Luminous Flux, Intensity & Illuminance"),
                        LectureTimestamp(2520, "42:00", "Lambert's Cosine Law & Photometer")
                    ),
                    dppId = "dpp_hcv_ch20"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_hcv_ch20",
                    number = 20,
                    title = "DPP 20: Dispersion, Prisms & Photometry",
                    subject = SubjectType.PHYSICS,
                    chapterTitle = "Chapter 20: Dispersion, Spectra & Photometry",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = HCV_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "hcv_ch20_q1",
                            questionText = "The refractive indices of flint glass for violet, yellow and red colours are 1.632, 1.620 and 1.613 respectively. The dispersive power of flint glass is:",
                            options = listOf("0.0306", "0.019", "0.045", "0.062"),
                            correctOptionIndex = 0,
                            explanation = "Dispersive power is defined as:\nω = (μ_v - μ_r) / (μ_y - 1)\nω = (1.632 - 1.613) / (1.620 - 1) = 0.019 / 0.620 ≈ 0.0306.",
                            hint = "Formula for dispersive power is ω = (μ_v - μ_r) / (μ_y - 1).",
                            topic = "Dispersive Power"
                        ),
                        Question(
                            id = "hcv_ch20_q2",
                            questionText = "An electric lamp hangs 3 m directly above the center of a horizontal table. If the lamp is raised by 1 m, the illuminance at the point directly below the lamp will decrease by:",
                            options = listOf("43.75%", "25%", "50%", "36%"),
                            correctOptionIndex = 0,
                            explanation = "By inverse square law for normal incidence (cos θ = 1): E = I / r².\nInitially at r₁ = 3 m: E₁ = I / 3² = I / 9.\nWhen raised by 1 m, r₂ = 4 m: E₂ = I / 4² = I / 16.\nRatio E₂ / E₁ = 9 / 16 = 0.5625 = 56.25% of original.\nPercentage decrease = (1 - 0.5625) × 100% = 43.75%.",
                            hint = "Illuminance follows the inverse-square law E ∝ 1 / r².",
                            topic = "Photometry Inverse Square Law"
                        ),
                        Question(
                            id = "hcv_ch20_q3",
                            questionText = "To produce deviation without dispersion using two thin prisms of crown (ω₁, A₁) and flint glass (ω₂, A₂), the required condition is:",
                            options = listOf(
                                "(μ_v - μ_r) A + (μ'_v - μ'_r) A' = 0",
                                "(μ - 1) A + (μ' - 1) A' = 0",
                                "A / A' = - ω₁ / ω₂",
                                "ω₁ A = ω₂ A'"
                            ),
                            correctOptionIndex = 0,
                            explanation = "For no net dispersion, net angular dispersion must be zero:\nθ_net = θ₁ + θ₂ = 0\n(μ_v - μ_r) A + (μ'_v - μ'_r) A' = 0.",
                            hint = "Set net angular dispersion θ₁ + θ₂ = 0.",
                            topic = "Achromatic Combinations"
                        )
                    )
                )
            )
        )
    }
}
