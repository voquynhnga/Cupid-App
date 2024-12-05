package com.midterm.destined.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class DB {
    private static FirebaseDatabase databaseInstance;
    private static FirebaseFirestore firestoreInstance;
    private static FirebaseStorage storageInstance;

    private static CollectionReference usersCollection;
    private static CollectionReference matchesCollection;

    private static DatabaseReference chatsRef;
    private static DatabaseReference storyRef;

    // FirebaseDatabase Instance
    public static FirebaseDatabase getDatabaseInstance() {
        if (databaseInstance == null) {
            databaseInstance = FirebaseDatabase.getInstance();
        }
        return databaseInstance;
    }

    // FirebaseFirestore Instance
    public static FirebaseFirestore getFirestoreInstance() {
        if (firestoreInstance == null) {
            firestoreInstance = FirebaseFirestore.getInstance();
        }
        return firestoreInstance;
    }

    // FirebaseStorage Instance
    public static FirebaseStorage getStorageInstance() {
        if (storageInstance == null) {
            storageInstance = FirebaseStorage.getInstance();
        }
        return storageInstance;
    }

    // Firestore Collection References
    public static CollectionReference getUsersCollection() {
        if (usersCollection == null) {
            usersCollection = getFirestoreInstance().collection("users");
        }
        return usersCollection;
    }

    public static CollectionReference getMatchesCollection() {
        if (matchesCollection == null) {
            matchesCollection = getFirestoreInstance().collection("matches");
        }
        return matchesCollection;
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
            return getUsersCollection().document(currentUser.getUid());
        } else {
            throw new IllegalStateException("No authenticated user found.");
        }
    }

    // Firestore document for a specific user
    public static DocumentReference getUserDocument(String userID) {
        return getUsersCollection().document(userID);
    }
}
