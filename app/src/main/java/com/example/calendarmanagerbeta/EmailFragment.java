package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

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

    private ICallback<IMessageCollectionPage> getEmailCallback(){
        return new ICallback<IMessageCollectionPage>() {
            @Override
            public void success(IMessageCollectionPage iMessageCollectionPage) {
                Log.d("EMAIL", "Pull successful");
                for(Message message : iMessageCollectionPage.getCurrentPage()){
                    mEmailList.add(message);
                    System.out.println(message.subject);
                    System.out.println(message.sender.emailAddress.address);
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
                        graphHelper.getSpecificEmails(authenticationResult.getAccessToken(), "CG1112",
                                getEmailCallback());

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
}