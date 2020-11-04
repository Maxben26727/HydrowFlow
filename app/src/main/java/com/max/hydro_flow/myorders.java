package com.max.hydro_flow;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;


public class myorders extends Fragment {
    private Context mctx;
    private static List<order_data> order_dataList = new ArrayList<order_data>();
    static RecyclerView recyclerView;
    static TextView order_text;
    private order_dataAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageView;
    private RelativeLayout relativeLayout;
    public static LinearLayout back;
    static ValueEventListener listener;
    static Query databaseReference_orders;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myorders, container, false);
        mctx = view.getContext();
        swipeRefreshLayout = view.findViewById(R.id.order_refresh);
        ImageButton order_back = view.findViewById(R.id.back_to_order);
        back = view.findViewById(R.id.backbtn);
        order_text = view.findViewById(R.id.order_text);
        recyclerView = view.findViewById(R.id.order_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mctx));
        imageView = view.findViewById(R.id.empty_order_iv);
        relativeLayout = view.findViewById(R.id.order_display);


        String customerID = FirebaseAuth.getInstance().getUid();
        assert customerID != null;
         databaseReference_orders = FirebaseDatabase.getInstance().getReference("orders").orderByChild("customerID").equalTo(customerID);
         listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order_dataList.clear();
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    order_data orderData = itemSnapshot.getValue(order_data.class);
                    order_dataList.add(orderData);
                }
                Collections.reverse(order_dataList);
                adapter = new order_dataAdapter(mctx, order_dataList);
                recyclerView.setAdapter(adapter);
                if (adapter.getItemCount() > 0 && relativeLayout.getVisibility() == View.GONE) {
                    imageView.setVisibility(View.GONE);
                    Animation slideup = AnimationUtils.loadAnimation(mctx, R.anim.slide_up);
                    slideup.setDuration(500);
                    relativeLayout.startAnimation(slideup);
                    relativeLayout.setVisibility(View.VISIBLE);
                } else if (adapter.getItemCount() == 0) {
                    relativeLayout.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
         databaseReference_orders.addValueEventListener(listener);
        order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.GONE);
                databaseReference_orders.addValueEventListener(listener);
                adapter = new order_dataAdapter(mctx, order_dataList);
                recyclerView.setAdapter(adapter);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

}
