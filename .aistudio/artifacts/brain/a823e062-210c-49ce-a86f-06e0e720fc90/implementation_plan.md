# H.C. Verma Volume 1 Objective I & Objective II DPP Expansion (Chapters 1–22)

Expand the Physics Daily Practice Problem (DPP) library in **ARCSTFUG** with the complete set of **Objective I** (single-choice) and **Objective II** (one-or-more-than-one correct) questions from *Concepts of Physics (Volume 1)* by Dr. H. C. Verma across Chapters 1 through 22, and upgrade the DPP test and result engine to support interactive multi-select checkbox answering for Objective II problems.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product and UX decisions were confirmed during interactive clarification and govern this implementation:

- **Confirmed Decision 1 — Chapter & DPP Organization**: Merge all provided Objective I and Objective II questions directly into the corresponding H.C. Verma chapter DPP sets and split out **Chapter 20: Dispersion and Spectra**, **Chapter 21: Speed of Light**, and **Chapter 22: Photometry** as distinct, independent chapters (expanding the H.C. Verma syllabus from 20 to 22 chapters).
- **Confirmed Decision 2 — Multi-Select Checkbox Mode for Objective II**: Upgrade the DPP practice and evaluation screens to support **Multi-Select Checkbox Mode** for Objective II questions where one or more options can be toggled, while preserving single-select radio behavior for Objective I questions.
- **Confirmed Decision 3 — Canonical H.C. Verma Completion**: Restore any truncated options in the pasted excerpt (such as Chapter 1 Objective I Q2, Chapter 2 Objective II Q2, Chapter 17 Objective I Q6, and Chapter 22 Objective I Q2) using the exact canonical H.C. Verma textbook options along with complete step-by-step mathematical derivations and exam tips.

---

## 1. Overview & Core Concept

- **What It Does**:
  - Integrates all 48 extracted **Objective I** and **Objective II** conceptual and numerical problems from H.C. Verma Volume 1 into their respective chapter-wise DPPs across Chapters 1–22 (`Introduction to Physics`, `Physics and Mathematics`, `Rest and Motion: Kinematics`, `The Forces`, `Friction`, `Circular Motion`, `Rotational Mechanics`, `Gravitation`, `Simple Harmonic Motion`, `Wave Motion`, `Sound Waves`, `Light Waves`, `Geometrical Optics`, `Optical Instruments`, `Dispersion and Spectra`, `Speed of Light`, and `Photometry`).
  - Introduces native support for **Objective II (One or More Than One Correct)** questions in the timed DPP solving flow and post-submission solution analysis screen.
