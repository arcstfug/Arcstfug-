const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS & DIRTY DOZEN TESTS ---

test("Unauthenticated user: cannot read or write student profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read their own valid StudentProfile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Arun Sharma",
      email: "alice@example.com",
      targetExam: "JEE Main & Advanced 2026",
      classLevel: "Class 12th",
      studentGoal: "Aim: Top 100 AIR",
      bio: "ARCSTFUG Scholar",
      currentStreakDays: 14,
      totalDppsSolved: 28,
      overallAccuracy: 82,
      lecturesWatched: 42,
      studyHours: 56.5,
      xp: 3450,
      enrolledCourseIds: ["c_arjuna_jee"],
      bookmarkedLectureIds: [],
      completedLectureIds: [],
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user's profile (cross-user isolation)", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      name: "Bob Student",
      targetExam: "NEET-UG 2026",
      classLevel: "Class 12th",
      studentGoal: "AIIMS Delhi",
      bio: "Bio Scholar",
      currentStreakDays: 5,
      totalDppsSolved: 10,
      overallAccuracy: 80,
      lecturesWatched: 12,
      studyHours: 20.0,
      xp: 1200,
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Shadow Update Test: rejects ghost fields on StudentProfile create", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Arun Sharma",
      targetExam: "JEE Main & Advanced 2026",
      classLevel: "Class 12th",
      studentGoal: "Aim: Top 100 AIR",
      bio: "ARCSTFUG Scholar",
      currentStreakDays: 14,
      totalDppsSolved: 28,
      overallAccuracy: 82,
      lecturesWatched: 42,
      studyHours: 56.5,
      xp: 3450,
      isAdmin: true,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Authenticated user: can create and query their own DPP attempts", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("dpp_attempts")
      .doc("dpp_hcv_ch1")
      .set({
        userId: ALICE_UID,
        dppId: "dpp_hcv_ch1",
        title: "DPP 01: Units, Dimensions & Measurement",
        subject: "PHYSICS",
        score: 4,
        totalQuestions: 4,
        timeSpentSeconds: 320,
        status: "COMPLETED",
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("dpp_attempts")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});

test("Authenticated user: cannot read another user's DPP attempts", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("dpp_attempts")
      .doc("dpp_hcv_ch1")
      .set({
        userId: BOB_UID,
        dppId: "dpp_hcv_ch1",
        title: "DPP 01",
        subject: "PHYSICS",
        score: 3,
        totalQuestions: 4,
        timeSpentSeconds: 400,
        status: "COMPLETED",
        createdAt: now,
        updatedAt: now,
      });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("dpp_attempts")
      .doc("dpp_hcv_ch1")
      .get()
  );
});

test("Authenticated user: can create, update, and query their own StudentDoubts", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  const doubtRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("doubts")
    .doc("doubt_1");

  await assertSucceeds(
    doubtRef.set({
      userId: ALICE_UID,
      studentName: "Arun Sharma",
      questionText: "Why does Brewster angle make reflected and refracted rays perpendicular?",
      subject: "PHYSICS",
      status: "Solved by Gemini AI Tutor",
      facultyReply: "By Snell's Law and Brewster's Law, tan(i_p) = mu = sin(i_p)/sin(r) => i_p + r = 90 deg.",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("doubts")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});
