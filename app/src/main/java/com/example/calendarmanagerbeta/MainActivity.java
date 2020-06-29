package com.example.calendarmanagerbeta;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NusmodsFragment.moduleParamsChangedListener, NusmodsFragment.removeModuleListener{
    private static final String SAVED_IS_SIGNED_IN = "isSignedIn";
    private static final String SAVED_USER_NAME = "userName";
    private static final String SAVED_USER_EMAIL = "userEmail";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
            case R.id.toolbar_opt_storage:
                openFirebaseLoginFragment();
                uncheckAllNavItems();
                return true;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setSubtitle("");
        HomeFragment fragment = HomeFragment.createInstance(userName);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        mNavigationView.setCheckedItem(R.id.nav_home);
    }

    public void openEmailFragment(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Email");
        toolbar.setSubtitle("");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment()).commit();
        mNavigationView.setCheckedItem(R.id.nav_email);
    }

    private void openDayCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Day View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarDayFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_day_view);
    }

    private void openMonthCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Month View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarMonthFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_month_view);
    }

    private void openWeekCalendar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        toolbar.setSubtitle("Week View");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarWeekFragment()).commit();
        mNavigationView.setCheckedItem(R.id.calendar_week_view);
    }

    private void openFirebaseLoginFragment(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Storage");
        toolbar.setSubtitle("");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FirebaseLoginFragment()).commit();
    }

    private void openNusmodsFragment() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    @Override
    public void onParamsChanged(String moduleCode, String lessonType, String classNo) {
        //if they already exist, change their values. if not then add it in.
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            System.out.println("User is not signed in, cannot add modules");
        }
        else {
            System.out.println(user.getDisplayName() + " is adding the module " + moduleCode + " " + lessonType + " " + classNo);
            DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getDisplayName()).child("modules");
            //mModulesDatabaseReference.child(moduleCode);
            mModulesDatabaseReference.child(moduleCode).child("Module Name").setValue(moduleCode);
            mModulesDatabaseReference.child(moduleCode).child(lessonType).setValue(classNo);


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
            System.out.println(user.getDisplayName() + " is removing the module " + moduleCode);
            DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getDisplayName()).child("modules");
            mModulesDatabaseReference.child(moduleCode.toString()).removeValue();
        }
    }
}
