package com.example.chatapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.UserModel;
import com.example.chatapp.databinding.ItemContainerUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private List<UserModel> users;

    public UsersAdapter(List<UserModel> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private Bitmap getUserImage(String profileImage)
    {
        byte[] bytes = Base64.decode(profileImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

       public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding)
        {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

       public void setUserData(UserModel userModel)
       {
           binding.textName.setText(userModel.name);
           binding.textEmail.setText(userModel.email);
           binding.imageProfile.setImageBitmap(getUserImage(userModel.image));
       }

    }


}
