package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.FragmentUserBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class UserFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private MyViewModel myViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private String uid;
    private FragmentUserBinding fuBind;
    public final int GET_G = 102;
    public final int GET_SING_G = 103;
    public UserFragment() {
    }

    public static UserFragment newInstance(String uid) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");

        }
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://android-nail-shop.appspot.com/");
        mAuth = FirebaseAuth.getInstance();

        myViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(MyViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fuBind = FragmentUserBinding.inflate(inflater, container, false);
        fuBind.myNameEdit.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fuBind.myNameEdit.getEditText().getText().toString();
                db.collection("users").document(uid).update("user_name", name)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                fuBind.userNameView.setText(name);
                            }
                        });
            }
        });
        fuBind.myDetailEdit.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detail = fuBind.myDetailEdit.getEditText().getText().toString();
                HashMap<String,Object> detailmap = new HashMap<>();
                detailmap.put("user_detail", detail);
                db.collection("users").document(uid).update(detailmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fuBind.userDetailView.setText(detail);
                    }
                });
            }
        });
        fuBind.userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GET_G);
            }
        });


        return fuBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @org.jetbrains.annotations.NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myViewModel.user.observe(getViewLifecycleOwner(), new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> user) {
                fuBind.userNameView.setText((String) user.get("user_name"));
                fuBind.userDetailView.setText((String) user.get("user_detail"));
                String uri = (String) user.get("user_image");
                StorageReference storageRef = storage.getReference();
                storageRef.child(uri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(requireContext())
                                .load(uri)
                                .centerCrop()
                                .into(fuBind.userImageView);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_G){
            Uri uri = data.getData();
            StorageReference storageRef = storage.getReference();
            storageRef.child("user_image/"+uid+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    db.collection("users").document(uid).update("user_image","user_image/"+uid+".jpg")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Glide.with(getContext())
                                            .load(uri)
                                            .centerCrop()
                                            .into(fuBind.userImageView);
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        }
        else if(requestCode == GET_SING_G){
            Log.d("login", "firebaseAuthWithGoogle");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult();
            fuBind.userNameView.setText(account.getDisplayName());
            Log.d("login", "firebaseAuthWithGoogle:" + account.getId());
//                    firebaseAuthWithGoogle(account.getIdToken());

            firebaseAuthWithGoogle(account.getIdToken());
        }

    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fuBind.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GET_SING_G);
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                Toast.makeText(getContext(), user.getEmail(),Toast.LENGTH_LONG).show();
            }
        });
    }
}