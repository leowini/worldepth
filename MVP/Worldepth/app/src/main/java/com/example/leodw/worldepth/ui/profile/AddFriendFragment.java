package com.example.leodw.worldepth.ui.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leodw.worldepth.R;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFriendFragment extends Fragment {
    private Button mBackToProfileButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBackToProfileButton = view.findViewById(R.id.friendToProfileBtn);
        mBackToProfileButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_addFriend_to_profileFragment));
    }
}
