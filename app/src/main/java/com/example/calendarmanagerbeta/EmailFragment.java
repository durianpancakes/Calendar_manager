package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.alamkanak.weekview.WeekViewEvent;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageCollectionRequestBuilder;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

import java.util.ArrayList;

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
    private EmailFragmentCallback mCallback;
    private ArrayList<String> mUserKeywords;
    private ListView emailListView;

    public interface EmailFragmentCallback{
        void onEmailPressed(Message message);
        void onEmailSpinnerItemPressed(String keyword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EmailFragment.EmailFragmentCallback){
            mCallback = (EmailFragment.EmailFragmentCallback)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    public EmailFragment(String keyword, ArrayList<String> userKeywords) {
        mKeyword = keyword;
        mUserKeywords = userKeywords;
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
                int i = 1;
                for (Message message : iMessageCollectionPage.getCurrentPage()) {
                    mEmailList.add(message);
                    //System.out.println(message.body.content);

                    /*if(i == 1) {
                        final EmailParser mEmailParser = EmailParser.getInstance(getContext());
                        mEmailParser.setmCallback(new ParserCallback() {
                            @Override
                            public void onEventAdded(WeekViewEvent event) {
                                System.out.println("An event is found");
                            }

                            @Override
                            public void onEmpty() {
                                System.out.println("No suitable events found");
                            }
                        });
                        i = 0;
                    }*/

                }

                nextPage = iMessageCollectionPage.getNextPage();
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
        emailListView = myFragmentView.findViewById(R.id.emaillist);
        Toolbar toolbar = myFragmentView.findViewById(R.id.email_toolbar);
        Spinner toolbarSpinner = myFragmentView.findViewById(R.id.email_spinner);
        int spinnerOptionPos = 0;

        if(mUserKeywords.size() == 0){
            toolbar.setVisibility(View.GONE);
        } else {
            ArrayList<String> mToolbarSpinnnerArray = new ArrayList<>();
            ArrayAdapter<String> mToolbarSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.email_checked_title, mToolbarSpinnnerArray);
            mToolbarSpinnerAdapter.setDropDownViewResource(R.layout.email_spinner_dropdown_item);
            mToolbarSpinnerAdapter.clear();
            mToolbarSpinnerAdapter.add("Inbox");
            toolbar.setVisibility(View.VISIBLE);
            for(int i = 0; i < mUserKeywords.size(); i++){
                mToolbarSpinnerAdapter.add(mUserKeywords.get(i));
                if(mUserKeywords.get(i).equals(mKeyword)){
                    spinnerOptionPos = i + 1;
                }
            }
            toolbarSpinner.setAdapter(mToolbarSpinnerAdapter);
            toolbarSpinner.setSelection(spinnerOptionPos);
            toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String keywordSelected = parent.getItemAtPosition(pos).toString();
                    System.out.println("onItemSelected: " + keywordSelected);
                    if(!keywordSelected.equals(mKeyword)) {
                        if (mCallback != null) {
                            mCallback.onEmailSpinnerItemPressed(keywordSelected);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Inbox");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

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
                if(listAdapter == null){
                    listAdapter = new EmailListAdapter(getActivity(), R.layout.email_list_item, mEmailList);
                    emailListView.setAdapter(listAdapter);
                    listAdapter.setEmailListAdapterCallback(new EmailListAdapter.EmailListAdapterCallback() {
                        @Override
                        public void onEmailPressed(Message message) {
                            mCallback.onEmailPressed(message);
                        }
                    });
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