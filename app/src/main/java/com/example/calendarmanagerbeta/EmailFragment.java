package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.microsoft.graph.requests.extensions.IMessageCollectionRequestBuilder;
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
    private IMessageCollectionRequestBuilder nextPage = null;
    private EmailListAdapter listAdapter = null;

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
                nextPage = iMessageCollectionPage.getNextPage();
                addEmailsToList();
                /*for(int i = 0; i < mEmailList.size(); i++) {
                    String emailContent = mEmailList.get(i).body.content;
                    System.out.println(emailContent);
                    EmailParser mEmailParser = new EmailParser();
                    ArrayList<Integer> DMY = mEmailParser.DateParse(emailContent);

                    if(DMY.get(0) != 0) {
                        //set date
                    }
                    if(DMY.get(1) != 0) {
                        //set month
                    }
                    if(DMY.get(2) != 0) {
                        //set year
                    }

                    //default d / m / y is 0 so if == 0 means not detected.


                }*/

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
                final ListView emailListView = getView().findViewById(R.id.emaillist);

                if(listAdapter == null){
                    listAdapter = new EmailListAdapter(getActivity(), R.layout.email_list_item, mEmailList);
                    emailListView.setAdapter(listAdapter);
                } else {
                    listAdapter.notifyDataSetChanged();
                }

                emailListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (emailListView.getLastVisiblePosition() - emailListView.getHeaderViewsCount() - emailListView.getFooterViewsCount()) >= (listAdapter.getCount() - 5)){
                            final GraphHelper graphHelper = GraphHelper.getInstance();

                            if(nextPage != null) {
                                graphHelper.getNextEmails(nextPage, getEmailCallback());
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                    }
                });
            }
        });
    }
}