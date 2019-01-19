package com.example.leodw.worldepth.ui.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.data.User;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mName = new MutableLiveData<>();
    private MutableLiveData<Integer> mFriendCount = new MutableLiveData<>();

    private FirebaseWrapper mFb;
    private FirebaseDatabase mDb;

    public ProfileViewModel() {
        mFb = new FirebaseWrapper();//((MainActivity) getActivity()).getFirebaseWrapper();
        mDb = mFb.getFirebaseDatabase();
        setName();
        setFriendCount();
    }

    private void setName() {
        DatabaseReference dbRef = mDb.getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(mFb.getUid()).getValue(User.class);
                String fullname = user.firstName + " " + user.lastName;
                mName.setValue(fullname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setFriendCount() {
        mFriendCount.setValue(1);
    }

    public LiveData<String> getName() { return mName; }

    public LiveData<Integer> getFriendCount() { return mFriendCount; }

}
