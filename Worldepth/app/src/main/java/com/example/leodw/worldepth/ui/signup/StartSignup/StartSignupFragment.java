package com.example.leodw.worldepth.ui.signup.StartSignup;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.arch.lifecycle.ViewModelProviders;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataPair;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.Navigation;

public class  StartSignupFragment extends Fragment {
    private static final String TAG = "StartSignupFragment";

    private StartSignupViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;

    private EditText mName;
    private EditText mEmail;
    private String fullName;

    private String firstName;
    private String lastName;

    private ImageView mBackToStart;


    public static StartSignupFragment newInstance() {
        return new StartSignupFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_signup_fragment, container, false);
        mName = view.findViewById(R.id.signUpName);
        fullName = mName.getText().toString();
        splitName();
        mEmail = view.findViewById(R.id.signUpEmail);
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();

        Button signUpContinue = view.findViewById(R.id.signUpContinue);
        signUpContinue.setOnClickListener((view1) -> {
            mDt.addData(new DataPair(mEmail.getText().toString(), "continueSignUpFragment", "startSignupFragment"));
            mDt.addData(new DataPair(firstName, "continueSignUpFragment", "startSignupFragment"));
            mDt.addData(new DataPair(lastName, "continueSignUpFragment", "startSignupFragment"));
            Navigation.findNavController(view1).navigate(R.id.action_startSignupFragment_to_continueSignUpFragment);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartSignupViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();

        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        /*setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.logo_move));
        AnimatorSet emailSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.signup_anim);
        emailSet.setStartDelay(600);
        emailSet.setTarget(view.findViewById(R.id.emailSignup));
        AnimatorSet phoneSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                R.animator.signup_anim);
        phoneSet.setStartDelay(600);
        phoneSet.setTarget(view.findViewById(R.id.phoneSignup));
        emailSet.start();
        phoneSet.start();
        mPhoneSignup = view.findViewById(R.id.phoneSignup);
        mEmailSignup = view.findViewById(R.id.emailSignup);
        mPhoneSignup.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_startSignupFragment_to_phoneFragment));
        mEmailSignup.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_startSignupFragment_to_emailFragment));*/
        mBackToStart = view.findViewById(R.id.signUpBackButton);
        mBackToStart.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void splitName() {
        int spaceIndex = fullName.indexOf(' ');
        if (spaceIndex != -1) {
            firstName = fullName.substring(0, spaceIndex);
            lastName = fullName.substring(spaceIndex + 1);
        } else {
            firstName = fullName;
            lastName = "Temp";
        }
    }

}
