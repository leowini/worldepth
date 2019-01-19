package com.example.leodw.worldepth.ui.signup.Birthday;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.SignupViewModel;

import androidx.navigation.Navigation;

public class BirthdayFragment extends Fragment {

    private static final String TAG = "BirthdayFragment";

    private SignupViewModel mViewModel;
    private FirebaseWrapper mFb;
    private EditText mBirthdayDay;
    private EditText mBirthdayYear;

    public static BirthdayFragment newInstance() {
        return new BirthdayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.birthday_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(SignupViewModel.class);
        String [] values =
                {"Month","January","Febuary","March","April","May","June","July","August","September","October","November","December"};
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(LTRadapter);

        mBirthdayDay = view.findViewById(R.id.birthdayDay);
        mBirthdayYear = view.findViewById(R.id.birthdayYear);

        Button birthdayNextButton = view.findViewById(R.id.birthdayNextButton);
        birthdayNextButton.setOnClickListener((view1) -> {
            String birthday = spinner.getSelectedItem() + " " + mBirthdayDay.getText().toString() + ", " + mBirthdayYear.getText().toString();
            Toast.makeText(getContext(), "birthday string: " + birthday, Toast.LENGTH_SHORT).show();
            mViewModel.setBirthday(birthday);
            Navigation.findNavController(view1).navigate(R.id.action_birthdayFragment_to_passwordFragment);
        });

        ImageView birthdayBackButton = view.findViewById(R.id.birthdayBackButton);
        birthdayBackButton.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).popBackStack();
        });
    }
}