- **Target Audience / Persona**: JEE Main, JEE Advanced, and NEET aspirants practicing chapter-wise H.C. Verma Objective I & II problems on mobile and tablet devices.
- **Key Value**: Mirrors the authentic JEE Advanced / H.C. Verma distinction between single-choice (`Objective I`) and multiple-correct (`Objective II`) problems, eliminating guesswork and providing full derivations for every option.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Browsing Chapters 1–22**: In the Practice (DPP Hub) and Batch Syllabus views, students see all 22 distinct H.C. Verma Volume 1 chapters—including dedicated chapters for *Chapter 20: Dispersion and Spectra*, *Chapter 21: Speed of Light*, and *Chapter 22: Photometry*—with updated question counts.
  2. **Solving Objective I vs. Objective II Questions**:
     - When a student opens a chapter DPP, each question displays a clear question-type pill badge in the header: **`Objective I • Single Choice`** or **`Objective II • Multiple Correct (Select all that apply)`**.
     - For **Objective I** questions, tapping an option selects that single option (replacing any previous choice).
     - For **Objective II** questions, each option card displays a rounded multi-select checkbox badge (`A`, `B`, `C`, `D` with check indicator); tapping an option toggles it in or out of the student's selected set for that question.
  3. **Reviewing Solutions & Multi-Select Evaluation**:
     - Upon submitting the DPP, a question is marked **Correct ✓** if the student's selected option set exactly matches the full set of correct options for that question.
     - In the solution breakdown card, every correct option is highlighted in emerald green (`✓ Correct Option`) and any erroneously selected option is highlighted in rose red (`✗ Your Selection`), followed by a step-by-step analysis of why each statement `(a)`, `(b)`, `(c)`, and `(d)` is true or false.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: High-contrast academic dark/light Material 3 aesthetic consistent with ARCSTFUG's Indigo, Cyan, Amber, and Emerald palette.
  - *Color Palette & Mood*:
    - Primary interactive accents: `IndigoPrimary` (`#4F46E5`) and `IndigoContainer` for active selections.
    - Objective II multi-select badge: `AmberContainer` (`#FEF3C7`) with `AmberStreak` (`#F59E0B`) accent border to immediately alert students that multiple options may be correct.
    - Evaluation feedback: `EmeraldSuccess` (`#10B981`) for verified correct options and `RoseError` (`#F43F5E`) for incorrect selections.
  - *Typography & Hierarchy*: Clear `labelSmall` badges for question classification (`Objective I` vs `Objective II`), bold `bodyLarge` question stems with generous `24.sp` line height for mathematical expressions (`ML⁻¹T⁻²`, `∫ dx / √(2ax − x²)`), and structured multi-line derivations in solution cards.
  - *Component Styling & Layout*: 8.dp grid alignment, minimum 48.dp touch targets on all option cards and palette pills, and full edge-to-edge `WindowInsets.navigationBars` padding on bottom action bars.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1 — Backward-Compatible Multi-Select Question Model**:
  - *Chosen Approach*: Extend the `Question` model with `correctOptionIndices: Set<Int>` (defaulting to `setOf(correctOptionIndex)`) and `isMultiSelect: Boolean` (true for Objective II questions), and support `userMultiAnswers: Map<Int, Set<Int>>` alongside `userAnswers: Map<Int, Int>` in `Dpp`.
  - *Why*: Preserves 100% backward compatibility with all existing single-choice DPPs (Cengage Algebra, Chemistry, Biology, and AI-generated DPPs) while enabling true multi-checkbox selection and exact-set grading for H.C. Verma Objective II questions.
  - *Alternatives Considered*: Converting Objective II questions into synthetic single-choice strings ("Both A and C are correct") was rejected per user preference for authentic multi-select checkboxes.
- **Decision 2 — Distinct Chapters 20, 21, and 22**:
  - *Chosen Approach*: Separate the previously combined Chapter 20 into three independent chapters matching the H.C. Verma Volume 1 table of contents:
    - **Chapter 20**: *Dispersion and Spectra*
    - **Chapter 21**: *Speed of Light*
    - **Chapter 22**: *Photometry*
  - *Why*: Matches the exact chapter numbering in the user's Study Notebook and textbook.
- **Decision 3 — Real-Time Cloud Sync Compatibility**:
  - *Chosen Approach*: Keep the Firestore `DppAttempt` document schema unchanged (`dppId`, `title`, `subject`, `score`, `totalQuestions`, `timeSpentSeconds`, `status`) so completed H.C. Verma DPPs (including Chapters 21 and 22 and Objective II scores) automatically sync with Firestore under the existing deployed security rules.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌───────────────────────────────────────────────────────────────────────────┐
│                        ARCSTFUG Presentation Layer                        │
│                                                                           │
│  ┌─────────────────────────┐               ┌───────────────────────────┐  │
│  │    DppSolvingScreen     │               │      DppResultScreen      │  │
│  │  • Objective I / II Tag │               │  • Multi-Option Highlight │  │
│  │  • Single Radio Mode    │──────────────▶│  • Exact-Set Grading      │  │
│  │  • Multi Checkbox Mode  │   onSubmit    │  • Step-by-Step Analysis  │  │
│  └────────────┬────────────┘               └─────────────▲─────────────┘  │
└───────────────┼──────────────────────────────────────────┼────────────────┘
                │ onSelectOption /                         │ StateFlow<Dpp>
                │ onToggleMultiOption                      │
