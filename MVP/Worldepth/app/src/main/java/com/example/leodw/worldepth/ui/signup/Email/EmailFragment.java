package com.example.leodw.worldepth.ui.signup.Email;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.signup.SignupViewModel;


import androidx.navigation.Navigation;

public class EmailFragment extends Fragment {
    private static final String TAG = "EmailFragment";

    private SignupViewModel mViewModel;
    private EditText mEmailInput;
    private EditText mServiceInput;
    private EditText mSuffixInput;
    private Button mNextButton;
    private ImageView goBack;


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
        mViewModel = ViewModelProviders.of(this).get(SignupViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mEmailInput = view.findViewById(R.id.emailInput);
        mServiceInput = view.findViewById(R.id.emailService);
        mSuffixInput = view.findViewById(R.id.emailSuffix);
        mNextButton = view.findViewById(R.id.emailNextButton);
        goBack = view.findViewById(R.id.emailBackButton);

        mEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmailInput.getText().toString().endsWith("@")) {
                    asteriskAdded();
                    Selection.setSelection((Editable) mServiceInput.getText(), mServiceInput.getSelectionStart());
                    mServiceInput.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mServiceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mServiceInput.getText().toString().endsWith(".")) {
                    dotAdded();
                    Selection.setSelection((Editable) mSuffixInput.getText(), mSuffixInput.getSelectionStart());
                    mSuffixInput.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNextButton.setOnClickListener((view1) -> {
            if (validEmail()) {
                String email = mEmailInput.getText().toString() + "@" + mServiceInput.getText().toString() + "." + mSuffixInput.getText().toString();
                mViewModel.setEmail(email);
                Navigation.findNavController(view1).navigate(R.id.action_emailFragment_to_nameFragment);
            }
        });

        goBack.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).popBackStack();
        });

        mEmailInput.requestFocus();
    }

    private boolean validEmail() {
        return !(mEmailInput.getText().toString().equals(""));
    }

    private void asteriskAdded() {
        EditText text = (EditText) getView().findViewById(R.id.emailInput);
        text.setText(text.getText().delete(text.length()-1,text.length()));
        getView().findViewById(R.id.asterisk).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.emailService).setVisibility(View.VISIBLE);
    }

    private void dotAdded() {
        EditText serviceText = (EditText) getView().findViewById(R.id.emailService);
        serviceText.setText(serviceText.getText().delete(serviceText.length()-1,serviceText.length()));
        getView().findViewById(R.id.dot).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.emailSuffix).setVisibility(View.VISIBLE);
    }

    private void asteriskDeleted() {
        getView().findViewById(R.id.asterisk).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.emailService).setVisibility(View.INVISIBLE);
    }

    private void dotDeleted() {
        getView().findViewById(R.id.dot).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.emailSuffix).setVisibility(View.INVISIBLE);
    }
}
