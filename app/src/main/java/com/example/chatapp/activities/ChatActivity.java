package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chatapp.Models.UserModel;
import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.databinding.ActivityChatBinding;


public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private UserModel receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        getReceiverDetails();
    }

    private void getReceiverDetails()
    {
        receiverUser = (UserModel) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners()
    {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}