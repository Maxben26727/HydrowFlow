package com.max.hydro_flow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.willy.ratingbar.ScaleRatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class orderItem_dataAdapter extends RecyclerView.Adapter<orderItem_dataViewHolder> {
    private Context mctx;
    private int lastPosition = -1;
    private List<cart_data> orderItemList;
    String status;
    String orderID;

    public orderItem_dataAdapter(Context mctx, List<cart_data> orderItemList,String status,String orderID) {
        this.mctx = mctx;
        this.orderItemList = orderItemList;
        this.status = status;
        this.orderID = orderID;
    }


    @NonNull
    @Override
    public orderItem_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.orderitem_item_layout, null);
        return new orderItem_dataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final orderItem_dataViewHolder holder, int position) {
        final cart_data cartdata = orderItemList.get(position);
        if(status.equals("delivered")&&!cartdata.getRated_state())
        {
            holder.rating_layout.setVisibility(View.VISIBLE);
            holder.rate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("products").child(cartdata.getProductID()).child("reviews");
                    HashMap<String,Object> hashMap=new HashMap<>();
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    hashMap.put("customerID", FirebaseAuth.getInstance().getUid());
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("customers").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                customer_data cd = dataSnapshot.getValue(customer_data.class);
                                hashMap.put("customerName", cd.getName());
                                hashMap.put("picurl", cd.getPicurl());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    hashMap.put("feedback", holder.comments.getText().toString());
                    hashMap.put("rating", holder.scaleRatingBar.getRating());
                    hashMap.put("date", date);
                    reference.child(FirebaseAuth.getInstance().getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          DatabaseReference ref=FirebaseDatabase.getInstance().getReference("orders").child(orderID);
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("rated_state",true);
                             ref.child("orderItems").child(cartdata.getProductID()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     holder.rating_layout.setVisibility(View.GONE);
                                 }
                             });
                        }
                    });
                }
            });
        }
        else
        {
            holder.rating_layout.setVisibility(View.GONE);
        }

        Glide.with(mctx).load(cartdata.getProductImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.productpic);
        holder.titletv.setText(cartdata.getProductName());
        holder.qty.setText(cartdata.getCartqty());
        holder.pricetv.setText(String.format("RS.%s", cartdata.getProductPrice()));
        holder.totalPrice.setText(String.format("GTotal:RS.%s/-", cartdata.getTotalPrice()));
        setAnimaton(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public void setAnimaton(View view, int position) {
        if (position > lastPosition) {
            Animation fadout = new AlphaAnimation(1, 0);
            fadout.setStartOffset(1500);
            fadout.setDuration(1500);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(1500);
            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(fadein);
            animation.addAnimation(fadout);
            view.setAnimation(fadein);

        }
        lastPosition = position;
    }
}

class orderItem_dataViewHolder extends RecyclerView.ViewHolder {
    TextView titletv, pricetv, totalPrice,qty;
    ImageView productpic;
    LinearLayout rating_layout;
    EditText comments;
    Button rate_btn;
    ScaleRatingBar scaleRatingBar;
    orderItem_dataViewHolder(View itemView) {
        super(itemView);
        productpic = itemView.findViewById(R.id.cart_product_iv);
        titletv = itemView.findViewById(R.id.cart_product_name);
        pricetv = itemView.findViewById(R.id.cart_product_price);
        totalPrice = itemView.findViewById(R.id.cart_product_total);
        qty=itemView.findViewById(R.id.qty);
        rating_layout=itemView.findViewById(R.id.rate_layout);
        comments=itemView.findViewById(R.id.feedback_text);
        rate_btn=itemView.findViewById(R.id.rate_btn);
        scaleRatingBar=itemView.findViewById(R.id.simpleRatingBar);
    }
}