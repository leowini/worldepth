package com.example.leodw.worldepth.ui.signup.Email;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataPair;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.Name.NameViewModel;
import com.example.leodw.worldepth.ui.signup.Password.PasswordFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;

public class EmailFragment extends Fragment {
    private static final String TAG = "EmailFragment";

    private EmailViewModel mViewModel;
    private FirebaseWrapper mFb;
    private EditText mEmailInput;
    private Button signUp, goBack;

    private DataTransfer mDt;


    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EmailViewModel.class);
        mFb = ((MainActivity) this.getActivity()).getFirebaseWrapper();
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mEmailInput = view.findViewById(R.id.emailInput);
        signUp = view.findViewById(R.id.emailNextButton);
        goBack = view.findViewById(R.id.emailBackButton);

        signUp.setOnClickListener((view1) -> {
            if (validEmail()) {
                String email = mEmailInput.getText().toString();
                mDt.addData(new DataPair(email, ((MainActivity) getActivity()).getFragmentIndex("Password_Fragment"),
                        ((MainActivity) getActivity()).getFragmentIndex("Email_Fragment")));
                Toast.makeText(getActivity(), "Valid email!", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPagerByTitle("Password_Fragment"); //password fragment
            }
        });
    }

    private boolean validEmail() {
        return !(mEmailInput.getText().toString().equals(""));
    }
}
