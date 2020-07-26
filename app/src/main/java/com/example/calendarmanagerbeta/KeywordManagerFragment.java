package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

public class KeywordManagerFragment extends Fragment {
    View myFragmentView;
    Context context;
    private KeywordManagerCallback mKeywordManagerCallback;
    private ManagerInternalCallback mManagerInternalCallback;
    private ArrayList<String> mUserKeywords = new ArrayList<>();
    private ProgressBar mProgress = null;
    private KeywordListAdapter keywordListAdapter;

    public interface KeywordManagerCallback{
        void onKeywordAdded(String keyword);
        void onKeywordRemoved(String keyword);
    }

    public interface ManagerInternalCallback{
        void onRefreshCompleted();
    }

    public KeywordManagerFragment(Context context){
        this.context = context;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProgress = getActivity().findViewById(R.id.progressbar);
        showProgressBar();
    }

    public void refreshFromDatabase(){
        // START: Get all keywords from Firebase
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(context);
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

            }

            @Override
            public void onGetKeyword(ArrayList<String> userKeywords) {
                mUserKeywords = userKeywords;
                if(mManagerInternalCallback != null){
                    mManagerInternalCallback.onRefreshCompleted();
                }
            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {

            }

            @Override
            public void onEventDeleted() {

            }

            @Override
            public void onKeywordDeleted() {

            }
        });
        firebaseHelper.getUserKeywords();
        // END: Get all keywords from Firebase
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_keyword, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Keywords");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        mManagerInternalCallback = new ManagerInternalCallback() {
            @Override
            public void onRefreshCompleted() {
                final EditText addKeywordEditText = myFragmentView.findViewById(R.id.add_keyword_edittext);
                Button addKeywordBtn = myFragmentView.findViewById(R.id.add_keyword);
                addKeywordBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence input = addKeywordEditText.getText();
                        String inputString = input.toString();
                        if(inputString.equals("")){
                            // Invalid input from user
                            // REJECT
                            Toast.makeText(getActivity(), "Invalid keyword added", Toast.LENGTH_LONG).show();
                        } else {
                            mKeywordManagerCallback.onKeywordAdded(inputString);
                            refreshFromDatabase();
                            keywordListAdapter.notifyDataSetChanged();
                            addKeywordEditText.setText("");
                            Toast.makeText(getActivity(), "Keyword added", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ListView moduleListView = myFragmentView.findViewById(R.id.keywords_listview);
                keywordListAdapter = new KeywordListAdapter(context, R.layout.keyword_list_item, mUserKeywords);
                moduleListView.setAdapter(keywordListAdapter);
                keywordListAdapter.setKeywordManagerAdapterCallback(new KeywordManagerAdapterCallback() {
                    @Override
                    public void onKeywordRemoved(String keyword) {
                        mKeywordManagerCallback.onKeywordRemoved(keyword);
                        FirebaseHelper.getInstance(context).setFirebaseCallbackListener(new FirebaseCallback() {
                            @Override
                            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

                            }

                            @Override
                            public void onGetKeyword(ArrayList<String> userKeywords) {

                            }

                            @Override
                            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {

                            }

                            @Override
                            public void onEventDeleted() {

                            }

                            @Override
                            public void onKeywordDeleted() {
                                refreshFromDatabase();
                                Toast.makeText(getActivity(), "Keyword deleted", Toast.LENGTH_LONG).show();
                                keywordListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

                hideProgressBar();
            }
        };
        refreshFromDatabase();

        return myFragmentView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.toolbar_cal_btn).setVisible(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof KeywordManagerFragment.KeywordManagerCallback){
            mKeywordManagerCallback = (KeywordManagerFragment.KeywordManagerCallback)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mKeywordManagerCallback = null;
    }
}
