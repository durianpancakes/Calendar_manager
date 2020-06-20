package com.example.calendarmanagerbeta;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class firebaseHelperFragment {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;


    /*private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;*/


    private FirebaseDatabase mFirebaseDatabase; //firebase database object, entry point for app to access database (needs dependencies)
    private DatabaseReference mMessagesDatabaseReference;  //class that references a specific part of the database. eg references messages portion of the database
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    //in oncreate

    /*// Initialize references to views
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mMessageListView = (ListView) findViewById(R.id.messageListView);
    mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
    mMessageEditText = (EditText) findViewById(R.id.messageEditText);
    mSendButton = (Button) findViewById(R.id.sendButton);*/


    //gets the references to the root node then says we are interested in messages part
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

    //initializes auth
    mFirebaseAuth = FirebaseAuth.getInstance();

    // Initialize message ListView and its adapter
    List<FriendlyMessage> friendlyMessages = new ArrayList<>();
    mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
    mMessageListView.setAdapter(mMessageAdapter);


























}
