package com.max.hydro_flow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class cart_dataAdapter extends RecyclerView.Adapter<cart_dataViewHolder> {
    private Context mctx;
    private int lastPosition = -1;
    private List<cart_data> cart_dataList;
    private String customerID;
    private DatabaseReference databaseReference_cart;

    public cart_dataAdapter(Context mctx, List<cart_data> cart_dataList) {
        this.mctx = mctx;
        this.cart_dataList = cart_dataList;
        customerID = FirebaseAuth.getInstance().getUid();
        databaseReference_cart = FirebaseDatabase.getInstance().getReference("cart");
    }


    @NonNull
    @Override
    public cart_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.cart_item_layout, null);
        return new cart_dataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cart_dataViewHolder holder, int position) {
        final cart_data cartdata = cart_dataList.get(position);
        Glide.with(mctx).load(cartdata.getProductImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.productpic);
        holder.titletv.setText(cartdata.getProductName());
        holder.pricetv.setText(String.format("RS.%s", cartdata.getProductPrice()));
        holder.numberButton.setNumber(cartdata.getCartqty());
        holder.totalPrice.setText(String.format("GTotal:RS.%s/-", cartdata.getTotalPrice()));
        holder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {


                int a = Integer.parseInt(holder.numberButton.getNumber());
                int b = Integer.parseInt(cartdata.getProductPrice());
                int c = a * b;
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("cartqty", String.valueOf(a));
                hashMap.put("totalPrice", String.valueOf(c));
                databaseReference_cart.child(customerID).child(cartdata.getProductID()).updateChildren(hashMap);

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                assert customerID != null;
                databaseReference_cart.child(customerID).child(cartdata.getProductID()).removeValue();

            }
        });
        //setAnimaton(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return cart_dataList.size();
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

class cart_dataViewHolder extends RecyclerView.ViewHolder {
    TextView titletv, pricetv, totalPrice;
    ImageView productpic;
    ImageButton delete;
    ElegantNumberButton numberButton;

    cart_dataViewHolder(View itemView) {
        super(itemView);
        productpic = itemView.findViewById(R.id.cart_product_iv);
        titletv = itemView.findViewById(R.id.cart_product_name);
        pricetv = itemView.findViewById(R.id.cart_product_price);
        totalPrice = itemView.findViewById(R.id.cart_product_total);
        delete = itemView.findViewById(R.id.cart_remove_btn);
        numberButton = itemView.findViewById(R.id.number_button);

    }
}