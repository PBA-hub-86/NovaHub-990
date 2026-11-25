const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const assert = require("node:assert");
const fs = require("node:fs");

const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

let testEnv;

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort
    }
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

test("Unauthenticated user: cannot read or write gallery_images, users, public_profiles, or arcade_scores", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("gallery_images").where("visibility", "==", "PUBLIC").get()
  );
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("public_profiles").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("arcade_scores").get());
  await assertFails(
    unauthDb.collection("gallery_images").doc("img_unauth").set({
      userId: ALICE_UID,
      authorName: "Anon",
      title: "Blocked",
      imageUrl: "https://images.unsplash.com/photo-1519608487953-e999c86e7455?w=800",
      category: "Urban Night",
      visibility: "PUBLIC",
      likesCount: 0,
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000)
    })
  );
});

test("Authenticated user: enforces PII isolation on users/{uid} and validates public_profiles/{uid}", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  // Alice writes her private user profile (contains email PII)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      displayName: "Alice Explorer",
      handle: "@alice",
      email: "alice@example.com",
      roleTitle: "Game Creator",
      bio: "Building neon arcade worlds",
      avatarInitials: "AE",
      accentTheme: "GOLD",
      createdAt: now,
      updatedAt: now
    })
  );

  // Bob CANNOT read Alice's private user document
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());

  // Bob CANNOT write email into public_profiles
  await assertFails(
    bobDb.collection("public_profiles").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      email: "bob@example.com",
      roleTitle: "Photographer",
      avatarInitials: "BO",
      updatedAt: now
    })
  );

  // Alice writes her public_profiles doc (no email) and Bob can read it
  await assertSucceeds(
    aliceDb.collection("public_profiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Explorer",
      handle: "@alice",
      roleTitle: "Game Creator",
      bio: "Building neon arcade worlds",
      avatarInitials: "AE",
      avatarUrl: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
      accentTheme: "GOLD",
      highScoreTotal: 4200,
      updatedAt: now
    })
  );
  await assertSucceeds(bobDb.collection("public_profiles").doc(ALICE_UID).get());
});

test("Authenticated user: gallery_images create, filtered visibility query, likes update, and delete", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);

  // Create PUBLIC image as Alice (mirrors FirestoreRepository.addRemoteGalleryImage)
  await assertSucceeds(
    aliceDb.collection("gallery_images").doc("img_pub").set({
      userId: ALICE_UID,
      authorName: "Alice Explorer",
      title: "Midnight Neon Overpass",
      imageUrl: "https://images.unsplash.com/photo-1519608487953-e999c86e7455?w=800",
      category: "Urban Night",
      description: "Rain reflections at twilight",
      visibility: "PUBLIC",
      likesCount: 0,
      createdAt: created,
      updatedAt: created
    })
  );

  // Create PRIVATE image as Alice
  await assertSucceeds(
    aliceDb.collection("gallery_images").doc("img_priv").set({
      userId: ALICE_UID,
      authorName: "Alice Explorer",
      title: "Private Draft",
      imageUrl: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
      category: "Golden Dawn",
      description: "Internal shot",
      visibility: "PRIVATE",
      likesCount: 0,
      createdAt: created,
      updatedAt: created
    })
  );

  // Reject spoofed userId or non-https imageUrl or future timestamp
  await assertFails(
    bobDb.collection("gallery_images").doc("img_spoof").set({
      userId: ALICE_UID,
      authorName: "Bob",
      title: "Spoofed",
      imageUrl: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
      category: "Abstract",
      visibility: "PUBLIC",
      likesCount: 0,
      createdAt: created,
      updatedAt: created
    })
  );
  await assertFails(
    bobDb.collection("gallery_images").doc("img_bad_url").set({
      userId: BOB_UID,
      authorName: "Bob",
      title: "Bad URL",
      imageUrl: "file:///etc/passwd",
      category: "Abstract",
      visibility: "PUBLIC",
      likesCount: 0,
      createdAt: created,
      updatedAt: created
    })
  );
  await assertFails(
    bobDb.collection("gallery_images").doc("img_future").set({
      userId: BOB_UID,
      authorName: "Bob",
      title: "Future",
      imageUrl: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800",
      category: "Abstract",
      visibility: "PUBLIC",
      likesCount: 0,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: created
    })
  );

  // Filtered PUBLIC query succeeds for Bob; unfiltered query on mixed visibility fails; Bob cannot read Alice's private doc
  const res = await assertSucceeds(
    bobDb.collection("gallery_images").where("visibility", "==", "PUBLIC").limit(60).get()
  );
  assert.strictEqual(res.size, 1);
  await assertFails(bobDb.collection("gallery_images").get());
  await assertFails(bobDb.collection("gallery_images").doc("img_priv").get());

  // Bob can increment likesCount on PUBLIC image, cannot mutate title or delete
  await assertSucceeds(
    bobDb.collection("gallery_images").doc("img_pub").update({
      likesCount: 1,
      updatedAt: new Date(Date.now() - 1000)
    })
  );
  await assertFails(
    bobDb.collection("gallery_images").doc("img_pub").update({
      title: "Hijacked",
      updatedAt: new Date(Date.now() - 1000)
    })
  );
  await assertFails(bobDb.collection("gallery_images").doc("img_pub").delete());

  // Alice can delete her own image
  await assertSucceeds(aliceDb.collection("gallery_images").doc("img_pub").delete());
});

test("Authenticated user: arcade_scores validates gameId, score bounds, and immutability", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("arcade_scores").doc("score_1").set({
      userId: ALICE_UID,
      playerName: "Alice Explorer",
      avatarInitials: "AE",
      gameId: "cyber_breaker",
      gameTitle: "Cyber Brick Breaker",
      score: 1850,
      createdAt: now
    })
  );

  await assertFails(
    aliceDb.collection("arcade_scores").doc("score_bad_game").set({
      userId: ALICE_UID,
      playerName: "Alice Explorer",
      avatarInitials: "AE",
      gameId: "hacked_game",
      gameTitle: "Fake",
      score: 500,
      createdAt: now
    })
  );

  await assertFails(
    aliceDb.collection("arcade_scores").doc("score_neg").set({
      userId: ALICE_UID,
      playerName: "Alice Explorer",
      avatarInitials: "AE",
      gameId: "void_runner",
      gameTitle: "Void Runner",
      score: -5,
      createdAt: now
    })
  );

  await assertFails(aliceDb.collection("arcade_scores").doc("score_1").update({ score: 99999 }));
  await assertFails(bobDb.collection("arcade_scores").doc("score_1").delete());
  await assertSucceeds(bobDb.collection("arcade_scores").limit(40).get());
});
