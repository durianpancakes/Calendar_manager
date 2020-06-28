package com.example.calendarmanagerbeta;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirebaseLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirebaseLoginFragment extends Fragment {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase; //firebase database object, entry point for app to access database (needs dependencies)
    private DatabaseReference mMessagesDatabaseReference;  //class that references a specific part of the database. eg references messages portion of the database
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View firebaseView = inflater.inflate(R.layout.fragment_firebase_login, container, false);

        //mUsername = ANONYMOUS;

        //gets the references to the root node then says we are interested in messages part
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        //initializes auth
        mFirebaseAuth = FirebaseAuth.getInstance();



        /*// Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);*/

        //Initialize progress bar
        //mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //this firebaseauth is guaranteed to show whether user is signed in/out
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //can see sign in example in guide
                if(user != null) {
                    //user is signed in
                    //Toast.makeText(MainActivity.this, "You're now signed in. Welcome to FriendlyChat.", Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());
                    mUsername = user.getDisplayName();

                    System.out.println(mUsername + " has signed in");
                    if (mUsername != null) {
                        TextView userName = firebaseView.findViewById(R.id.firebase_username);
                        userName.setText(mUsername);
                    }

                } else {
                    onSignedOutCleanup();
                    System.out.println("user is signed out and name is" + mUsername);

                    if (mUsername != null) {
                        TextView userName = firebaseView.findViewById(R.id.firebase_username);
                        userName.setText(mUsername);
                    }

                    //user is signed out
                    //choose authentication providers(new method from github)
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    // Create and launch sign-in intent
                    // uses authUI firebase
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)     //not used in this tut
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);  //RC is quest code, flag for when return from starting the activity for result
                }



            }

        };

        System.out.println(mUsername + "is in oncreateview");
        if (mUsername != null) {
            TextView userName = firebaseView.findViewById(R.id.firebase_username);
            userName.setText(mUsername);
        }

        // Inflate the layout for this fragment
        return firebaseView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // if the activity being returned from was login flow, use this code
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(getActivity(), "Sign in cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish(); //finish activity
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_opt_signout:
                //signout, normal code for signout button
                AuthUI.getInstance().signOut(getActivity());
                //User = null;
                mUsername = "signed out";
                System.out.println(mUsername);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                //easy to sign out of firebase UI
                return true;


        }
        return false;

    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        // if resume and user is logged in, a database will be attached
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            //detaches
        }
        // make sure that when activity is destroyed when nth to do with signing out, user is cleaned up
        //detachDatabaseReadListener();
        //mMessageAdapter.clear();

    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        // when making friendlymessage object above at onClick, uses musername
        //so any message that is sent wil have the username
        //attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        //tears down UI
        mUsername = "Please Sign In!";
        //mMessageAdapter.clear(); //clears messages


    }
    /*private void attachDatabaseReadListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //called whenever message is added to messages list and also triggered

                    // for every child message in the list when the listener is first attached
                    //datasnapshot contains data at a specific location at the exact time the listener is triggered
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    //takes newly added message, converts into friendly message object and adds to adapter
                    mMessageAdapter.add(friendlyMessage);
                    // for list view
                    //friendly messages fields match the snapshot fields in DB
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //called when existing message changed
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //called when existing message is deleted
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //called if message change positions
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //an error occurred when trying to make changes(normally cos of permission issues)

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
            //only for messages node
        }
    }
    */


    /*private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}