┌───────────────▼──────────────────────────────────────────┴────────────────┐
│                        ViewModel & Domain State                           │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                            EduViewModel                             │  │
│  │  • dppUserAnswers: StateFlow<Map<Int, Int>>                         │  │
│  │  • dppUserMultiAnswers: StateFlow<Map<Int, Set<Int>>>               │  │
│  │  • selectDppOption(qIdx, optIdx) — auto-routes Single vs Multi      │  │
│  │  • submitCurrentDpp() — evaluates exact Set<Int> match & syncs      │  │
│  └──────────────────┬───────────────────────────────────┬──────────────┘  │
└─────────────────────┼───────────────────────────────────┼─────────────────┘
                      │                                   │
        ┌─────────────▼─────────────┐       ┌─────────────▼─────────────┐
        │       EduRepository       │       │    CloudSyncRepository    │
        │  • HcVermaPhysicsDpps     │       │  • /users/{uid}/          │
        │    (Chapters 1 to 22)     │       │    dpp_attempts/{dppId}   │
        │  • Objective I & II Bank  │       │  • Real-time Firestore    │
        └───────────────────────────┘       └───────────────────────────┘
```

### Data Model & State

- **`Question`**:
  - `id: String`
  - `questionText: String`
  - `options: List<String>`
  - `correctOptionIndex: Int` (primary correct index for single-choice compatibility)
  - `correctOptionIndices: Set<Int>` (defaults to `setOf(correctOptionIndex)`; contains all correct indices `0..3` for Objective II questions)
  - `isMultiSelect: Boolean` (defaults to `correctOptionIndices.size > 1`, or explicitly `true` for all Objective II questions even when only one option happens to be true)
  - `explanation: String`
  - `hint: String`
  - `topic: String`
- **`Dpp`**:
  - Retains `userAnswers: Map<Int, Int>` and adds `userMultiAnswers: Map<Int, Set<Int>> = emptyMap()` so each question index maps to the full set of option indices selected by the student.
  - Helper `fun isQuestionAnswered(qIdx: Int): Boolean` returns whether `userMultiAnswers[qIdx]?.isNotEmpty() == true || userAnswers.containsKey(qIdx)`.
  - Helper `fun isQuestionCorrect(qIdx: Int): Boolean` compares the student's selected `Set<Int>` against `questions[qIdx].correctOptionIndices`.

### Interactive Component & State Mapping

- **Option Selection (`EduViewModel.selectDppOption(questionIndex, optionIndex)`)**:
  - Inspects `currentQuestion.isMultiSelect`:
    - If `false` (**Objective I**): sets `userMultiAnswers[questionIndex] = setOf(optionIndex)` and `userAnswers[questionIndex] = optionIndex`.
    - If `true` (**Objective II**): toggles `optionIndex` inside `userMultiAnswers[questionIndex]` (removing the key if the set becomes empty, or updating `userAnswers[questionIndex]` to the first selected index for backward compatibility).
- **Question Palette & Submit Dialog (`DppSolvingScreen`)**:
  - Marks a question pill in the palette as answered whenever `userMultiAnswers[index]?.isNotEmpty() == true` or `userAnswers.containsKey(index)`.
  - Displays an **`Objective II • Multiple Correct`** badge on Objective II questions and renders a multi-select check indicator inside each option row.
- **Result Breakdown (`DppResultScreen`)**:
  - For each option `optIdx` of question `q`:
    - `isCorrectAnswer = q.correctOptionIndices.contains(optIdx)`
    - `isUserPicked = selectedSet.contains(optIdx)`
  - Highlights all correct options in green (`✓ Correct Option`), any missed correct option as `✓ Correct Option (Missed)`, and any wrongly picked option in red (`✗ Your Choice`).
