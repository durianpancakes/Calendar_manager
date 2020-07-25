package com.example.calendarmanagerbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    private static final String USER_NAME = "userName";

    private String mUserName;
    private RecyclerView recyclerView;
    private LinearLayout signInPrompt;
    private ArrayList<String> mUserKeywords = new ArrayList<>();
    private ArrayList<KeywordInfo> keywordInfoArrayList = new ArrayList<>();
    private ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();
    private DeltaEmailCallback mDeltaEmailCallback;
    private HomeFragmentReadyListener mHomeFragmentReadyListener;
    private ProgressBar mProgress = null;

    public interface DeltaEmailCallback{
        void onFinish(String moduleCode, ArrayList<WeekViewEvent> events, ArrayList<Message> messages);
    }

    public interface HomeFragmentReadyListener{
        void onFinish();
    }

    public void setmHomeFragmentReadyListener(HomeFragmentReadyListener homeFragmentReadyListener){
        this.mHomeFragmentReadyListener = homeFragmentReadyListener;
    }

    public void setmDeltaEmailCallback(DeltaEmailCallback deltaEmailCallback){
        this.mDeltaEmailCallback = deltaEmailCallback;
    }

    public HomeFragment() {

    }

    public static HomeFragment createInstance(String userName) {
        HomeFragment fragment = new HomeFragment();

        // Add the provided username to the fragment's arguments
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(USER_NAME);
        }
        keywordInfoArrayList = MainActivity.mUserKeywordDelta;
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        keywordInfoArrayList.clear();
        mUserKeywords.clear();
        mProgress = getActivity().findViewById(R.id.progressbar);
        showProgressBar();
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        signInPrompt = homeView.findViewById(R.id.home_signin_prompt);
        recyclerView = homeView.findViewById(R.id.home_sectioned_recyclerView);
        if(mUserName != null){
            signInPrompt.setVisibility(View.GONE);
            // Setup recycler view
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            Calendar todayCal = Calendar.getInstance();
            Date today = todayCal.getTime();
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
            String currentDateTimeString = sdf1.format(today);
            String currentDateString = sdf2.format(today);

            // populate recycler view
            ArrayList<HomeSectionModel> sectionModelArrayList = new ArrayList<>();
            sectionModelArrayList.add(new HomeSectionModel("KEYWORDS MONITORED ·", "last updated at " + currentDateTimeString, null, keywordInfoArrayList));
            sectionModelArrayList.add(new HomeSectionModel("TODAY ·", currentDateString, eventArrayList, null));
            final HomeSectionRecyclerViewAdapter adapter = new HomeSectionRecyclerViewAdapter(getContext(), sectionModelArrayList);
            recyclerView.setAdapter(adapter);
            setmHomeFragmentReadyListener(new HomeFragmentReadyListener() {
                @Override
                public void onFinish() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            });

            final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        // user is signed into Microsoft, but not Firebase
                        Log.e("Firebase Auth", "User is not signed in");
                        startActivity(new Intent(getContext(), MyAppIntro.class));
                    } else {
                        // user is signed in
                        Log.d("Firebase Auth", "User is signed in");
                        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getContext());
                        firebaseHelper.getAllKeywords();
                        firebaseHelper.pullEventsByDay(Calendar.getInstance());
                        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
                            @Override
                            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {
                                return;
                            }

                            @Override
                            public void onGetKeyword(final ArrayList<String> userKeywords) {
                                Log.d("Firebase Helper", "onGetKeyword success");
                                mUserKeywords = userKeywords;
                                if (mUserKeywords.size() != 0) {
                                    AuthenticationHelper.getInstance()
                                            .acquireTokenSilently(new AuthenticationCallback() {
                                                @Override
                                                public void onSuccess(IAuthenticationResult authenticationResult) {
                                                    final GraphHelper graphHelper = GraphHelper.getInstance();
                                                    String lastDateTimeString = getLastDateTimeString();
                                                    for (int i = 0; i < mUserKeywords.size(); i++) {
                                                        System.out.println("OUTER LOOP " + mUserKeywords.get(i));
                                                        graphHelper.getDeltaSpecificEmails(authenticationResult.getAccessToken(), lastDateTimeString, mUserKeywords.get(i), getDeltaEmailCallback(mUserKeywords.get(i)));
                                                        setmDeltaEmailCallback(new DeltaEmailCallback() {
                                                            @Override
                                                            public void onFinish(String moduleCode, ArrayList<WeekViewEvent> events, ArrayList<Message> messages) {
                                                                KeywordInfo keywordInfo = new KeywordInfo();
                                                                keywordInfo.setmDeltaEventsAdded(events);
                                                                keywordInfo.setmDeltaMessages(messages);
                                                                keywordInfo.setmKeyword(moduleCode);
                                                                keywordInfo.setmNumOfDeltaEmails(messages.size());
                                                                keywordInfo.setmNumOfEventsAdded(events.size());
                                                                System.out.println(keywordInfo.getmKeyword());
                                                                System.out.println("NUMBER OF NEW EMAILS: " + keywordInfo.getmNumOfDeltaEmails());
                                                                keywordInfoArrayList.add(keywordInfo);
                                                                if (mHomeFragmentReadyListener != null) {
                                                                    mHomeFragmentReadyListener.onFinish();
                                                                }
                                                            }
                                                        });
                                                    }
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
                                    hideProgressBar();
                                }
                            }

                            @Override
                            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                                eventArrayList.clear();
                                for(int i = 0; i < userEvents.size(); i++){
                                    eventArrayList.add(userEvents.get(i));
                                }
                                if (mHomeFragmentReadyListener != null) {
                                    mHomeFragmentReadyListener.onFinish();
                                }
                            }

                            @Override
                            public void onEventDeleted() {

                            }

                            @Override
                            public void onKeywordDeleted() {

                            }
                        });
                    }
                }
            };
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
        } else {
            signInPrompt.setVisibility(View.VISIBLE);
            hideProgressBar();
            //create another variable that only increases once when app is started and use that to control the logout
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        return homeView;
    }

    public String getLastDateTimeString(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String lastSyncDateTime = sharedPreferences.getString("lastSyncDateTime", getCurrentDateTimeString());
        System.out.println("Previous synchronized at: " + lastSyncDateTime);

        return lastSyncDateTime;
    }

    public void setLastDateTimeString(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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

    private ICallback<IMessageCollectionPage> getDeltaEmailCallback(final String moduleCode) {
        return new ICallback<IMessageCollectionPage>() {
            @Override
            public void success(IMessageCollectionPage iMessageCollectionPage) {
                Log.d("DELTA EMAIL", "Pull successful");
                int deltaEmails = 0;
                final ArrayList<WeekViewEvent> events = new ArrayList<>();
                ArrayList<Message> messages = new ArrayList<>();

                setLastDateTimeString();
                deltaEmails = iMessageCollectionPage.getCurrentPage().size();
                System.out.println("New emails: " + deltaEmails);

                for(Message message : iMessageCollectionPage.getCurrentPage()){
                    // Parse all emails here
                    messages.add(message);
                    System.out.println(message.subject);
                    EmailParser emailParser = EmailParser.getInstance(getContext());
                    emailParser.setmCallback(new ParserCallback() {
                        @Override
                        public void onEventAdded(WeekViewEvent event) {
                            Log.w("EMAIL PARSER", "Event added from email");
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
                            mWeekViewEventLite.Weblink = event.getmWeblink();
                            System.out.println("ALLDAY: "  + mWeekViewEventLite.AllDay);
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

                            events.add(event);
                        }

                        @Override
                        public void onEmpty() {
                            Log.e("EMAIL PARSER", "No viable dates today");
                        }
                    });
                    emailParser.AllParse(message.body.content, message.subject, message.webLink);
                    deltaEmails++;
                }

                if(mDeltaEmailCallback != null){
                    mDeltaEmailCallback.onFinish(moduleCode, events, messages);
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
}