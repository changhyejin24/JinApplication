package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentSearchBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class SearchFragment extends Fragment {

    private String uid;
    private FragmentSearchBinding sfBind;
    public SearchFragment() {
    }

    public static SearchFragment newInstance(String uid) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");

        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        sfBind = FragmentSearchBinding.inflate(inflater,container,false);
        return (sfBind.getRoot());


    }

    @Override
    public void onStart() {
        super.onStart();
        sfBind.outlinedTextField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = sfBind.outlinedTextField.getEditText().getText().toString();
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("검색")
                        .setMessage(a)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(getView(),a,Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        });
        SearchAdapt searchAdapt = new SearchAdapt();
        sfBind.searchList.setLayoutManager(new LinearLayoutManager(requireContext()));
        sfBind.searchList.setAdapter(searchAdapt);

    }
}
