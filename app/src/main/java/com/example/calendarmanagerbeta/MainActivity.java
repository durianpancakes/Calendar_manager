package com.example.calendarmanagerbeta;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import androidx.fragment.app.FragmentManager;

import android.util.Log;


import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.models.extensions.Shared;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IMessageDeltaCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageDeltaCollectionRequestBuilder;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.OnTimeReceiveCallback, DatePickerFragment.OnDateReceiveCallback, NavigationView.OnNavigationItemSelectedListener, CalendarEventInputFragment.eventInputListener, CalendarDayFragment.addEventListener, CalendarWeekFragment.addEventListener, NusmodsFragment.moduleParamsChangedListener, NusmodsFragment.removeModuleListener{
    private static final String SAVED_IS_SIGNED_IN = "isSignedIn";
    private static final String SAVED_USER_NAME = "userName";
    private static final String SAVED_USER_EMAIL = "userEmail";
    public static final String PREFS_NAME = "CalendarManagerPreferences";

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private View mHeaderView;
    private Drawable mProfilePicture;
    private boolean mIsSignedIn = false;
    private String mUserName = null;
    private String mUserEmail = null;
    private AuthenticationHelper mAuthHelper = null;
    private boolean mAttemptInteractiveSignIn = false;
    private FirebaseAuth mFirebaseAuth;
    private Spinner mToolbarSpinner;
    private ArrayList<String> mUserKeywords = new ArrayList<>();

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
            case R.id.calendar_month_view:
                openMonthCalendar();
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

        menu.findItem(R.id.go_current_day).setVisible(mIsSignedIn);
        menu.setGroupVisible(R.id.toolbar_opt_group, mIsSignedIn);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.go_current_day).setVisible(mIsSignedIn);
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
            menu.findItem(R.id.calendar_submenu).getSubMenu().removeItem(R.id.calendar_month_view);
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
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                startActivity(new Intent(getApplication(), MyAppIntro.class));
            }

            userName.setText(mUserName);
            userEmail.setText(mUserEmail);
            if(mProfilePicture != null) {
                // Profile picture is present
                mProfilePicture = resizeProfilePicture(mProfilePicture);
                userProfilePicture.setImageDrawable(mProfilePicture);
            }
            else{
                // Profile picture is not present, create new based on initials
                mProfilePicture = createProfilePicture(mUserName);
                userProfilePicture.setImageDrawable(mProfilePicture);
            }

            AuthenticationHelper.getInstance()
                    .acquireTokenSilently(new AuthenticationCallback() {
                        @Override
                        public void onSuccess(IAuthenticationResult authenticationResult) {
                            final GraphHelper graphHelper = GraphHelper.getInstance();
                            String lastDateTimeString = getLastDateTimeString();
                            graphHelper.getDeltaSpecificEmails(authenticationResult.getAccessToken(), lastDateTimeString, "CS0000", getDeltaEmailCallback());
                        }

                        @Override
                        public void onError(MsalException exception) {
                            Log.e("AUTH", "Could not get token silently", exception);
                            hideProgressBar();
                        }

                        @Override
                        public void onCancel() {
                            hideProgressBar();
                        }
                    });
        } else {
            mUserName = null;
            mUserEmail = null;
            mProfilePicture = null;

            userName.setText("Please sign in");
            userEmail.setText("");
            userProfilePicture.setImageDrawable(null);
        }
    }

    public String getLastDateTimeString(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String lastSyncDateTime = sharedPreferences.getString("lastSyncDateTime", getCurrentDateTimeString());
        System.out.println("Previous synchronized at: " + lastSyncDateTime);

        return lastSyncDateTime;
    }

    public void setLastDateTimeString(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        System.out.println("New synchronized at: "+ getCurrentDateTimeString());
        editor.putString("lastSyncDateTime", getCurrentDateTimeString());
        editor.commit();
    }

    public String getCurrentDateTimeString(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        return nowAsISO;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Home");
        toolbar.setSubtitle("");
        HomeFragment fragment = HomeFragment.createInstance(userName);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        mNavigationView.setCheckedItem(R.id.nav_home);
    }

    public void openEmailFragment(){
        final Toolbar toolbar = findViewById(R.id.toolbar);

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
                if(userKeywords.size() == 0){
                    // Nothing in database, revert to no options (show inbox only)
                    toolbar.setTitle("Inbox");
                    toolbar.setSubtitle("");
                    mToolbarSpinner.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment("Inbox")).commit();
                } else {
                    toolbar.setTitle("");
                    toolbar.setSubtitle("");
                    ArrayAdapter<String> mToolbarSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.email_checked_title, mUserKeywords);
                    mToolbarSpinnerAdapter.setDropDownViewResource(R.layout.email_spinner_dropdown_item);
                    mToolbarSpinnerAdapter.clear();
                    mToolbarSpinnerAdapter.add("Inbox");
                    mToolbarSpinner.setVisibility(View.VISIBLE);
                    for(int i = 0; i < userKeywords.size(); i++){
                        mToolbarSpinnerAdapter.add(userKeywords.get(i));
                    }
                    mToolbarSpinner.setAdapter(mToolbarSpinnerAdapter);
                    mToolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            String keywordSelected = parent.getItemAtPosition(pos).toString();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment(keywordSelected)).commit();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
        });

        mNavigationView.setCheckedItem(R.id.nav_email);
    }

    private void openDayCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Day View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarDayFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_day_view);
    }

    private void openMonthCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Month View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarMonthFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_month_view);
    }

    private void openWeekCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Week View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarWeekFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_week_view);
    }

    private void openCalendarInput(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Add event");
        toolbar.setSubtitle("");
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left).addToBackStack(null).replace(R.id.fragment_container, new CalendarEventInputFragment()).commit();
    }

    private void openNusmodsFragment() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarSpinner.setVisibility(View.GONE);
        toolbar.setTitle("Modules");
        toolbar.setSubtitle("");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NusmodsFragment()).commit();
    }

    private void signIn() {
        showProgressBar();
        // Attempt silent sign in first
        // if this fails, the callback will handle doing
        // interactive sign in
        doSilentSignIn(true);
    }

    private void signOut() {
        mAuthHelper.signOut();
        mFirebaseAuth.signOut();
        setSignedInState(false);
        openHomeFragment(mUserName);
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

                // Get NUSmods helper
                NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getApplicationContext());
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

    private ICallback<IMessageCollectionPage> getDeltaEmailCallback() {
        return new ICallback<IMessageCollectionPage>() {
            @Override
            public void success(IMessageCollectionPage iMessageCollectionPage) {
                Log.d("DELTA EMAIL", "Pull successful");
                int deltaEmails = 0;

                setLastDateTimeString();
                deltaEmails = iMessageCollectionPage.getCurrentPage().size();
                System.out.println("New emails: " + deltaEmails);

                for(Message message : iMessageCollectionPage.getCurrentPage()){
                    System.out.println(message.subject);
                    deltaEmails++;
                }

                hideProgressBar();
            }

            @Override
            public void failure(ClientException ex) {
                Log.d("DELTA EMAIL", "Pull failed");

                hideProgressBar();
            }
        };
    }

    @Override
    public void onParamsChanged(String moduleCode, String lessonType, String classNo) {
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
            mModulesDatabaseReference.child("modules").child(moduleCode).child(lessonType).setValue(classNo);
            //may be made to be more efficient i think?

            EmailParser mEmailParser = new EmailParser();
            mEmailParser.TimeParse("test(not used)");
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


        String emailContent = "There will be a quiz on 29 July 2020 at 6pm.";
        System.out.println(emailContent);
        EmailParser mEmailParser = new EmailParser();
        ArrayList<Integer> DMY = mEmailParser.DateParse(emailContent);
        ArrayList<Integer> Time = mEmailParser.TimeParse(emailContent);

        /*public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
            this(String.valueOf(id), name, startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
        }*/
        long id = 2222; // change to email uid?

        int startDay;
        int startMonth;
        int startYear;
        int startHour;
        int startMinute;
        int endHour;
        int endMinute;

        if(DMY.get(0) != 99) {
            startDay = DMY.get(0);
        }
        if(DMY.get(1) != 99) {
            startMonth = DMY.get(1);
        }
        if(DMY.get(2) != 99) {
            startYear = DMY.get(2);
        }
        if(Time.get(0) != 99) {
            startHour = Time.get(0) ;
        }
        if(Time.get(1)  != 99) {
            startMinute = Time.get(1) ;
        }
        if(Time.get(2)  != 99) {
            endHour = Time.get(2) ;
        }
        if(Time.get(3)  != 99) {
            endMinute= Time.get(3) ;
        }
        //WeekViewEvent mWeekViewEvent = new WeekViewEvent(id, "event", startYear, )

        // how to set null if they dont exist.













    }

    @Override
    public void onAddEventButtonPressed() {
        openCalendarInput();
    }

    @Override
    public void onCancelPressed() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onAddPressed(WeekViewEvent event) {
        System.out.println("EVENT RECEIVED");
        System.out.println(event.getName());
        System.out.println(event.getLocation());
        System.out.println("START: " + event.getStartTime().get(Calendar.DAY_OF_MONTH) + "/" + (event.getStartTime().get(Calendar.MONTH) + 1) + "/" + event.getStartTime().get(Calendar.YEAR));
        System.out.println("END: " + event.getEndTime().get(Calendar.DAY_OF_MONTH) + "/" + (event.getEndTime().get(Calendar.MONTH) + 1) + "/" + event.getEndTime().get(Calendar.YEAR));
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDateReceive(int year, int month, int day) {
        CalendarEventInputFragment.updateDateButton(year, month, day);
    }

    @Override
    public void onTimeReceive(int hours, int min) {
        CalendarEventInputFragment.updateTimeButton(hours, min);
    }
}
