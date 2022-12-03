package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.Utilities.PreferenceManager;
import com.example.chatapp.ViewModel.AuthImplViewModel;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Observer;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String profileImage;
    private AuthImplViewModel authImplViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authImplViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())).get(AuthImplViewModel.class);


        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners()
    {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpData())
            {
                signUp();
            }
        });
    }

//    private void showToast(String message)
//    {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }

    private void signUp()
    {
        if(!binding.inputEmail.getText().toString().trim().isEmpty()
                && !binding.inputPassword.getText().toString().trim().isEmpty())
        {
            authImplViewModel.register(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString());
            System.out.println("authenticating sign up");
        }

        //adding user to Cloud Firestore db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());

        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user).addOnSuccessListener(documentReference -> {
                preferenceManager.setBoolean(Constants.KEY_IS_SIGNED_IN, true);
                preferenceManager.setString(Constants.KEY_USER_ID, documentReference.getId());
                preferenceManager.setString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private Boolean isValidSignUpData()
    {
//        if (profileImage == null)
//        {
//            showToast("Please select a profile image");
//            return false;
//        }
//        else
        if (binding.inputName.getText().toString().trim().isEmpty() ||
        binding.inputEmail.getText().toString().trim().isEmpty() ||
        binding.inputPassword.getText().toString().isEmpty() ||
        binding.inputConfirmPassword.getText().toString().isEmpty())
        {
            //showToast("All fields must be filled");
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches())
        {
           // showToast("Enter valid email");
            Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString()))
        {
            //showToast("Passwords do not match");
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }
}