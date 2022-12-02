package com.example.chatapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp.Authentication.AuthImplementation;
import com.google.firebase.auth.FirebaseUser;

public class AuthImplViewModel extends AndroidViewModel {

    private AuthImplementation authImplementation;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthImplViewModel(@NonNull Application application) {
        super(application);
        authImplementation = new AuthImplementation(application);
        userData = authImplementation.getFirebaseUserMutableLiveData();
        loggedStatus = authImplementation.getUserLoggedMutableLiveData();
    }

    public void register(String email, String password) {
        authImplementation.register(email, password);
    }

    public void signIn(String email, String password) {
        authImplementation.login(email, password);
    }

    public void signOut() {
        authImplementation.signOut();
    }
}
