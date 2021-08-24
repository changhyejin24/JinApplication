package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainAdapt.MainFragmentClickListener {

    public FirebaseFirestore db;
    private int num;
    private ActivityMainBinding amBind;
    private MyViewModel myViewModel;


    @Override
    public void itemClick(HashMap<String, Object> data) {
        myViewModel.setUser(data);
        getSupportFragmentManager().beginTransaction()
                .replace(amBind.mainContainer.getId(), DetailFragment.newInstance())
                .addToBackStack("detailFragment")
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        amBind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(amBind.getRoot());
        db = FirebaseFirestore.getInstance();

        myViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MyViewModel.class);

        db.collection("users").document("wjieiXsii2FsGYpx6kXH").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String, Object> user = (HashMap<String, Object>) documentSnapshot.getData();
                myViewModel.setUser(user);

            }
        });
        View.OnClickListener clickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        amBind.button1.setOnClickListener(clickListener1);
        amBind.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getSupportFragmentManager().beginTransaction()
                       .replace(amBind.mainContainer.getId(), UserFragment.newInstance("nail"))
                       .addToBackStack("subFrag")
                       .commit();
            }
        });

        BottomNavigationView.OnNavigationItemSelectedListener naviationlistener  = new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if(item.getItemId() == R.id.menu_main){
                    MainFragment mainFragment = MainFragment.newInstance("asfasfasfas");
                    mainFragment.setAdaptListener(MainActivity.this::itemClick);
                    getSupportFragmentManager().beginTransaction()
                            .replace(amBind.mainContainer.getId(), mainFragment)
                            .addToBackStack("secsubFrag")
                            .commit();
                    return true;
                }
                else if (item.getItemId() == R.id.menu_user){
                    getSupportFragmentManager().beginTransaction()
                            .replace(amBind.mainContainer.getId(), UserFragment.newInstance("wjieiXsii2FsGYpx6kXH"))
                            .addToBackStack("trdsubFrag")
                            .commit();
                    return true;
                }
                else if(item.getItemId() == R.id.search){
                    getSupportFragmentManager().beginTransaction()
                            .replace(amBind.mainContainer.getId(),SearchFragment.newInstance("anything3"))
                            .addToBackStack("foursubFrag")
                            .commit();
                    return true;
                }
                else {
                    return false;
                }
            }
        };
        amBind.bottomNavigation.setOnNavigationItemSelectedListener(naviationlistener);



    }



    private void acb() {
        num = 10;
    }
}