package com.example.calendarmanagerbeta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.widget.Toast;


import com.alamkanak.weekview.WeekViewEvent;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.User;

import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentCallback ,KeywordManagerFragment.KeywordManagerCallback, CalendarDayFragment.EventAddedListener, CalendarWeekFragment.EventAddedListener, EmailFragment.EmailFragmentCallback, TimePickerFragment.OnTimeReceiveCallback, DatePickerFragment.OnDateReceiveCallback, NavigationView.OnNavigationItemSelectedListener,  NusmodsFragment.moduleParamsChangedListener, NusmodsFragment.removeModuleListener{
    private static final String SAVED_IS_SIGNED_IN = "isSignedIn";
    private static final String SAVED_USER_NAME = "userName";
    private static final String SAVED_USER_EMAIL = "userEmail";
    public static final String PREFS_NAME = "CalendarManagerPreferences";

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private View mHeaderView;
    private Drawable mProfilePicture;
    private TextDrawable mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private boolean mIsSignedIn = false;
    private String mUserName = null;
    private String mUserEmail = null;
    private AuthenticationHelper mAuthHelper = null;
    private boolean mAttemptInteractiveSignIn = false;
    private FirebaseAuth mFirebaseAuth;
    private Spinner mToolbarSpinner;
    private ArrayList<String> mUserKeywords = new ArrayList<>();
    public static ArrayList<KeywordInfo> mUserKeywordDelta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbarSpinner = toolbar.findViewById(R.id.toolbar_spinner);

        mDrawer = findViewById(R.id.drawer_layout);

        // Add the hamburger menu icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);

        // Set user name and email
        mHeaderView = mNavigationView.getHeaderView(0);
        setSignedInState(mIsSignedIn);

        // Listen for item select events on menu
        mNavigationView.setNavigationItemSelectedListener(this);

        // Load the home fragment by default on startup
        if (savedInstanceState == null) {
            openHomeFragment(mUserName);
        } else {
            // Restore state
            mIsSignedIn = savedInstanceState.getBoolean(SAVED_IS_SIGNED_IN);
            mUserName = savedInstanceState.getString(SAVED_USER_NAME);
            mUserEmail = savedInstanceState.getString(SAVED_USER_EMAIL);
            setSignedInState(mIsSignedIn);
        }

        // <InitialLoginSnippet>
        showProgressBar();
        // Get the authentication helper
        AuthenticationHelper.getInstance(getApplicationContext(),
                new IAuthenticationHelperCreatedListener() {
                    @Override
                    public void onCreated(AuthenticationHelper authHelper) {
                        mAuthHelper = authHelper;
                        if (!mIsSignedIn) {
                            doSilentSignIn(false);
                        } else {
                            hideProgressBar();
                        }
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("AUTH", "Error creating auth helper", exception);
                    }
                });
        // </InitialLoginSnippet>

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_IS_SIGNED_IN, mIsSignedIn);
        outState.putString(SAVED_USER_NAME, mUserName);
        outState.putString(SAVED_USER_EMAIL, mUserEmail);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Load the fragment that corresponds to the selected item
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                openHomeFragment(mUserName);
                break;
            case R.id.nav_email:
                openEmailFragment();
                break;
            case R.id.nav_signin:
                signIn();
                break;
            case R.id.nav_signout:
                signOut();
                break;
            case R.id.calendar_day_view:
                openDayCalendar();
                break;
            case R.id.calendar_week_view:
                openWeekCalendar();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_menu, menu);

        menu.findItem(R.id.toolbar_cal_btn).setVisible(mIsSignedIn);
        menu.setGroupVisible(R.id.toolbar_opt_group, mIsSignedIn);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.toolbar_cal_btn).setVisible(mIsSignedIn);
        menu.setGroupVisible(R.id.toolbar_opt_group, mIsSignedIn);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.toolbar_opt_modules:
                openNusmodsFragment();
                uncheckAllNavItems();
                return true;
            case R.id.toolbar_opt_keywords:
                openKeywordManagerFragment();
                uncheckAllNavItems();
                return true;
        }
        return false;
    }

    private void uncheckAllNavItems(){
        int size = mNavigationView.getMenu().size();
        for(int i = 0; i < size; i++){
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showProgressBar()
    {
        FrameLayout container = findViewById(R.id.fragment_container);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        container.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar()
    {
        FrameLayout container = findViewById(R.id.fragment_container);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    // Update the menu and get the user's name and email
    private void setSignedInState(boolean isSignedIn) {
        mIsSignedIn = isSignedIn;

        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.drawer_menu);

        Menu menu = mNavigationView.getMenu();
        // Hide/show the Sign in, Calendar, email and Sign Out buttons
        if (isSignedIn) {
            menu.findItem(R.id.account_submenu).getSubMenu().removeItem(R.id.nav_signin);
        } else {
            menu.removeItem(R.id.nav_home);
            menu.removeItem(R.id.nav_email);
            menu.findItem(R.id.calendar_submenu).getSubMenu().removeItem(R.id.calendar_day_view);
            menu.findItem(R.id.calendar_submenu).getSubMenu().removeItem(R.id.calendar_week_view);
            menu.findItem(R.id.account_submenu).getSubMenu().removeItem(R.id.nav_signout);
        }
        invalidateOptionsMenu();

        // Set the user name and email in the nav drawer
        TextView userName = mHeaderView.findViewById(R.id.user_name);
        TextView userEmail = mHeaderView.findViewById(R.id.user_email);
        ImageView userProfilePicture = mHeaderView.findViewById(R.id.user_profile_pic);

        if (isSignedIn) {
            // Check if user is signed into Firebase
            userName.setText(mUserName);
            userEmail.setText(mUserEmail);
            if(mProfilePicture != null) {
                // Profile picture is present
                mProfilePicture = resizeProfilePicture(mProfilePicture);
                userProfilePicture.setImageDrawable(mProfilePicture);
            }
            else{
                // Profile picture is not present, create new based on initials
                String initialLetter = mUserName.substring(0, 1);
                int color = mColorGenerator.getRandomColor();
                mDrawableBuilder = TextDrawable.builder().beginConfig().width(256).height(256).endConfig().buildRound(initialLetter, color);
                userProfilePicture.setImageDrawable(mDrawableBuilder);
            }
        } else {
            mUserName = null;
            mUserEmail = null;
            mProfilePicture = null;

            userName.setText("Please sign in");
            userEmail.setText("");
            userProfilePicture.setImageDrawable(null);
        }
    }

    // Create a new profile picture based on user initials
    public Drawable createProfilePicture(String userName){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frame1 = (FrameLayout)layoutInflater.inflate(R.layout.default_profile_pic, null);
        TextView profilePictureInitials = (TextView)frame1.findViewById(R.id.default_profile_pic_name);

        String userInitials = getUserInitials(userName);
        profilePictureInitials.setText(userInitials);

        frame1.setDrawingCacheEnabled(true);
        frame1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        frame1.layout(0, 0, frame1.getMeasuredWidth(), frame1.getMeasuredHeight());
        frame1.buildDrawingCache(true);
        Bitmap newBitmap = Bitmap.createBitmap(frame1.getDrawingCache());
        frame1.setDrawingCacheEnabled(false);
        Drawable newImage = new BitmapDrawable(getResources(), newBitmap);

        return newImage;
    }

    public String getUserInitials(String userName){
        char initial = userName.charAt(0);
        String initialStr = String.valueOf(initial);

        return initialStr;
    }

    // Resize the profile picture
    public Drawable resizeProfilePicture(Drawable image){
        // Final picture must be 240*240
        Bitmap originalBitmap = ((BitmapDrawable)image).getBitmap();
        Bitmap newBitmap = Bitmap.createScaledBitmap(originalBitmap, 240, 240, false);
        Drawable newImage = new BitmapDrawable(getResources(), newBitmap);

        return newImage;
    }

    // Load the "Home" fragment
    public void openHomeFragment(String userName) {
        mToolbarSpinner.setVisibility(View.GONE);
//        HomeFragment fragment = HomeFragment.createInstance(userName);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment(userName))
                .commit();

        mNavigationView.setCheckedItem(R.id.nav_home);
    }

    public void openEmailFragment(){
        // Get the user's keywords from Firebase
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getApplicationContext());
        firebaseHelper.getAllKeywords();
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {
                return;
            }

            @Override
            public void onGetKeyword(final ArrayList<String> userKeywords) {
                mUserKeywords = userKeywords;
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new EmailFragment("Inbox", userKeywords)).commit();
            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                return;
            }

            @Override
            public void onEventDeleted() {

            }

            @Override
            public void onKeywordDeleted() {

            }
        });

        mNavigationView.setCheckedItem(R.id.nav_email);
    }

    private void openDayCalendar(){
        mToolbarSpinner.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new CalendarDayFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_day_view);
    }

    private void openWeekCalendar(){
        mToolbarSpinner.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new CalendarWeekFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_week_view);
    }

    private void openNusmodsFragment() {
        mToolbarSpinner.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new NusmodsFragment()).commit();
    }

    private void openViewEmailDialog(Message message){
        mToolbarSpinner.setVisibility(View.GONE);
        DialogFragment displayEmailDialog = DisplayEmailDialog.newInstance(message);
        displayEmailDialog.show(getSupportFragmentManager(), "viewEmail");
    }

    private void openViewEventDialog(WeekViewEvent event){
        mToolbarSpinner.setVisibility(View.GONE);
        DialogFragment displayEventDialog = DisplayEventDialog.newInstance(event);
        displayEventDialog.show(getSupportFragmentManager(), "viewEvent");
    }

    public void openKeywordManagerFragment(){
        mToolbarSpinner.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new KeywordManagerFragment(getApplicationContext())).commit();
    }

    private void signIn() {
        showProgressBar();
        // Attempt silent sign in first
        // if this fails, the callback will handle doing
        // interactive sign in
        doSilentSignIn(true);
    }

    private void signOut() {
        openHomeFragment(null);
        mAuthHelper.setMsAuthHelperCallback(new AuthenticationHelper.MSAuthHelperCallback() {
            @Override
            public void onSignedOutSuccess() {
                setSignedInState(false);
                mFirebaseAuth.signOut();
            }
        });
        mAuthHelper.signOut();
    }

    // Silently sign in - used if there is already a
