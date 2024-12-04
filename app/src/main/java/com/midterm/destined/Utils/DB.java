package com.midterm.destined.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class DB {
    private static FirebaseDatabase database;
    private static FirebaseFirestore firestore;
    private static DatabaseReference chatsRef;
    private static DatabaseReference storyRef;

    // Firebase Realtime Database instance
    public static FirebaseDatabase getDatabaseInstance() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    // Firestore instance
    public static FirebaseFirestore getFirestoreInstance() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

    // Firestore Collections
    public static CollectionReference getUsersCollection() {
        return getFirestoreInstance().collection("users");
    }

    public static CollectionReference getMatchesCollection() {
        return getFirestoreInstance().collection("matches");
    }

    // Realtime Database References
    public static DatabaseReference getChatsRef() {
        if (chatsRef == null) {
            chatsRef = getDatabaseInstance().getReference("chats");
        }
        return chatsRef;
    }

    public static DatabaseReference getStoryRef() {
        if (storyRef == null) {
            storyRef = getDatabaseInstance().getReference("Story");
        }
        return storyRef;
    }

    // Current authenticated user
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Current authenticated user's Firestore document
    public static DocumentReference getCurrentUserDocument() {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null) {
            return getFirestoreInstance().collection("users").document(currentUser.getUid());
        } else {
            throw new IllegalStateException("No authenticated user found.");
        }
    }

    public static DocumentReference getUserDocument(String userID) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null) {
            return getFirestoreInstance().collection("users").document(userID);
        } else {
            throw new IllegalStateException("No user found.");
        }
    }

}
