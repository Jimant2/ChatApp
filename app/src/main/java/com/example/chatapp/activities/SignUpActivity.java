package com.example.chatapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.Utilities.PreferenceManager;
import com.example.chatapp.ViewModel.AuthImplViewModel;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK)
                {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        profileImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

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
        user.put(Constants.KEY_IMAGE, profileImage);

        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user).addOnSuccessListener(documentReference -> {
                preferenceManager.setBoolean(Constants.KEY_IS_SIGNED_IN, true);
                preferenceManager.setString(Constants.KEY_USER_ID, documentReference.getId());
                preferenceManager.setString(Constants.KEY_NAME, binding.inputName.getText().toString());
                preferenceManager.setString(Constants.KEY_IMAGE, profileImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private Boolean isValidSignUpData()
    {
        if (profileImage == null)
        {
            Toast.makeText(getApplicationContext(), "Please select a profile image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (binding.inputName.getText().toString().trim().isEmpty() ||
        binding.inputEmail.getText().toString().trim().isEmpty() ||
        binding.inputPassword.getText().toString().isEmpty() ||
        binding.inputConfirmPassword.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches())
        {
            Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }
}