package com.example.leodw.worldepth.ui.signup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;

import androidx.navigation.Navigation;

public class SignupViewModel extends ViewModel {

    private static final String TAG = "SignupViewModel";

    private FirebaseWrapper mFb;
    private FirebaseDatabase mDb;

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Date birthday;
    private final MutableLiveData<Boolean> loginState = new MutableLiveData<>();

    public SignupViewModel() {
        mFb = new FirebaseWrapper();
        mDb = mFb.getFirebaseDatabase();
    }

    public LiveData<Boolean> getLoginState() { return loginState; }

    public void setLoginState(boolean state) { this.loginState.setValue(state); }

    public void setFirstName(String name) { this.firstName = name; }

    public void setLastName(String name) { this.lastName = name; }

    public void setPassword(String password) { this.password = password; }

    public void setEmail(String email) { this.email = email; }

    public void setBirthday(int year, int month, int date) { this.birthday = new Date(year, month, date); }

    public void createNewAccount() {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        FirebaseDatabase database = mFb.getFirebaseDatabase();
        _auth.createUserWithEmailAndPassword(Objects.requireNonNull(this.email), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFb.createNewAccount(this.firstName, this.lastName, this.email, this.birthday, this.password);
                //set login state
                setLoginState(true);
            } else {
                Log.d(TAG, "createNewAccount:failed", task.getException());
            }
        });
    }

}
