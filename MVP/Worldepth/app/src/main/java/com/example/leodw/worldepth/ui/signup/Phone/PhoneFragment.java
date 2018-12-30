package com.example.leodw.worldepth.ui.signup.Phone;

import android.arch.lifecycle.ViewModelProviders;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataPair;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.SignUpFragment;
import com.example.leodw.worldepth.ui.signup.SignUpViewModel;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.Navigation;

public class PhoneFragment extends Fragment {
    private static final String TAG = "PhoneFragment";

    private PhoneViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;

    private ImageView mPhoneBackButton;

    public static PhoneFragment newInstance() {
        return new PhoneFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_fragment, container, false);
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();

        String [] values =
                {"Country","+1 (US)"};
        Spinner spinner = (Spinner) view.findViewById(R.id.phone_spinner);
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(LTRadapter);

        Button phoneNextButton = view.findViewById(R.id.phoneNextButton);
        phoneNextButton.setOnClickListener((view1) -> {
            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            mDt.addData(new DataPair("FromPhone", ((MainActivity) getActivity()).getFragmentIndex("Password_Fragment"),
                    ((MainActivity) getActivity()).getFragmentIndex("Phone_Fragment")));
            Navigation.findNavController(view1).navigate(R.id.action_phoneFragment_to_nameFragment);
        });

        mPhoneBackButton = view.findViewById(R.id.phoneBackButton);
        mPhoneBackButton.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).navigate(R.id.action_phoneFragment_to_startSignupFragment);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PhoneViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    }
}
