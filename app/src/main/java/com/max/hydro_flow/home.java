package com.max.hydro_flow;


import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class home extends Fragment {
    private Context context;
    List<advert_data> advert_dataList = new ArrayList();
    private List<product_data> product_dataList = new ArrayList();
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SliderView sliderView;
    SearchView searchView;
    private SliderAdapter slideradapter;
    product_dataAdapter adapter;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.black);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ImageView loading = dialog.findViewById(R.id.progress_image);
        ObjectAnimator a = ObjectAnimator.ofFloat(loading, "rotation", 0f, 360f);
        a.setDuration(3000);
        a.setRepeatCount(Animation.INFINITE);
        a.start();
        dialog.show();
        //Toast.makeText(context, advert_dataList.size(), Toast.LENGTH_SHORT).show();

        sliderView = view.findViewById(R.id.imageSlider);
        searchView = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.allproduct_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DatabaseReference databaseReference_product = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference databaseReference_advert = FirebaseDatabase.getInstance().getReference("advertisements");
       databaseReference_product.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                product_dataList.clear();
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    product_data productData = itemSnapshot.getValue(product_data.class);
                    product_dataList.add(productData);
                }
                adapter = new product_dataAdapter(context, product_dataList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

       databaseReference_advert.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advert_dataList.clear();
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    advert_data advertData = itemSnapshot.getValue(advert_data.class);
                    // categoryData.setKey(itemSnapshot.getKey());
                    advert_dataList.add(advertData);
                }
                dialog.dismiss();
                slideradapter = new SliderAdapter(context);
                slideradapter.renewItems(advert_dataList);
                sliderView.setSliderAdapter(slideradapter);
                sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                sliderView.setIndicatorSelectedColor(Color.WHITE);
                sliderView.setIndicatorUnselectedColor(Color.GRAY);
                sliderView.startAutoCycle();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        swipeRefreshLayout = view.findViewById(R.id.home_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                slideradapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Objects.requireNonNull( adapter.getFilter()).filter(newText);

                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 50) {
                    Animation slidedown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                    slidedown.setDuration(300);
                    ((MainActivity) context).bnavigation.startAnimation(slidedown);
                    ((MainActivity) context).bnavigation.setVisibility(View.GONE);
                } else if (dy < -25 && ((MainActivity) context).bnavigation.getVisibility() == View.GONE) {
                    Animation slideup = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                    slideup.setDuration(300);
                    ((MainActivity) context).bnavigation.startAnimation(slideup);
                    ((MainActivity) context).bnavigation.setVisibility(View.VISIBLE);

                }

            }
        });
        return view;
    }


}
