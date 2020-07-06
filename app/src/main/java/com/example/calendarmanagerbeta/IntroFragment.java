package com.example.calendarmanagerbeta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.github.appintro.SlidePolicy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class IntroFragment extends Fragment implements SlidePolicy {
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int RC_SIGN_IN = 1231;
    private int layoutResId;
    View myFragmentView = null;
    Button signInButton;
    ImageView cloudImage;
    TextView detectedText;

    public static IntroFragment createInstance(int layoutResId){
        IntroFragment slide = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        slide.setArguments(args);

        return slide;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.activity_my_app_intro, container, false);
        signInButton = (Button) myFragmentView.findViewById(R.id.intro_fb_signin);
        cloudImage = (ImageView) myFragmentView.findViewById(R.id.intro_cloud_image);
        detectedText = (TextView) myFragmentView.findViewById(R.id.intro_detected_database);

        if(!isPolicyRespected()) {
            setSignedIn();
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build());
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(providers).build(), RC_SIGN_IN);
                }
            });
        } else {
            setSignedIn();
        }
        return myFragmentView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // if the activity being returned from was login flow, use this code
            if (resultCode == RESULT_OK) {
                setSignedIn();
                Toast.makeText(getActivity(), "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Sign in cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setSignedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            cloudImage.setImageResource(R.drawable.ic_baseline_cloud_done_24);
            detectedText.setText("Firebase log-in detected.");
            signInButton.setVisibility(View.INVISIBLE);
        } else {
            cloudImage.setImageResource(R.drawable.ic_baseline_cloud_off_24);
            detectedText.setText("Firebase log-in not detected. Please sign up/in");
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean isPolicyRespected() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
