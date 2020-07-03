package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmailFragment} factory method to
 * create an instance of this fragment.
 */
public class EmailFragment extends Fragment {
    View myFragmentView;
    private ProgressBar mProgress = null;
    private ArrayList<Message> mEmailList = new ArrayList<>();
    private String mKeyword;

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    public EmailFragment(String keyword) {
        mKeyword = keyword;
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
            }
        });
    }

    private ICallback<IMessageCollectionPage> getEmailCallback() {
        return new ICallback<IMessageCollectionPage>() {
            @Override
            public void success(IMessageCollectionPage iMessageCollectionPage) {
                Log.d("EMAIL", "Pull successful");
                for (Message message : iMessageCollectionPage.getCurrentPage()) {
                    mEmailList.add(message);
                }
                addEmailsToList();

                hideProgressBar();
            }

            @Override
            public void failure(ClientException ex) {
                Log.d("EMAIL", "Pull failed");

                hideProgressBar();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_email, container, false);

        return myFragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = getActivity().findViewById(R.id.progressbar);
        showProgressBar();

        // Get a current access token
        AuthenticationHelper.getInstance()
                .acquireTokenSilently(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        final GraphHelper graphHelper = GraphHelper.getInstance();

                        // Get the user's events
                        if(mKeyword.equals("Inbox")){
                            graphHelper.getEmails(authenticationResult.getAccessToken(), getEmailCallback());
                        } else {
                            graphHelper.getSpecificEmails(authenticationResult.getAccessToken(), mKeyword,
                                    getEmailCallback());
                        }

                        hideProgressBar();
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
    }

    private void addEmailsToList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView emailListView = getView().findViewById(R.id.emaillist);

                EmailListAdapter listAdapter = new EmailListAdapter(getActivity(),
                        R.layout.email_list_item, mEmailList);

                emailListView.setAdapter(listAdapter);
            }
        });
    }
    public ArrayList<NUSModuleLite> getAllModules() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);

        final ArrayList<NUSModuleLite> allModules = new ArrayList<>();

        mModulesDatabaseReference.child("modules").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String moduleString = new String();
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        // original testing
                        String modCode = userSnapshot.child("Module Name").getValue(String.class);
                        moduleString = moduleString + " " + modCode;
                        System.out.println(moduleString);
                        //

                        NUSModuleLite nusModuleLite = new NUSModuleLite();
                        nusModuleLite.setModuleCode(modCode);

                        ArrayList<ModuleInfo> moduleInfoList = new ArrayList<>();

                        if (userSnapshot.child("Sectional Teaching").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Sectional Teaching",
                                    userSnapshot.child("Sectional Teaching").getValue(String.class));
                            moduleInfoList.add(moduleInfo);

                        }

                        if (userSnapshot.child("Tutorial").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Tutorial",
                                    userSnapshot.child("Tutorial").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Recitation").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Recitation",
                                    userSnapshot.child("Recitation").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Lecture").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Lecture",
                                    userSnapshot.child("Lecture").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }


                        System.out.println(moduleInfoList.get(0).lessonType);

                        // works up to here
                        // get(1) works if the mod has a 2nd lessontype (tested)
                        nusModuleLite.setClassesSelected(moduleInfoList);

                        allModules.add(nusModuleLite);
                    }
                } else {
                    System.out.println("There are no children");
                }

                //test allModules here
                for (NUSModuleLite nusModuleLite : allModules) {
                    System.out.println(nusModuleLite.getModuleCode() + " in test zone");
                    // works up to here
                    ArrayList<ModuleInfo> moduleInfoArrayList = nusModuleLite.getClassesSelected();
                    for (ModuleInfo moduleInfo : moduleInfoArrayList) {
                        System.out.println(moduleInfo.lessonType + " " +
                                moduleInfo.classNo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());

            }

        });

        return allModules;
    }
}