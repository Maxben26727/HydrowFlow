package com.max.hydro_flow;


import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class cart extends Fragment {
    private static Context mctx;
    customer_data customerData;
    private List<cart_data> cart_dataList = new ArrayList<>();
    public static RecyclerView recyclerView;
    private static Button order_btn;
    public static cart_dataAdapter adapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    static ImageView imageView;
    String customerID;
    static RelativeLayout relativeLayout;
    View view;
    int total;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        mctx = view.getContext();
        swipeRefreshLayout = view.findViewById(R.id.cart_refresh);
        order_btn = view.findViewById(R.id.order_bn_btn);
        imageView = view.findViewById(R.id.empty_iv);
        relativeLayout = view.findViewById(R.id.cart_display);
        recyclerView = view.findViewById(R.id.cart_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mctx));


        customerID = FirebaseAuth.getInstance().getUid();
        assert customerID != null;
        final DatabaseReference databaseReference_cart = FirebaseDatabase.getInstance().getReference("cart").child(customerID);
        ValueEventListener eventListener = databaseReference_cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cart_dataList.clear();
                 total=0;
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    cart_data cartData = itemSnapshot.getValue(cart_data.class);
                    cart_dataList.add(cartData);
                    total+=Integer.parseInt(cartData.getTotalPrice());
                }
                adapter = new cart_dataAdapter(mctx, cart_dataList);
                recyclerView.setAdapter(adapter);
                order_btn.setText(String.format("PLACE ORDER (RS.%s)",String.valueOf(total)));

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

        });

        order_btn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {


                                             progressDialog = new ProgressDialog(mctx);
                                             progressDialog.show();
                                             progressDialog.setContentView(R.layout.progreedialog);
                                             progressDialog.setCanceledOnTouchOutside(false);
                                             Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);

                                             final DatabaseReference databaseReference_order = FirebaseDatabase.getInstance().getReference("orders");
                                             final String id = databaseReference_order.push().getKey();
                                             String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                             assert customerData != null;
                                             order_data orderData = new order_data(id, customerID, date, "not_accepted", "not_assigned","invoice_not_generated",String.valueOf(total));
                                             databaseReference_order.child(id).setValue(orderData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {

                                                     databaseReference_cart.addListenerForSingleValueEvent(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                             Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                                                             for (DataSnapshot itemSnapshot : childrens) {
                                                                 cart_data cartData = itemSnapshot.getValue(cart_data.class);
                                                                 assert cartData != null;

                                                                 databaseReference_order.child(id).child("orderItems").child(cartData.getProductID()).setValue(cartData);
                                                             }
                                                             databaseReference_cart.removeValue();
                                                             progressDialog.dismiss();
                                                             Toast.makeText(mctx, "Order Placed", Toast.LENGTH_SHORT).show();


                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                                         }
                                                     });
                                                 }
                                             });
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
