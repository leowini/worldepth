package com.example.leodw.worldepth.ui.signup.Name;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneViewModel;

public class NameFragment extends Fragment {
    private static final String TAG = "NameFragment";

    private NameViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;

    private ImageView mNameBackButton;

    public static NameFragment newInstance() {
        return new NameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_fragment, container, false);
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();

        Button nameNextButton = view.findViewById(R.id.nameNextButton);
        nameNextButton.setOnClickListener((view1) -> {
            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPagerByTitle("Birthday_Fragment"); //birthday
        });

        mNameBackButton = view.findViewById(R.id.nameBackButton);
        mNameBackButton.setOnClickListener((view2) -> {
            int lastLoc = getLastLocation();
            ((MainActivity) getActivity()).setViewPagerByTitle("StartSignup_Fragment"); //either phone or email fragment
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NameViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    }

    private int getLastLocation() {
        for (int i = mDt.size() - 1; i >= 0; i--) {
            int temp = mDt.getDataPair(i).getSender();
            int phoneIndex = ((MainActivity) getActivity()).getFragmentIndex("Phone_Fragment");
            int emailIndex = ((MainActivity) getActivity()).getFragmentIndex("Email_Fragment");
            if (temp == phoneIndex) {
                return phoneIndex;
            } else if (temp == emailIndex) {
                return emailIndex;
            }
        }
        return 0;
    }
}
