package com.example.leodw.worldepth.ui.profile;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        DatabaseReference dbRef = ((MainActivity) getActivity()).getFirebaseWrapper().getFirebaseDatabase().getReference();
        ArrayList<String> userList = new ArrayList<>();
        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (//) {
                    User user =
                    userList.append(user.firstName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child(phoneNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        mBackToProfileButton = view.findViewById(R.id.friendToProfileBtn);
        mBackToProfileButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_addFriend_to_profileFragment));
    }
}
