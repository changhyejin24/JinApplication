package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.databinding.FragmentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class MainFragment extends Fragment {

    private String uid;
    private FragmentMainBinding fmBind;
    private MainAdapt.MainFragmentClickListener mainFragmentClickListener;
    public void setAdaptListener(MainAdapt.MainFragmentClickListener mainFragmentClickListener){
        this.mainFragmentClickListener = mainFragmentClickListener;
    }
    public MainFragment() { }


    public static MainFragment newInstance(String uid) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString("uid",uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fmBind = FragmentMainBinding.inflate(inflater, container, false);

        return fmBind.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        HashMap<String, Object> a = new HashMap<>();
//        a.put("id", "wewerwer");
//        a.put("image", getResources().getDrawable(R.drawable.image4));
//        HashMap<String, Object> c = new HashMap<>();
//        c.put("id", "asdafwefsdf");
//        c.put("image", getResources().getDrawable(R.drawable.image3));
//
//        HashMap<String, Object> d = new HashMap<>();
//        d.put("id", "dvsfsfvsdv");
//        d.put("image", getResources().getDrawable(R.drawable.image2));

//        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
//        list.add(a);
//        list.add(c);
//        list.add(d);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        db.collection("users").whereNotEqualTo("user_id", "header").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add((HashMap<String, Object>) document.getData());
                    }
                    MainAdapt mainAdapt = new MainAdapt(list);
                    mainAdapt.setMainFragmentClickListener(mainFragmentClickListener);
                    fmBind.textListview.setLayoutManager(new GridLayoutManager(requireContext(),2));
                    fmBind.textListview.setAdapter(mainAdapt);
                } else {
                }
            }
        });
    }
}
