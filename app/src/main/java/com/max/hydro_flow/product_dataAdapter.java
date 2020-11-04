package com.max.hydro_flow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class product_dataAdapter extends RecyclerView.Adapter<product_dataViewHolder> implements Filterable {


    ProgressDialog progressDialog;
    private Context mctx;
    private int lastPosition=-1;
    private List<product_data> product_dataList;
    private List<product_data> product_dataListFull;
    DatabaseReference databaseReference_cart;

    String customerID;

    Dialog dialog;
    public product_dataAdapter(Context mctx, List<product_data> product_dataList) {
        this.mctx = mctx;
        this.product_dataList = product_dataList;
        product_dataListFull = new ArrayList<>(product_dataList);
        databaseReference_cart= FirebaseDatabase.getInstance().getReference("cart");
        customerID = FirebaseAuth.getInstance().getUid();


    }

    @NonNull
    @Override
    public product_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.product_item_layout, null);
        return new product_dataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull product_dataViewHolder holder, int position) {
         final product_data productData = product_dataList.get(position);

        holder.titletv.setText(productData.getProductName());
        holder.pricetv.setText(String.format("RS.%s/-",productData.getProductPrice()));
        Glide.with(mctx).load(productData.getProductpicurl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.productpic);
        holder.add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    progressDialog = new ProgressDialog(mctx);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progreedialog);
                    progressDialog.setCanceledOnTouchOutside(false);
                    Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);


                databaseReference_cart.child(customerID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(productData.getProductID()))
                        {
                            Toast.makeText(mctx, "Already Added", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        else
                        {
                            cart_data cartData=new cart_data(productData.getProductID(),productData.getProductName(),productData.getProductPrice(),
                                    productData.getProductpicurl(),"1",productData.getProductPrice(),productData.getProductUnit(),false);
                            databaseReference_cart.child(customerID).child(productData.getProductID()).setValue(cartData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(mctx, "Added to Cart", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        });

        setAnimaton(holder.itemView,position);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mctx,product_view.class);
                    i.putExtra("productID",productData.getProductID());
                    mctx.startActivity(i);

                }
            });
            }




    @Override
    public int getItemCount() {
        return product_dataList.size();
    }


    public void setAnimaton(View view, int position) {
        if (position>lastPosition) {
            Animation fadout=new AlphaAnimation(1,0);
            fadout.setStartOffset(1500);
            fadout.setDuration(1500);
            Animation fadein=new AlphaAnimation(0,1);
            fadein.setDuration(1500);
            AnimationSet animation=new AnimationSet(true);
            animation.addAnimation(fadein);
            animation.addAnimation(fadout);
            view.setAnimation(fadein);

        }
        lastPosition=position;
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }
    private  Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<product_data> filterdList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filterdList.addAll(product_dataListFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(product_data item:product_dataListFull){
                    if(item.getProductName().toLowerCase().contains(filterPattern)){
                        filterdList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values = filterdList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            product_dataList.clear();
            product_dataList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}

class product_dataViewHolder extends RecyclerView.ViewHolder {
    TextView titletv, pricetv;
    ImageView productpic;


    ImageButton add_cart_btn;
    product_dataViewHolder(View itemView) {
        super(itemView);
        productpic=itemView.findViewById(R.id.iv_product_image);
        titletv = itemView.findViewById(R.id.tv_product_name);
        pricetv = itemView.findViewById(R.id.tv_product_price);
        add_cart_btn=itemView.findViewById(R.id.addtocart_btn);



    }
}