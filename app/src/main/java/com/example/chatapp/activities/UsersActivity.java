package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapp.Adapters.UsersAdapter;
import com.example.chatapp.Listeners.UserListener;
import com.example.chatapp.Models.UserModel;
import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.Utilities.PreferenceManager;
import com.example.chatapp.databinding.ActivityUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners()
    {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if (task.isSuccessful() && task.getResult() != null)
                   {
                       List<UserModel> users = new ArrayList<>();
                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                           if (currentUserId.equals(queryDocumentSnapshot.getId()))
                           {
                               continue;
                           }
                           UserModel userModel = new UserModel();
                           userModel.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           userModel.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           userModel.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                           userModel.id = queryDocumentSnapshot.getId();
                           users.add(userModel);
                       }
                       if (users.size() > 0)
                       {
                           UsersAdapter usersAdapter = new UsersAdapter(users, this);
                           binding.usersRecyclerView.setAdapter(usersAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);
                       }
                       else
                       {
                           binding.textErrorMessage.setText(String.format("%s", "No user found"));
                           binding.textErrorMessage.setVisibility(View.VISIBLE);
                       }
                   }
                   else
                   {
                       binding.textErrorMessage.setText(String.format("%s", "No user found"));
                       binding.textErrorMessage.setVisibility(View.VISIBLE);
                   }
                });
    }

    @Override
    public void onUserClicked(UserModel userModel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, userModel);
        startActivity(intent);
        finish();
    }
}