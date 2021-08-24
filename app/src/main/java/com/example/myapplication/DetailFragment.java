package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.FragmentDetailBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DetailFragment extends Fragment implements OnMapReadyCallback{

    private MyViewModel myViewModel;
    private SupportMapFragment mapFragment;
    private FragmentDetailBinding fragmentDetailBinding;
    private StorageReference storage;

    private double lat;
    private double lng;
    private String title;
    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        myViewModel = new ViewModelProvider(getActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(MyViewModel.class);
        storage = FirebaseStorage.getInstance("gs://android-nail-shop.appspot.com/").getReference();

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        googleMap.addMarker(markerOptions);
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.584, 126.899))
//                .title("4D트래블러 사무실2"));
//        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.moveCamera(cameraUpdate);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentDetailBinding = FragmentDetailBinding.inflate(inflater,container,false);

        return fragmentDetailBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = ( SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detail_map);

        myViewModel.user.observe(getViewLifecycleOwner(), new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> user) {
                fragmentDetailBinding.detailTitle.setText((String) user.get("user_name"));
                fragmentDetailBinding.detailRating.setText(user.get("rat").toString());
                fragmentDetailBinding.detailLink.setText((String) user.get("link"));
                fragmentDetailBinding.detailLocate.setText((String) user.get("locate"));
                String uri = (String) user.get("user_image");
                storage.child(uri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext())
                                .load(uri)
                                .centerCrop()
                                .into(fragmentDetailBinding.detailImage);
                    }
                });

                lat = (double) user.get("lat");
                lng = (double) user.get("lng");
                title = (String) user.get("user_name");
                mapFragment.getMapAsync(DetailFragment.this::onMapReady);
                fragmentDetailBinding.detailLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebPage((String) user.get("link"));
                    }
                });

            }
        });


    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
