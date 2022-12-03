package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.Utilities.PreferenceManager;
import com.example.chatapp.ViewModel.AuthImplViewModel;
import com.example.chatapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AuthImplViewModel authImplViewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        authImplViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())).get(AuthImplViewModel.class);
        authImplViewModel.getLoggedStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        showUserDetails();
        setSignOutListeners();
    }

    private void setSignOutListeners()
    {
        binding.imageSignOut.setOnClickListener(v -> signOut());
    }

    private void showUserDetails()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void signOut()
    {
        Toast.makeText(getApplicationContext(), "Signing out..", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        })
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Could not sign out", Toast.LENGTH_SHORT).show());
    }
}