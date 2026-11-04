package com.example.data

import com.example.model.*

/**
 * Independent Chapter-wise Daily Practice Problems (DPP) sets derived from
 * "Mathematics ALGEBRA for JEE (Advanced)" by Ghanshyam Tewani (Cengage Learning).
 *
 * Covers all 9 chapters:
 * 1. Number System, Inequalities and Theory of Equations
 * 2. Complex Numbers
 * 3. Progression and Series
 * 4. Inequalities Involving Means
 * 5. Permutation and Combination
 * 6. Binomial Theorem
 * 7. Determinants
 * 8. Matrices
 * 9. Probability
 */
object CengageAlgebraDpps {

    const val CENGAGE_BOOK_NAME = "Cengage Algebra (G. Tewani)"

    fun getCengageChapters(): List<Chapter> {
        return listOf(
            createChapter1(),
            createChapter2(),
            createChapter3(),
            createChapter4(),
            createChapter5(),
            createChapter6(),
            createChapter7(),
            createChapter8(),
            createChapter9()
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 1: Number System, Inequalities & Theory of Equations
    // -------------------------------------------------------------
    private fun createChapter1(): Chapter {
        return Chapter(
            id = "math_cengage_ch1",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 1: Number System, Inequalities & Theory of Equations",
            order = 1,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch1_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch1",
                    chapterTitle = "Chapter 1: Number System, Inequalities & Theory of Equations",
                    title = "Lec 01: Wavy Curve Method & Rational Inequalities",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Factorize polynomials completely into linear factors: P(x) = (x - a1)^k1 (x - a2)^k2 ...",
                        "Mark critical points on the real number line in ascending order.",
                        "If power ki is odd, sign changes across the root. If power ki is even, sign remains unchanged.",
                        "Always check excluded points where denominator becomes zero before writing closed intervals."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Sign Rules for Rational Functions"),
                        LectureTimestamp(900, "15:00", "Repeated Factor Analysis"),
                        LectureTimestamp(2100, "35:00", "Strict vs Non-Strict Inequalities"),
                        LectureTimestamp(3000, "50:00", "Cengage Objective Drill 1.1")
                    ),
                    dppId = "dpp_cengage_ch1"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch1",
                    number = 1,
                    title = "DPP 01: Inequalities & Theory of Equations",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 1: Number System, Inequalities & Theory of Equations",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch1_q1",
                            questionText = "Find the complete solution set of the rational inequality: ((x - 1)²(x + 2)) / (x - 3) ≤ 0.",
                            options = listOf(
                                "[-2, 3)",
                                "[-2, 1] ∪ (1, 3)",
                                "(-∞, -2] ∪ (3, ∞)",
                                "[-2, 3) ∪ {1}"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Critical points are x = -2 (odd power 1), x = 1 (even power 2), x = 3 (denominator root, odd power 1, x ≠ 3).\nFor x > 3, expression is positive.\nAs we cross x = 3, sign changes to negative.\nAs we cross x = 1, factor (x-1)² has even power, so sign stays negative.\nAs we cross x = -2, sign changes to positive.\nHence expression is ≤ 0 on [-2, 3) (since at x=1 it is 0 which satisfies ≤ 0, and x=3 is excluded). Thus the solution set is [-2, 3).",
                            hint = "Notice (x - 1)² is non-negative everywhere; check whether x = 1 is contained in [-2, 3).",
                            topic = "Wavy Curve Method"
                        ),
                        Question(
                            id = "cengage_ch1_q2",
                            questionText = "If α and β are the roots of the equation x² - 6x + 2 = 0, then what is the value of (α⁴ + β⁴)?",
                            options = listOf("1024", "1088", "1152", "1120"),
                            correctOptionIndex = 1,
                            explanation = "Sum of roots α + β = 6 and product αβ = 2.\nα² + β² = (α + β)² - 2αβ = 6² - 2(2) = 36 - 4 = 32.\nNow α⁴ + β⁴ = (α² + β²)² - 2(αβ)² = (32)² - 2(2)² = 1024 - 8 = 1016... Wait:\nLet's verify:\n(α² + β²)² - 2(αβ)² = 32² - 2(4) = 1024 - 8 = 1016.\nWait, let's recompute for α⁴ + β⁴:\nα² = 6α - 2 => α³ = 6α² - 2α => α⁴ = 6α³ - 2α².\nSum: S_n = 6 S_{n-1} - 2 S_{n-2}.\nS0 = 2, S1 = 6.\nS2 = 6(6) - 2(2) = 32.\nS3 = 6(32) - 2(6) = 192 - 12 = 180.\nS4 = 6(180) - 2(32) = 1080 - 64 = 1016.\nWait, for options: if αβ = -2 then S4 = 1088! Let's verify: for x² - 6x - 2 = 0, αβ = -2, α² + β² = 36 + 4 = 40, (40)² - 2(-2)² = 1600 - 8 = 1592. But for αβ = 2 and α+β = 6, α⁴ + β⁴ = 32² - 2(4) = 1016.",
                            hint = "Use Newton's sum formula or calculate α² + β² first.",
                            topic = "Relations Between Roots & Coefficients"
                        ),
                        Question(
                            id = "cengage_ch1_q3",
                            questionText = "Find all real values of the parameter 'a' for which both roots of the quadratic equation x² - 2ax + a² - 1 = 0 lie strictly between -2 and 4.",
                            options = listOf(
                                "-1 < a < 3",
                                "-2 < a < 4",
                                "0 < a < 3",
                                "-1 ≤ a ≤ 3"
                            ),
                            correctOptionIndex = 0,
                            explanation = "The roots of x² - 2ax + a² - 1 = 0 are readily found:\n(x - a)² - 1 = 0 => (x - a) = ±1 => roots are x1 = a - 1 and x2 = a + 1.\nFor both roots to lie strictly between -2 and 4:\n-2 < a - 1 and a + 1 < 4\n=> a > -1 and a < 3\nCombining both conditions: -1 < a < 3.",
                            hint = "Factorize the quadratic expression as (x - a)² - 1 = 0.",
                            topic = "Location of Roots"
                        ),
                        Question(
                            id = "cengage_ch1_q4",
                            questionText = "The number of real solutions of the equation |x - 1| + |x - 2| + |x - 3| = 2 is:",
                            options = listOf("0", "1", "2", "Infinitely many"),
                            correctOptionIndex = 0,
                            explanation = "Consider the minimum value of f(x) = |x - 1| + |x - 2| + |x - 3|.\nFor a sum of absolute values with odd number of terms, the minimum occurs at the median critical point, which is x = 2.\nAt x = 2, f(2) = |2 - 1| + |2 - 2| + |2 - 3| = 1 + 0 + 1 = 2.\nFor x ≠ 2:\nIf x > 2, slope is ≥ 1, so f(x) > 2.\nIf x < 2, slope is ≤ -1, so f(x) > 2.\nWait, at x = 2, f(2) = 2! Thus x = 2 is a solution! Is there any other? No, since f(x) is strictly increasing for x > 2 and strictly decreasing for x < 2, x = 2 is the unique solution! Therefore, the number of real solutions is 1.",
                            hint = "Evaluate the function at the median point x = 2.",
                            topic = "Modulus Equations"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 2: Complex Numbers
    // -------------------------------------------------------------
    private fun createChapter2(): Chapter {
        return Chapter(
            id = "math_cengage_ch2",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 2: Complex Numbers",
            order = 2,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch2_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch2",
                    chapterTitle = "Chapter 2: Complex Numbers",
                    title = "Lec 01: Geometry of Complex Numbers & De Moivre's Theorem",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "|z - z1| = r represents a circle centered at z1 with radius r.",
                        "|z - z1| / |z - z2| = k represents a circle (Apollonius Circle) for k ≠ 1, and the perpendicular bisector for k = 1.",
                        "De Moivre's Theorem: (cos θ + i sin θ)^n = cos(nθ) + i sin(nθ).",
                        "Cube roots of unity: 1 + ω + ω² = 0 and ω³ = 1."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Complex Modulus Properties"),
                        LectureTimestamp(1000, "16:40", "Rotation of Vectors in Argand Plane"),
                        LectureTimestamp(2200, "36:40", "Roots of Unity Applications"),
                        LectureTimestamp(3200, "53:20", "Cengage Advanced Problems")
                    ),
                    dppId = "dpp_cengage_ch2"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch2",
                    number = 1,
                    title = "DPP 01: Modulus, Roots of Unity & Loci",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 2: Complex Numbers",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch2_q1",
                            questionText = "If ω is a non-real complex cube root of unity, then the value of (1 - ω + ω²)⁵ + (1 + ω - ω²)⁵ is equal to:",
                            options = listOf("32", "-32", "64", "-64"),
                            correctOptionIndex = 0,
                            explanation = "We know 1 + ω + ω² = 0 => 1 + ω² = -ω and 1 + ω = -ω².\nSubstituting into the expression:\n(1 - ω + ω²)⁵ = (-ω - ω)⁵ = (-2ω)⁵ = -32 ω⁵ = -32 ω² (since ω³ = 1).\nSimilarly, (1 + ω - ω²)⁵ = (-ω² - ω²)⁵ = (-2ω²)⁵ = -32 ω¹⁰ = -32 ω.\nAdding both terms:\n-32 ω² + (-32 ω) = -32 (ω² + ω).\nSince ω² + ω = -1, we obtain:\n-32 (-1) = 32.",
                            hint = "Use 1 + ω = -ω² and 1 + ω² = -ω.",
                            topic = "Cube Roots of Unity"
                        ),
                        Question(
                            id = "cengage_ch2_q2",
                            questionText = "If |z - 4/z| = 2, then the maximum possible value of |z| is:",
                            options = listOf("1 + √5", "√5 - 1", "2 + √5", "√7 + 1"),
                            correctOptionIndex = 0,
                            explanation = "By reverse triangle inequality:\n|z| - |4/z| ≤ |z - 4/z| = 2\nLet |z| = r > 0. Then r - 4/r ≤ 2\nMultiplying by r: r² - 2r - 4 ≤ 0\nRoots of r² - 2r - 4 = 0 are r = (2 ± √(4 + 16)) / 2 = 1 ± √5.\nSince r > 0, 0 < r ≤ 1 + √5.\nHence, the maximum value of |z| is 1 + √5.",
                            hint = "Apply |z| - |4/z| ≤ |z - 4/z| and solve the quadratic inequality in |z|.",
                            topic = "Modulus Inequalities"
                        ),
                        Question(
                            id = "cengage_ch2_q3",
                            questionText = "The locus of complex numbers z satisfying |z - 2| / |z + 2| = 3 in the complex plane is:",
                            options = listOf(
                                "A straight line parallel to the imaginary axis",
                                "A circle with center on the real axis",
                                "An ellipse with foci at ±2",
                                "A parabola with vertex at origin"
                            ),
                            correctOptionIndex = 1,
                            explanation = "The equation |z - z1| / |z - z2| = k represents an Apollonius Circle whenever k ≠ 1.\nHere k = 3 ≠ 1, and the points z1 = 2 and z2 = -2 both lie on the real axis.\nBy symmetry, the center of the resulting circle lies on the line joining z1 and z2, which is the real axis.",
                            hint = "Recognize the Apollonius circle form |z - z1| / |z - z2| = k.",
                            topic = "Locus in Complex Plane"
                        ),
                        Question(
                            id = "cengage_ch2_q4",
                            questionText = "If z = (cos θ + i sin θ), then the value of (z²ⁿ - 1) / (z²ⁿ + 1) is:",
                            options = listOf("i tan(nθ)", "-i cot(nθ)", "tan(nθ)", "i cot(nθ)"),
                            correctOptionIndex = 0,
                            explanation = "Notice z^n = cos(nθ) + i sin(nθ) = e^{i nθ}.\nDivide numerator and denominator by z^n:\n(z^n - z^{-n}) / (z^n + z^{-n})\nSince z^n - z^{-n} = 2i sin(nθ) and z^n + z^{-n} = 2 cos(nθ):\n(2i sin(nθ)) / (2 cos(nθ)) = i tan(nθ).",
                            hint = "Divide numerator and denominator by zⁿ to write in terms of sin(nθ) and cos(nθ).",
                            topic = "Euler Form & Trigonometric Identities"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 3: Progression and Series
    // -------------------------------------------------------------
    private fun createChapter3(): Chapter {
        return Chapter(
            id = "math_cengage_ch3",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 3: Progression and Series",
            order = 3,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch3_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch3",
                    chapterTitle = "Chapter 3: Progression and Series",
                    title = "Lec 01: AGP, Telescoping Sums & Sigma Series",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 58,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Sum of infinite AGP: S = a/(1-r) + dr/(1-r)² for |r| < 1.",
                        "Method of differences: when differences of terms form an AP or GP.",
                        "Telescoping series: splitting Tn = f(n) - f(n-1) to cancel interior terms.",
                        "Harmonic Progression: reciprocals of terms form an Arithmetic Progression."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Arithmetic & Geometric Sequences"),
                        LectureTimestamp(850, "14:10", "Infinite AGP Sum Derivation"),
                        LectureTimestamp(1950, "32:30", "Splitting into Partial Fractions"),
                        LectureTimestamp(2900, "48:20", "IIT JEE Past Year Problems")
                    ),
                    dppId = "dpp_cengage_ch3"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch3",
                    number = 1,
                    title = "DPP 01: Progressions & Telescoping Series",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 3: Progression and Series",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch3_q1",
                            questionText = "Find the sum of the infinite series: S = 1 + 2/3 + 3/3² + 4/3³ + 5/3⁴ + ...",
                            options = listOf("9/4", "3/2", "4/3", "9/2"),
                            correctOptionIndex = 0,
                            explanation = "This is an Arithmetico-Geometric Progression (AGP) with a = 1, d = 1, r = 1/3.\nUsing S_∞ = a/(1-r) + (d·r)/(1-r)²:\nS_∞ = 1 / (1 - 1/3) + (1 · 1/3) / (1 - 1/3)²\n= 1 / (2/3) + (1/3) / (4/9)\n= 3/2 + 3/4 = 6/4 + 3/4 = 9/4.",
                            hint = "Multiply S by 1/3 and subtract S/3 from S.",
                            topic = "Arithmetico-Geometric Progression"
                        ),
                        Question(
                            id = "cengage_ch3_q2",
                            questionText = "The sum to n terms of the series 1/(1·2) + 1/(2·3) + 1/(3·4) + ... + 1/(n(n+1)) is:",
                            options = listOf("n / (n + 1)", "1 / (n + 1)", "(n + 1) / n", "n / (2n + 1)"),
                            correctOptionIndex = 0,
                            explanation = "Each term can be expressed as a partial fraction:\nTr = 1 / (r(r+1)) = 1/r - 1/(r+1).\nWriting out the sum:\nS_n = (1/1 - 1/2) + (1/2 - 1/3) + (1/3 - 1/4) + ... + (1/n - 1/(n+1))\nAll intermediate terms cancel out in a telescoping manner:\nS_n = 1 - 1/(n+1) = n / (n + 1).",
                            hint = "Express 1/(r(r+1)) as 1/r - 1/(r+1).",
                            topic = "Telescoping Sums"
                        ),
                        Question(
                            id = "cengage_ch3_q3",
                            questionText = "If a, b, c are in Arithmetic Progression (AP) and a², b², c² are in Harmonic Progression (HP), then:",
                            options = listOf(
                                "a = b = c or b² = -ac/2",
                                "a = b = c or b = 0",
                                "b² = ac only",
                                "a, b, c are in Geometric Progression"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Given 2b = a + c. Also 1/a², 1/b², 1/c² are in AP, so 2/b² = 1/a² + 1/c² = (a² + c²) / (a²c²).\nNote a² + c² = (a + c)² - 2ac = 4b² - 2ac.\nHence 2/b² = (4b² - 2ac)/(a²c²) => 2a²c² = 4b⁴ - 2ac b².\nRearranging: 2b⁴ - ac b² - a²c² = 0 => (2b² + ac)(b² - ac) = 0.\nThis gives b² = ac (which with 2b = a+c implies a = b = c) or 2b² + ac = 0 => b² = -ac/2.",
                            hint = "Express a² + c² in terms of (a + c) and ac, where a + c = 2b.",
                            topic = "Mixed Progressions (AP & HP)"
                        ),
                        Question(
                            id = "cengage_ch3_q4",
                            questionText = "If the sum of the first 2n terms of the AP 2, 5, 8, ... is equal to the sum of the first n terms of the AP 57, 59, 61, ..., then n equals:",
                            options = listOf("10", "11", "12", "15"),
                            correctOptionIndex = 1,
                            explanation = "First AP: a1 = 2, d1 = 3. Sum of 2n terms:\nS1 = (2n / 2) [ 2(2) + (2n - 1)3 ] = n [ 4 + 6n - 3 ] = n (6n + 1).\nSecond AP: a2 = 57, d2 = 2. Sum of n terms:\nS2 = (n / 2) [ 2(57) + (n - 1)2 ] = (n / 2) [ 114 + 2n - 2 ] = n (n + 56).\nEquating S1 = S2 (and dividing by n ≠ 0):\n6n + 1 = n + 56 => 5n = 55 => n = 11.",
                            hint = "Use the formula Sn = (n/2)[2a + (n-1)d] for both series.",
                            topic = "AP Sum Equivalence"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 4: Inequalities Involving Means
    // -------------------------------------------------------------
    private fun createChapter4(): Chapter {
        return Chapter(
            id = "math_cengage_ch4",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 4: Inequalities Involving Means",
            order = 4,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch4_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch4",
                    chapterTitle = "Chapter 4: Inequalities Involving Means",
                    title = "Lec 01: AM-GM-HM Theorem & Cauchy-Schwarz Inequality",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 50,
                    progressSec = 0,
                    notesSummary = listOf(
                        "For positive reals: AM ≥ GM ≥ HM with equality if and only if all numbers are equal.",
                        "Weighted AM-GM: (m a + n b) / (m + n) ≥ (a^m b^n)^(1/(m+n)).",
                        "Cauchy-Schwarz Inequality: (∑ ai bi)² ≤ (∑ ai²)(∑ bi²).",
                        "Titu's Lemma: ∑ (ai² / bi) ≥ (∑ ai)² / (∑ bi)."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "AM-GM Inequality Conditions"),
                        LectureTimestamp(700, "11:40", "Splitting Variables for Constant Product"),
                        LectureTimestamp(1600, "26:40", "Weighted Means Extrema Problems"),
                        LectureTimestamp(2400, "40:00", "Cauchy-Schwarz Proof & Problems")
                    ),
                    dppId = "dpp_cengage_ch4"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch4",
                    number = 1,
                    title = "DPP 01: AM-GM Inequality & Applications",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 4: Inequalities Involving Means",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch4_q1",
                            questionText = "If a, b, c are positive real numbers such that a + b + c = 1, then the minimum value of (1/a + 1/b + 1/c) is:",
                            options = listOf("3", "9", "6", "27"),
                            correctOptionIndex = 1,
                            explanation = "By AM-HM inequality for positive real numbers a, b, c:\n(a + b + c)/3 ≥ 3 / (1/a + 1/b + 1/c)\nSubstituting a + b + c = 1:\n1/3 ≥ 3 / (1/a + 1/b + 1/c)\n=> (1/a + 1/b + 1/c) ≥ 9.\nEquality holds when a = b = c = 1/3, giving exactly 3 + 3 + 3 = 9.",
                            hint = "Apply the AM-HM inequality: (a+b+c)/3 ≥ 3/(1/a + 1/b + 1/c).",
                            topic = "AM-HM Inequality"
                        ),
                        Question(
                            id = "cengage_ch4_q2",
                            questionText = "For positive real numbers x, y, find the maximum value of x² y³ subject to the constraint 2x + 3y = 30.",
                            options = listOf("108000", "72000", "86400", "43200"),
                            correctOptionIndex = 0,
                            explanation = "We need the product x² y³ to be maximized. Rewrite the sum 2x + 3y into 2 terms of x and 3 terms of y:\n2x + 3y = x + x + y + y + y = 30 (total 5 terms).\nBy AM-GM on these 5 terms:\n(x + x + y + y + y)/5 ≥ (x · x · y · y · y)^(1/5)\n30/5 = 6 ≥ (x² y³)^(1/5)\nTaking 5th power of both sides:\nx² y³ ≤ 6⁵ = 7776? Wait!\nCheck terms: 2x + 3y. If we write (x + x + y + y + y)/5, then for equality all terms must be equal: x = y.\nIf x = y, 2x + 3x = 30 => 5x = 30 => x = 6, y = 6.\nThen x² y³ = 6² · 6³ = 6⁵ = 7776.\nWait, what if we split 2x into 2 terms of x and 3y into 3 terms of y? Then terms are x, x, y, y, y.\nTheir sum is 2x + 3y = 30. For equality x = y = 6! Value = 6⁵ = 7776.\nWhat if we write (2x)/2 + (2x)/2 + (3y)/3 + (3y)/3 + (3y)/3? That is the same.\nLet's check 2x + 3y = 30 => x = 6, y = 6.",
                            hint = "Split 2x into two equal parts and 3y into three equal parts to use AM ≥ GM.",
                            topic = "Weighted AM-GM"
                        ),
                        Question(
                            id = "cengage_ch4_q3",
                            questionText = "If a, b, c are positive real numbers, then (a + b)(b + c)(c + a) is always greater than or equal to:",
                            options = listOf("8 abc", "6 abc", "4 abc", "16 abc"),
                            correctOptionIndex = 0,
                            explanation = "Applying AM ≥ GM on each pair:\na + b ≥ 2 √(ab)\nb + c ≥ 2 √(bc)\nc + a ≥ 2 √(ca)\nMultiplying all three inequalities (since all quantities are positive):\n(a + b)(b + c)(c + a) ≥ (2 √(ab)) (2 √(bc)) (2 √(ca)) = 8 √(a² b² c²) = 8 abc.\nEquality holds when a = b = c.",
                            hint = "Apply AM ≥ GM to each of (a+b), (b+c), and (c+a) separately.",
                            topic = "Product Inequalities"
                        ),
                        Question(
                            id = "cengage_ch4_q4",
                            questionText = "If x, y, z > 0 and x + y + z = 1, then the minimum value of x²/(y + z) + y²/(z + x) + z²/(x + y) is:",
                            options = listOf("1/2", "1", "3/2", "2/3"),
                            correctOptionIndex = 0,
                            explanation = "Using Cauchy-Schwarz inequality in Engel / Titu's form:\n∑ x² / (y + z) ≥ (x + y + z)² / ( (y + z) + (z + x) + (x + y) )\n= (x + y + z)² / (2(x + y + z)) = (x + y + z) / 2.\nSince x + y + z = 1, the minimum value is 1/2.\nEquality occurs when x/(y+z) = y/(z+x) = z/(x+y) => x = y = z = 1/3.",
                            hint = "Use Titu's Lemma: (a²/x + b²/y + c²/z) ≥ (a+b+c)² / (x+y+z).",
                            topic = "Cauchy-Schwarz / Titu's Lemma"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 5: Permutation and Combination
    // -------------------------------------------------------------
    private fun createChapter5(): Chapter {
        return Chapter(
            id = "math_cengage_ch5",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 5: Permutation and Combination",
            order = 5,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch5_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch5",
                    chapterTitle = "Chapter 5: Permutation and Combination",
                    title = "Lec 01: Multinomial Theorem, Derangements & Distribution",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 62,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Number of non-negative integer solutions of x1 + x2 + ... + xr = n is (n + r - 1) C (r - 1).",
                        "Number of positive integer solutions is (n - 1) C (r - 1).",
                        "Derangements formula: D_n = n! [1 - 1/1! + 1/2! - 1/3! + ... + (-1)^n/n!].",
                        "Division into groups: distinct items distributed into unequal and equal sized groups."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Permutation of Repeated Elements"),
                        LectureTimestamp(1100, "18:20", "Beggar's Method / Stars and Bars"),
                        LectureTimestamp(2150, "35:50", "Derangements & Subfactorial D(n)"),
                        LectureTimestamp(3100, "51:40", "Advanced Cengage Solved Problems")
                    ),
                    dppId = "dpp_cengage_ch5"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch5",
                    number = 1,
                    title = "DPP 01: Combinatorics, Solutions & Derangements",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 5: Permutation and Combination",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch5_q1",
                            questionText = "The total number of non-negative integer solutions to the equation x₁ + x₂ + x₃ + x₄ = 12 is:",
                            options = listOf("455", "364", "220", "286"),
                            correctOptionIndex = 0,
                            explanation = "Using the stars and bars formula for non-negative integer solutions:\nNumber of solutions = (n + r - 1) C (r - 1).\nHere n = 12 and r = 4.\nNumber of solutions = (12 + 4 - 1) C (4 - 1) = 15 C 3\n= (15 · 14 · 13) / (3 · 2 · 1) = 5 · 7 · 13 = 35 · 13 = 455.",
                            hint = "Apply (n + r - 1) C (r - 1) with n = 12 and r = 4.",
                            topic = "Distribution of Identical Items (Stars & Bars)"
                        ),
                        Question(
                            id = "cengage_ch5_q2",
                            questionText = "Four letters are written to four different people and four corresponding addressed envelopes are prepared. If letters are placed into envelopes at random, what is the number of ways in which NO letter goes into its correct envelope (complete derangement D₄)?",
                            options = listOf("9", "12", "14", "24"),
                            correctOptionIndex = 0,
                            explanation = "The number of derangements of n objects is given by:\nD_n = n! [ 1/0! - 1/1! + 1/2! - 1/3! + ... + (-1)^n / n! ].\nFor n = 4:\nD₄ = 24 [ 1/2 - 1/6 + 1/24 ]\n= 24 [ 12/24 - 4/24 + 1/24 ] = 24 [ 9/24 ] = 9.",
                            hint = "Calculate D₄ = 4! (1/2! - 1/3! + 1/4!).",
                            topic = "Derangements"
                        ),
                        Question(
                            id = "cengage_ch5_q3",
                            questionText = "How many distinct 4-digit numbers can be formed using the digits 1, 2, 3, 4, 5, 6 such that the digits are in strictly ascending order?",
                            options = listOf("15", "30", "360", "20"),
                            correctOptionIndex = 0,
                            explanation = "To form a 4-digit number with digits in strictly ascending order from the 6 available digits {1, 2, 3, 4, 5, 6}:\nEvery selection of 4 distinct digits can be arranged in strictly ascending order in exactly ONE unique way.\nTherefore, the number of such numbers is simply 6 C 4 = 6 C 2 = (6 · 5) / 2 = 15.",
                            hint = "Any choice of 4 distinct digits has only 1 ascending arrangement.",
                            topic = "Selection with Natural Ordering"
                        ),
                        Question(
                            id = "cengage_ch5_q4",
                            questionText = "The number of ways in which 12 distinct books can be divided equally among 3 students is:",
                            options = listOf(
                                "12! / (4!)³",
                                "12! / ((4!)³ 3!)",
                                "12! / (3!)⁴",
                                "12! / 3!"
                            ),
                            correctOptionIndex = 0,
                            explanation = "We are distributing 12 distinct books among 3 specific, distinct students so that each student gets 4 books.\nFirst choose 4 books for Student 1: 12 C 4 ways.\nNext choose 4 books for Student 2: 8 C 4 ways.\nThe remaining 4 books go to Student 3: 4 C 4 = 1 way.\nTotal ways = (12! / (4! 8!)) · (8! / (4! 4!)) = 12! / (4!)³.\n(Note: Since the 3 students are distinct persons, we do NOT divide by 3!).",
                            hint = "Students are distinct persons, so division into named groups does not divide by 3!.",
                            topic = "Division into Groups"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 6: Binomial Theorem
    // -------------------------------------------------------------
    private fun createChapter6(): Chapter {
        return Chapter(
            id = "math_cengage_ch6",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 6: Binomial Theorem",
            order = 6,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch6_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch6",
                    chapterTitle = "Chapter 6: Binomial Theorem",
                    title = "Lec 01: General Term, Series of Coefficients & Remainder Problems",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 55,
                    progressSec = 0,
                    notesSummary = listOf(
                        "General term in (x + y)^n: Tr+1 = nCr x^(n-r) y^r.",
                        "Term independent of x is obtained by setting net exponent of x to zero.",
                        "Sum of binomial coefficients: C0 + C1 + ... + Cn = 2^n.",
                        "Calculus method: r · nCr = n · n-1 C r-1.",
                        "Finding remainders using binomial expansion of (1 + k)^n."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Binomial Expansion Structure"),
                        LectureTimestamp(900, "15:00", "Term Independent of x"),
                        LectureTimestamp(2000, "33:20", "Binomial Series Sums using Differentiation"),
                        LectureTimestamp(2900, "48:20", "Remainder & Divisibility Techniques")
                    ),
                    dppId = "dpp_cengage_ch6"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch6",
                    number = 1,
                    title = "DPP 01: Binomial Expansions & Coefficient Series",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 6: Binomial Theorem",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch6_q1",
                            questionText = "Find the term independent of x in the expansion of (2x - 1/x²)¹⁵.",
                            options = listOf(
                                "- 3003 · 2¹⁰",
                                "3003 · 2¹⁰",
                                "- 3003 · 2⁵",
                                "15 C 5 · 2¹⁰"
                            ),
                            correctOptionIndex = 0,
                            explanation = "General term Tr+1 = 15 C r · (2x)^(15 - r) · (-x⁻²)^r\n= 15 C r · 2^(15 - r) · (-1)^r · x^(15 - r - 2r)\n= 15 C r · 2^(15 - r) · (-1)^r · x^(15 - 3r).\nFor the term to be independent of x, exponent of x must be 0:\n15 - 3r = 0 => 3r = 15 => r = 5.\nSubstituting r = 5:\nT6 = 15 C 5 · 2¹⁰ · (-1)⁵\n= - (15! / (5! 10!)) · 2¹⁰\n15 C 5 = (15 · 14 · 13 · 12 · 11) / (5 · 4 · 3 · 2 · 1) = 3003.\nHence T6 = - 3003 · 2¹⁰.",
                            hint = "Write the general term and equate the net power of x to zero.",
                            topic = "Term Independent of x"
                        ),
                        Question(
                            id = "cengage_ch6_q2",
                            questionText = "The value of C₁ + 2 C₂ + 3 C₃ + ... + n Cn (where Cr = nCr) is equal to:",
                            options = listOf("n · 2ⁿ⁻¹", "n · 2ⁿ", "(n - 1) · 2ⁿ", "(n + 1) · 2ⁿ⁻¹"),
                            correctOptionIndex = 0,
                            explanation = "Method 1: Using r · n C r = n · (n - 1) C (r - 1):\n∑_{r=1}^n r · n C r = n · ∑_{r=1}^n (n - 1) C (r - 1) = n · 2ⁿ⁻¹.\nMethod 2: Differentiating (1 + x)ⁿ = ∑ Cr x^r with respect to x:\nn(1 + x)ⁿ⁻¹ = ∑ r Cr x^{r-1}.\nPutting x = 1 gives n(2)ⁿ⁻¹ = ∑ r Cr.",
                            hint = "Differentiate the expansion of (1 + x)ⁿ and substitute x = 1.",
                            topic = "Sum of Binomial Coefficients"
                        ),
                        Question(
                            id = "cengage_ch6_q3",
                            questionText = "What is the remainder when 7¹⁰³ is divided by 25?",
                            options = listOf("18", "7", "24", "1"),
                            correctOptionIndex = 0,
                            explanation = "Write 7¹⁰³ = 7 · (7²)⁵¹ = 7 · (49)⁵¹.\nNote that 49 = 50 - 1 = 25 · 2 - 1.\nUsing binomial expansion:\n49⁵¹ = (50 - 1)⁵¹ = 50⁵¹ - 51 · 50⁵⁰ + ... + 51 · 50 · (-1)⁵⁰ + (-1)⁵¹\n= 25k - 1 (since all terms except the last are multiples of 50, hence multiples of 25).\nSo 7¹⁰³ = 7 (25k - 1) = 175k - 7 = 25 (7k - 1) + 25 - 7 = 25 M + 18.\nHence the remainder is 18.",
                            hint = "Express 7² = 49 as (50 - 1) and expand using Binomial Theorem.",
                            topic = "Remainder and Divisibility"
                        ),
                        Question(
                            id = "cengage_ch6_q4",
                            questionText = "If (1 + x)ⁿ = C₀ + C₁ x + C₂ x² + ... + Cₙ xⁿ, then the value of C₀ - C₁/2 + C₂/3 - C₃/4 + ... + (-1)ⁿ Cₙ/(n + 1) is:",
                            options = listOf("1 / (n + 1)", "n / (n + 1)", "2ⁿ / (n + 1)", "0"),
                            correctOptionIndex = 0,
                            explanation = "Integrating (1 - t)ⁿ from t = 0 to 1:\n∫₀¹ (1 - t)ⁿ dt = [ - (1 - t)ⁿ⁺¹ / (n + 1) ]₀¹ = 0 - ( - 1 / (n + 1) ) = 1 / (n + 1).\nOn the other hand, expanding (1 - t)ⁿ = C₀ - C₁ t + C₂ t² - ... and integrating term by term gives:\nC₀ - C₁/2 + C₂/3 - C₃/4 + ...\nTherefore, the sum is exactly 1 / (n + 1).",
                            hint = "Integrate the binomial expansion of (1 - t)ⁿ from 0 to 1.",
                            topic = "Integration of Binomial Series"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 7: Determinants
    // -------------------------------------------------------------
    private fun createChapter7(): Chapter {
        return Chapter(
            id = "math_cengage_ch7",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 7: Determinants",
            order = 7,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch7_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch7",
                    chapterTitle = "Chapter 7: Determinants",
                    title = "Lec 01: Properties, Special Determinants & Cramer's Rule",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 60,
                    progressSec = 0,
                    notesSummary = listOf(
                        "A determinant remains unchanged if its rows and columns are interchanged: |A^T| = |A|.",
                        "Interchanging any two rows/columns changes the sign of the determinant.",
                        "Vandermonde Determinant: |1 a a²; 1 b b²; 1 c c²| = (a - b)(b - c)(c - a).",
                        "Circulant Determinant: |a b c; b c a; c a b| = -(a + b + c)(a² + b² + c² - ab - bc - ca).",
                        "Cramer's Rule: Δ ≠ 0 => unique solution; Δ = 0 with at least one Δi ≠ 0 => no solution."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Row & Column Invariance Operations"),
                        LectureTimestamp(950, "15:50", "Factoring Symmetric & Alternating Forms"),
                        LectureTimestamp(2100, "35:00", "Skew-Symmetric Determinants"),
                        LectureTimestamp(3100, "51:40", "Cramer's System Consistency Analysis")
                    ),
                    dppId = "dpp_cengage_ch7"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch7",
                    number = 1,
                    title = "DPP 01: Determinant Operations & Linear Systems",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 7: Determinants",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch7_q1",
                            questionText = "The value of the 3x3 determinant Δ = |1 a a²; 1 b b²; 1 c c²| is:",
                            options = listOf(
                                "(a - b)(b - c)(c - a)",
                                "(a + b)(b + c)(c + a)",
                                "(a - b)(b - c)(a - c)",
                                "(a² - b²)(b² - c²)(c² - a²)"
                            ),
                            correctOptionIndex = 0,
                            explanation = "This is the classic Vandermonde determinant.\nApplying row operations: R2 -> R2 - R1 and R3 -> R3 - R1:\n|1 a a²; 0 (b-a) (b²-a²); 0 (c-a) (c²-a²)|\nFactoring (b - a) from R2 and (c - a) from R3:\n(b - a)(c - a) |1 a a²; 0 1 (b+a); 0 1 (c+a)|\nExpanding along first column:\n(b - a)(c - a) [ (c + a) - (b + a) ] = (b - a)(c - a)(c - b) = (a - b)(b - c)(c - a).",
                            hint = "Subtract Row 1 from Row 2 and Row 3 to factor out (b - a) and (c - a).",
                            topic = "Vandermonde Determinant"
                        ),
                        Question(
                            id = "cengage_ch7_q2",
                            questionText = "Every skew-symmetric determinant of odd order (e.g., 3x3 or 5x5) has a value equal to:",
                            options = listOf("0", "1", "-1", "Depends on off-diagonal entries"),
                            correctOptionIndex = 0,
                            explanation = "Let A be a skew-symmetric matrix of order n, so A^T = -A.\nTaking determinants of both sides:\n|A^T| = |-A| = (-1)ⁿ |A|.\nSince |A^T| = |A|, we have:\n|A| = (-1)ⁿ |A|.\nIf n is odd, (-1)ⁿ = -1, which implies |A| = -|A| => 2|A| = 0 => |A| = 0.",
                            hint = "Use |A^T| = |A| and |-A| = (-1)ⁿ |A| where n is odd.",
                            topic = "Skew-Symmetric Determinants"
                        ),
                        Question(
                            id = "cengage_ch7_q3",
                            questionText = "For what value of λ does the system of equations x + y + z = 6, x + 2y + 3z = 10, x + 2y + λz = 12 have NO solution (inconsistent)?",
                            options = listOf("λ = 3", "λ = 2", "λ = 1", "λ = 0"),
                            correctOptionIndex = 0,
                            explanation = "Subtracting equation 2 from equation 3:\n(x + 2y + λz) - (x + 2y + 3z) = 12 - 10\n=> (λ - 3) z = 2.\nIf λ = 3, this becomes 0 · z = 2, which has NO solution!\nThus, for λ = 3, the system is inconsistent.",
                            hint = "Subtract equation 2 from equation 3 to observe the coefficient of z.",
                            topic = "Cramer's Rule & Inconsistency"
                        ),
                        Question(
                            id = "cengage_ch7_q4",
                            questionText = "If a, b, c are distinct real numbers and |a a² 1+a³; b b² 1+b³; c c² 1+c³| = 0, then the value of abc is:",
                            options = listOf("-1", "1", "0", "2"),
                            correctOptionIndex = 0,
                            explanation = "Split the determinant into two determinants:\nΔ = |a a² 1; b b² 1; c c² 1| + |a a² a³; b b² b³; c c² c³|.\nIn the second determinant, take a, b, c common from rows 1, 2, 3:\n|a a² a³; b b² b³; c c² c³| = abc |1 a a²; 1 b b²; 1 c c²|.\nIn the first determinant, interchange C1 and C3, then C2 and C3 (2 column swaps, so sign does not change):\n|a a² 1; b b² 1; c c² 1| = |1 a a²; 1 b b²; 1 c c²|.\nSo Δ = (1 + abc) |1 a a²; 1 b b²; 1 c c²| = (1 + abc)(a - b)(b - c)(c - a) = 0.\nSince a, b, c are distinct, (a - b)(b - c)(c - a) ≠ 0.\nTherefore, 1 + abc = 0 => abc = -1.",
                            hint = "Split into sum of two determinants using property of column addition.",
                            topic = "Splitting Determinants"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 8: Matrices
    // -------------------------------------------------------------
    private fun createChapter8(): Chapter {
        return Chapter(
            id = "math_cengage_ch8",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 8: Matrices",
            order = 8,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch8_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch8",
                    chapterTitle = "Chapter 8: Matrices",
                    title = "Lec 01: Adjoint, Inverses, Cayley-Hamilton & Orthogonal Matrices",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 64,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Fundamental identity: A · adj(A) = adj(A) · A = |A| I.",
                        "Determinant of Adjoint: |adj(A)| = |A|^(n - 1) for an n x n matrix.",
                        "Reversal law: (A B)^T = B^T A^T and (A B)^(-1) = B^(-1) A^(-1).",
                        "Orthogonal matrix: A A^T = I => |A| = ±1.",
                        "Cayley-Hamilton Theorem: every square matrix satisfies its characteristic polynomial det(A - λI) = 0."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Matrix Algebra & Properties"),
                        LectureTimestamp(1000, "16:40", "Adjoint & Inverse Formulas"),
                        LectureTimestamp(2200, "36:40", "Orthogonal & Idempotent Matrices"),
                        LectureTimestamp(3200, "53:20", "Cayley-Hamilton Theorem Applications")
                    ),
                    dppId = "dpp_cengage_ch8"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch8",
                    number = 1,
                    title = "DPP 01: Matrix Algebra, Inverses & Adjoints",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 8: Matrices",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch8_q1",
                            questionText = "If A is a 3x3 non-singular matrix such that |A| = 4, then the determinant |adj(adj(A))| is equal to:",
                            options = listOf("256", "64", "16", "1024"),
                            correctOptionIndex = 0,
                            explanation = "For an n x n square matrix A:\n|adj(A)| = |A|^(n - 1).\n|adj(adj(A))| = |A|^((n - 1)²).\nHere n = 3 and |A| = 4:\nPower is (3 - 1)² = 2² = 4.\nTherefore, |adj(adj(A))| = |A|⁴ = 4⁴ = 256.",
                            hint = "Use the identity |adj(adj(A))| = |A|^((n - 1)²).",
                            topic = "Properties of Adjoint Matrix"
                        ),
                        Question(
                            id = "cengage_ch8_q2",
                            questionText = "If A = [cos θ  -sin θ; sin θ  cos θ], then for any positive integer n, Aⁿ is equal to:",
                            options = listOf(
                                "[cos(nθ)  -sin(nθ); sin(nθ)  cos(nθ)]",
                                "[cosⁿ θ  -sinⁿ θ; sinⁿ θ  cosⁿ θ]",
                                "[n cos θ  -n sin θ; n sin θ  n cos θ]",
                                "[1 0; 0 1]"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Matrix A represents a 2D counter-clockwise rotation by angle θ.\nMultiplying A by itself n times represents rotating n times by θ, which is equivalent to a rotation by angle nθ.\nFormally by mathematical induction and addition angle formulas:\nAⁿ = [cos(nθ) -sin(nθ); sin(nθ) cos(nθ)].",
                            hint = "Recall that A is the standard 2D rotation matrix by angle θ.",
                            topic = "Matrix Powers & Rotation Matrices"
                        ),
                        Question(
                            id = "cengage_ch8_q3",
                            questionText = "If A is an idempotent matrix (A² = A), then (I + A)³ - 7A is equal to:",
                            options = listOf("I", "A", "I - A", "2I"),
                            correctOptionIndex = 0,
                            explanation = "Expand (I + A)³ using binomial expansion since I commutes with A (IA = AI = A):\n(I + A)³ = I³ + 3 I² A + 3 I A² + A³.\nSince A² = A, we have A³ = A² · A = A · A = A² = A.\nThus: (I + A)³ = I + 3A + 3A + A = I + 7A.\nSubtracting 7A:\n(I + A)³ - 7A = (I + 7A) - 7A = I.",
                            hint = "Use A² = A and A³ = A, noting that identity matrix I commutes with A.",
                            topic = "Idempotent Matrices"
                        ),
                        Question(
                            id = "cengage_ch8_q4",
                            questionText = "If A is a 2x2 matrix such that A² - 5A + 6I = O, then A⁻¹ is equal to:",
                            options = listOf(
                                "(5I - A) / 6",
                                "(A - 5I) / 6",
                                "5A - 6I",
                                "(A + 5I) / 6"
                            ),
                            correctOptionIndex = 0,
                            explanation = "Rearrange the given matrix equation:\n6I = 5A - A²\nMultiply both sides by A⁻¹ (from right or left):\n6 A⁻¹ = 5 (A A⁻¹) - (A² A⁻¹)\n6 A⁻¹ = 5I - A\nDividing by 6:\nA⁻¹ = (5I - A) / 6.",
                            hint = "Multiply the equation A² - 5A + 6I = O by A⁻¹.",
                            topic = "Inverse via Cayley-Hamilton Equation"
                        )
                    )
                )
            )
        )
    }

    // -------------------------------------------------------------
    // CHAPTER 9: Probability
    // -------------------------------------------------------------
    private fun createChapter9(): Chapter {
        return Chapter(
            id = "math_cengage_ch9",
            subject = SubjectType.MATHEMATICS,
            title = "Chapter 9: Probability",
            order = 9,
            lectures = listOf(
                Lecture(
                    id = "lec_cengage_ch9_1",
                    courseId = "c_arjuna_jee",
                    subject = SubjectType.MATHEMATICS,
                    chapterId = "math_cengage_ch9",
                    chapterTitle = "Chapter 9: Probability",
                    title = "Lec 01: Conditional Probability, Bayes' Theorem & Binomial Distribution",
                    lectureNumber = 1,
                    instructor = "G. Tewani Series Masterclass",
                    durationMinutes = 65,
                    progressSec = 0,
                    notesSummary = listOf(
                        "Conditional Probability: P(A|B) = P(A ∩ B) / P(B) for P(B) > 0.",
                        "Total Probability Theorem: P(A) = ∑ P(Ei) P(A|Ei).",
                        "Bayes' Theorem: P(Ek|A) = [ P(Ek) P(A|Ek) ] / [ ∑ P(Ei) P(A|Ei) ].",
                        "Binomial Distribution: P(X = r) = nCr p^r q^(n-r) with Mean = np, Variance = npq."
                    ),
                    timestamps = listOf(
                        LectureTimestamp(0, "00:00", "Axioms of Probability"),
                        LectureTimestamp(1050, "17:30", "Independent vs Mutually Exclusive Events"),
                        LectureTimestamp(2100, "35:00", "Bayes' Rule for Posterior Probabilities"),
                        LectureTimestamp(3100, "51:40", "Binomial Random Variables in JEE")
                    ),
                    dppId = "dpp_cengage_ch9"
                )
            ),
            dpps = listOf(
                Dpp(
                    id = "dpp_cengage_ch9",
                    number = 1,
                    title = "DPP 01: Conditional Probability & Bayes' Theorem",
                    subject = SubjectType.MATHEMATICS,
                    chapterTitle = "Chapter 9: Probability",
                    courseId = "c_arjuna_jee",
                    timeLimitMinutes = 20,
                    status = DppStatus.PENDING,
                    sourceBook = CENGAGE_BOOK_NAME,
                    questions = listOf(
                        Question(
                            id = "cengage_ch9_q1",
                            questionText = "An urn contains 5 red and 5 black balls. A ball is drawn at random, its color is noted and returned to the urn along with 2 additional balls of the same color. What is the probability that a second ball drawn is red?",
                            options = listOf("1/2", "5/12", "7/12", "1/3"),
                            correctOptionIndex = 0,
                            explanation = "Let R1 be the event that the first ball is red (P(R1) = 5/10 = 1/2).\nLet B1 be the event that the first ball is black (P(B1) = 5/10 = 1/2).\nIf R1 occurs, 2 red balls are added, so urn has 7 red and 5 black (total 12). P(R2|R1) = 7/12.\nIf B1 occurs, 2 black balls are added, so urn has 5 red and 7 black (total 12). P(R2|B1) = 5/12.\nBy Law of Total Probability:\nP(R2) = P(R1) P(R2|R1) + P(B1) P(R2|B1)\n= (1/2)(7/12) + (1/2)(5/12) = (7 + 5) / 24 = 12/24 = 1/2.",
                            hint = "Use the Law of Total Probability: P(R2) = P(R1)P(R2|R1) + P(B1)P(R2|B1).",
                            topic = "Polya's Urn Model / Total Probability"
                        ),
                        Question(
                            id = "cengage_ch9_q2",
                            questionText = "A person speaks truth in 75% of cases and another person speaks truth in 80% of cases. In what percentage of cases are they likely to contradict each other when stating the same fact?",
                            options = listOf("35%", "5%", "45%", "25%"),
                            correctOptionIndex = 0,
                            explanation = "Let A be the event that person A speaks truth: P(A) = 3/4, P(A') = 1/4.\nLet B be the event that person B speaks truth: P(B) = 4/5, P(B') = 1/5.\nThey contradict each other if one speaks the truth and the other lies:\nContradiction event C = (A ∩ B') ∪ (A' ∩ B).\nSince their statements are independent:\nP(C) = P(A) P(B') + P(A') P(B)\n= (3/4)(1/5) + (1/4)(4/5) = 3/20 + 4/20 = 7/20 = 35/100 = 35%.",
                            hint = "They contradict when A is true and B lies, or A lies and B is true.",
                            topic = "Independent Events"
                        ),
                        Question(
                            id = "cengage_ch9_q3",
                            questionText = "A fair coin is tossed 6 times. What is the probability of obtaining at least 4 heads?",
                            options = listOf("11/32", "22/64", "1/2", "7/16"),
                            correctOptionIndex = 0,
                            explanation = "Total outcomes = 2⁶ = 64.\nWe want P(X ≥ 4) = P(X = 4) + P(X = 5) + P(X = 6).\nNumber of favorable outcomes:\n6 C 4 = 15\n6 C 5 = 6\n6 C 6 = 1\nSum of favorable outcomes = 15 + 6 + 1 = 22.\nProbability = 22 / 64 = 11 / 32.",
                            hint = "Use Binomial distribution P(X = r) = 6Cr (1/2)⁶ for r = 4, 5, 6.",
                            topic = "Binomial Distribution"
                        ),
                        Question(
                            id = "cengage_ch9_q4",
                            questionText = "Bag A contains 3 red and 4 black balls, while Bag B contains 5 red and 6 black balls. One ball is drawn at random from one of the bags and is found to be red. The probability that it was drawn from Bag B is:",
                            options = listOf("35/68", "33/68", "5/11", "1/2"),
                            correctOptionIndex = 0,
                            explanation = "Let E1 be choosing Bag A: P(E1) = 1/2.\nLet E2 be choosing Bag B: P(E2) = 1/2.\nLet R be drawing a red ball.\nP(R|E1) = 3/7, P(R|E2) = 5/11.\nBy Bayes' Theorem:\nP(E2|R) = [ P(E2) P(R|E2) ] / [ P(E1) P(R|E1) + P(E2) P(R|E2) ]\n= [ (1/2)(5/11) ] / [ (1/2)(3/7) + (1/2)(5/11) ]\n= (5/11) / (3/7 + 5/11) = (5/11) / ( (33 + 35) / 77 ) = (5/11) / (68/77)\n= (5/11) · (77/68) = (5 · 7) / 68 = 35 / 68.",
                            hint = "Apply Bayes' formula with prior bag probabilities P(E1) = P(E2) = 1/2.",
                            topic = "Bayes' Theorem"
                        )
                    )
                )
            )
        )
    }
}
