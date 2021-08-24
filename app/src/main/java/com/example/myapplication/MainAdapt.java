package com.example.myapplication;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainAdapt extends RecyclerView.Adapter {
    private ArrayList<HashMap<String, Object>> mainlist ;
    private MainFragmentClickListener mainFragmentClickListener;
    private StorageReference storage;

    public void setMainFragmentClickListener(MainFragmentClickListener mainFragmentClickListener) {
        this.mainFragmentClickListener = mainFragmentClickListener;
    }
    interface MainFragmentClickListener{
        void itemClick(HashMap<String, Object> data);
    }


    public MainAdapt(ArrayList<HashMap<String, Object>> list){
        mainlist = list;
        storage = FirebaseStorage.getInstance("gs://android-nail-shop.appspot.com/").getReference();

    }
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.main_itme_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder item = (MyViewHolder) holder;
        HashMap<String, Object> data = mainlist.get(position);
        item.textView.setText((String) data.get("user_name"));
        //item.image.setImageDrawable((Drawable) data.get("image"));
        String uri = (String) data.get("user_image");
        storage.child(uri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(item.itemView)
                        .load(uri)
                        .circleCrop()
                        .into(item.image);
            }
        });

        item.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFragmentClickListener.itemClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView image;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.main_item_text);
            image = itemView.findViewById(R.id.main_item_image);

        }
    }
}