// user account in the MSAL cache
    private void doSilentSignIn(boolean shouldAttemptInteractive) {
        mAttemptInteractiveSignIn = shouldAttemptInteractive;
        mAuthHelper.acquireTokenSilently(getAuthCallback());
    }

    // Prompt the user to sign in
    private void doInteractiveSignIn() {
        mAuthHelper.acquireTokenInteractively(this, getAuthCallback());
    }

    // Handles the authentication result
    public AuthenticationCallback getAuthCallback() {
        return new AuthenticationCallback() {

            // <OnSuccessSnippet>
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                // Log the token for debug purposes
                String accessToken = authenticationResult.getAccessToken();
                Log.d("AUTH", String.format("Access token: %s", accessToken));

                // Get Graph client and get user
                GraphHelper graphHelper = GraphHelper.getInstance();
                graphHelper.getUser(accessToken, getUserCallback());


                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    startActivity(new Intent(getApplicationContext(), MyAppIntro.class));
                }
            }
            // </OnSuccessSnippet>

            @Override
            public void onError(MsalException exception) {
                // Check the type of exception and handle appropriately
                if (exception instanceof MsalUiRequiredException) {
                    Log.d("AUTH", "Interactive login required");
                    if (mAttemptInteractiveSignIn) {
                        doInteractiveSignIn();
                    }

                } else if (exception instanceof MsalClientException) {
                    if (exception.getErrorCode() == "no_current_account" ||
                            exception.getErrorCode() == "no_account_found") {
                        Log.d("AUTH", "No current account, interactive login required");
                        if (mAttemptInteractiveSignIn) {
                            doInteractiveSignIn();
                        }
                    } else {
                        // Exception inside MSAL, more info inside MsalError.java
                        Log.e("AUTH", "Client error authenticating", exception);
                    }
                } else if (exception instanceof MsalServiceException) {
                    // Exception when communicating with the auth server, likely config issue
                    Log.e("AUTH", "Service error authenticating", exception);
                }

                hideProgressBar();
            }

            @Override
            public void onCancel() {
                // User canceled the authentication
                Log.d("AUTH", "Authentication canceled");
                hideProgressBar();
            }
        };
    }

    // <GetUserCallbackSnippet>
    private ICallback<User> getUserCallback() {
        return new ICallback<User>() {
            @Override
            public void success(User user) {
                Log.d("AUTH", "User: " + user.displayName);

                mUserName = user.displayName;
                mUserEmail = user.mail == null ? user.userPrincipalName : user.mail;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });

            }

            @Override
            public void failure(ClientException ex) {
                Log.e("AUTH", "Error getting /me", ex);
                mUserName = "ERROR";
                mUserEmail = "ERROR";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });
            }
        };
    }
    // </GetUserCallbackSnippet>

    private ICallback<InputStream> getProfilePhotoCallback(){
        return new ICallback<InputStream>() {
            @Override
            public void success(InputStream inputStream) {
                mProfilePicture = Drawable.createFromStream(inputStream, "UserProfilePicture");
            }

            @Override
            public void failure(ClientException ex) {
                Log.e("PROFILE PICTURE", "ERROR GETTING PROFILE PICTURE", ex);
            }
        };
    }

    @Override
    public void onParamsChanged(final String moduleCode, final String lessonType, final String classNo, final ArrayList<WeekViewEvent> classes) {
        System.out.println(moduleCode + lessonType + classNo );

        //if they already exist, change their values. if not then add it in.
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            System.out.println("User is not signed in, cannot add modules");
        }
        else {
            String uid = user.getUid();
            System.out.println(user.getDisplayName() + " is adding the module " + moduleCode + " " + lessonType + " " + classNo);
            DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);
            mModulesDatabaseReference.child("Name").setValue(user.getDisplayName());
            mModulesDatabaseReference.child("modules").child(moduleCode).child("Module Name").setValue(moduleCode);
            mModulesDatabaseReference.child("modules").child(moduleCode).child(lessonType).child("classNo").setValue(classNo);
            final DatabaseReference mModuleEventsDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid).child("modules").child(moduleCode).child(lessonType).child("lessons");



            mModuleEventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getChildren() != null) {
                        boolean lessonsExist = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            //System.out.println( "snapshot 's name : " + userSnapshot.child("Name").getValue());
                            //System.out.println( "class 's name : " + classes.get(0).getName());
                            if(userSnapshot.child("Name").getValue().equals(classes.get(0).getName())) {
                                System.out.println("Detected class name is the same, no need to re add classes");
                                lessonsExist = true;
                                break;
                            }
                        }
                        if(lessonsExist == false ) {
                            System.out.println(moduleCode + " " + lessonType + " " + classNo + " Lessons added to firebase");
                            mModuleEventsDatabaseReference.removeValue();

                            for (WeekViewEvent event : classes) {
                                WeekViewEventLite mWeekViewEventLite = new WeekViewEventLite();
                                mWeekViewEventLite.startDayOfMonth = event.getStartTime().get(Calendar.DAY_OF_MONTH);
                                mWeekViewEventLite.startMonth = event.getStartTime().get(Calendar.MONTH);
                                mWeekViewEventLite.startYear = event.getStartTime().get(Calendar.YEAR);
                                mWeekViewEventLite.startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
                                mWeekViewEventLite.startMinute = event.getStartTime().get(Calendar.MINUTE);

                                mWeekViewEventLite.endDayOfMonth = event.getEndTime().get(Calendar.DAY_OF_MONTH);
                                mWeekViewEventLite.endMonth = event.getEndTime().get(Calendar.MONTH);
                                mWeekViewEventLite.endYear = event.getEndTime().get(Calendar.YEAR);
                                mWeekViewEventLite.endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
                                mWeekViewEventLite.endMinute = event.getEndTime().get(Calendar.MINUTE);

                                mWeekViewEventLite.Description = event.getDescription();
                                mWeekViewEventLite.Location = event.getLocation();
                                mWeekViewEventLite.Name = event.getName();
                                mWeekViewEventLite.AllDay = event.isAllDay();
                                mWeekViewEventLite.Weblink = event.getmWeblink();
                                System.out.println(mWeekViewEventLite.AllDay);
                                String key = mModuleEventsDatabaseReference.push().getKey();
                                mModuleEventsDatabaseReference.child(key).setValue(mWeekViewEventLite);
                                event.setIdentifier(key);

                                mModuleEventsDatabaseReference.child(key).child("identifier").setValue(key);

                            }
                        }
                    } else {
                        System.out.println("There are no events");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: @onparamschanged" + error.getCode());
                }

            });
            
        }
    }

    @Override
    public void onModuleRemove(CharSequence moduleCode) {
        System.out.println(moduleCode + " is being removed");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            System.out.println("User is not signed in, cannot remove modules");
        }
        else {
            String uid = user.getUid();
            System.out.println(user.getDisplayName() + " is removing the module " + moduleCode);
            DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid).child("modules");
            mModulesDatabaseReference.child(moduleCode.toString()).removeValue();
        }

        Toast.makeText(getApplicationContext(), "Module " + moduleCode + " deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDateReceive(int year, int month, int day) {
        try{
            CalendarEventInputDialog.updateDateButton(year, month, day);
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            CalendarEventEditDialog.updateDateButton(year, month, day);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeReceive(int hours, int min) {
        try{
            CalendarEventInputDialog.updateTimeButton(hours, min);
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            CalendarEventEditDialog.updateTimeButton(hours, min);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onEmailPressed(Message message) {
        openViewEmailDialog(message);
    }



    @Override
    public void onEmailSpinnerItemPressed(String keyword) {
        System.out.println("MAIN ACTIVITY: " + keyword);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new EmailFragment(keyword, mUserKeywords)).commit();
    }

    @Override
    public void eventAdded(WeekViewEvent event) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mEventsDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid).child("events");
        WeekViewEventLite mWeekViewEventLite = new WeekViewEventLite();

        mWeekViewEventLite.startDayOfMonth = event.getStartTime().get(Calendar.DAY_OF_MONTH);
        mWeekViewEventLite.startMonth = event.getStartTime().get(Calendar.MONTH);
        mWeekViewEventLite.startYear = event.getStartTime().get(Calendar.YEAR);
        mWeekViewEventLite.startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
        mWeekViewEventLite.startMinute = event.getStartTime().get(Calendar.MINUTE);

        mWeekViewEventLite.endDayOfMonth = event.getEndTime().get(Calendar.DAY_OF_MONTH);
        mWeekViewEventLite.endMonth = event.getEndTime().get(Calendar.MONTH);
        mWeekViewEventLite.endYear = event.getEndTime().get(Calendar.YEAR);
        mWeekViewEventLite.endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
        mWeekViewEventLite.endMinute = event.getEndTime().get(Calendar.MINUTE);

        mWeekViewEventLite.Description = event.getDescription();
        mWeekViewEventLite.Location = event.getLocation();
        mWeekViewEventLite.Name = event.getName();
        mWeekViewEventLite.AllDay = event.isAllDay();
        //mWeekViewEventLite.Weblink = event.getmWeblink();
        System.out.println(mWeekViewEventLite.AllDay);
        String key = mEventsDatabaseReference.push().getKey();
        mEventsDatabaseReference.child(key).setValue(mWeekViewEventLite);
        event.setIdentifier(key);
        // dont know if this updates local event

        System.out.println(key + " new key added");
        mEventsDatabaseReference.child(key).child("identifier").setValue(key);

        System.out.println("EVENT RECEIVED");
        System.out.println(event.getName());
        System.out.println(event.getLocation());
        System.out.println("START: " + event.getStartTime().get(Calendar.DAY_OF_MONTH) + "/" + (event.getStartTime().get(Calendar.MONTH) + 1) + "/" + event.getStartTime().get(Calendar.YEAR));
        System.out.println("END: " + event.getEndTime().get(Calendar.DAY_OF_MONTH) + "/" + (event.getEndTime().get(Calendar.MONTH) + 1) + "/" + event.getEndTime().get(Calendar.YEAR));
    }

    // START: Callback methods from KeywordManagerFragment.java
    @Override
    public void onKeywordAdded(String keyword) {
        System.out.println("onKeywordAdded " + keyword);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mKeywordDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);
        mKeywordDatabaseReference.child("keywords").child(keyword).child("Keyword").setValue(keyword);
    }

    @Override
    public void onKeywordRemoved(String keyword) {
        System.out.println("onKeywordRemoved " + keyword);
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getApplicationContext());
        firebaseHelper.removeKeyword(keyword);
    }
    // END: Callback methods from KeywordManagerFragment.java

    // (INCOMPLETE) START: Callback methods from ReminderFragment
    @Override
    public void onToolbarCalendarClicked() {
        openWeekCalendar();
    }
    // END: Callback methods from ReminderFragment
}
