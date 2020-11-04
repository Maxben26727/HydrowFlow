package com.max.hydro_flow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class order_dataAdapter extends RecyclerView.Adapter<order_dataViewHolder> {
    ProgressDialog progressDialog;
    private Context mctx;
    private List<order_data> order_dataList;
    private int lastPosition = -1;

    public order_dataAdapter(Context mctx, List<order_data> order_dataList) {
        this.mctx = mctx;
        this.order_dataList = order_dataList;
    }

    @NonNull
    @Override
    public order_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.orderd_cardview_layout, null);
        return new order_dataViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final order_dataViewHolder holder, final int position) {

        final order_data orderData = order_dataList.get(position);
        holder.orderID.setText(orderData.getOrderID());

        switch (orderData.getStatus()) {
            case "delivered":
                holder.iv1.setColorFilter(Color.GREEN);
                holder.iv11.setImageTintMode(null);
                holder.tv1.setTextColor(mctx.getColor(R.color.black));
                holder.c1.setImageTintMode(null);
                holder.view_invoice_btn.setVisibility(View.VISIBLE);

                holder.iv2.setColorFilter(Color.GREEN);
                holder.iv22.setImageTintMode(null);
                holder.tv2.setTextColor(mctx.getColor(R.color.black));
                holder.c2.setImageTintMode(null);

                holder.iv3.setColorFilter(Color.GREEN);
                holder.iv33.setImageTintMode(null);
                holder.tv3.setTextColor(mctx.getColor(R.color.black));
                holder.c3.setImageTintMode(null);

                holder.iv4.setColorFilter(Color.GREEN);
                holder.iv44.setImageTintMode(null);
                holder.tv4.setTextColor(mctx.getColor(R.color.black));

                holder.cancel.setVisibility(View.GONE);
                break;
            case "shipped":
                holder.iv1.setColorFilter(Color.GREEN);
                holder.iv11.setImageTintMode(null);
                holder.tv1.setTextColor(mctx.getColor(R.color.black));
                holder.c1.setImageTintMode(null);

                holder.iv2.setColorFilter(Color.GREEN);
                holder.iv22.setImageTintMode(null);
                holder.tv2.setTextColor(mctx.getColor(R.color.black));
                holder.c2.setImageTintMode(null);
                holder.view_invoice_btn.setVisibility(View.VISIBLE);

                holder.iv3.setColorFilter(Color.GREEN);
                holder.iv33.setImageTintMode(null);
                holder.tv3.setTextColor(mctx.getColor(R.color.black));
                holder.c3.setImageTintMode(null);
                break;
            case "accepted":
                holder.iv1.setColorFilter(Color.GREEN);
                holder.iv11.setImageTintMode(null);
                holder.tv1.setTextColor(mctx.getColor(R.color.black));
                holder.c1.setImageTintMode(null);
                holder.view_invoice_btn.setVisibility(View.VISIBLE);

                holder.iv2.setColorFilter(Color.GREEN);
                holder.iv22.setImageTintMode(null);
                holder.tv2.setTextColor(mctx.getColor(R.color.black));
                holder.c2.setImageTintMode(null);
                break;
            case "cancelled":
                holder.iv1.setColorFilter(Color.RED);
                holder.iv11.setColorFilter(Color.RED);
                holder.tv1.setTextColor(Color.RED);
                holder.tv1.setText("OrderCancelled");
                holder.c1.setVisibility(View.GONE);

                holder.iv2.setVisibility(View.GONE);
                holder.iv22.setVisibility(View.GONE);
                holder.tv2.setVisibility(View.GONE);
                holder.c2.setVisibility(View.GONE);

                holder.iv3.setVisibility(View.GONE);
                holder.iv33.setVisibility(View.GONE);
                holder.tv3.setVisibility(View.GONE);
                holder.c3.setVisibility(View.GONE);

                holder.iv4.setVisibility(View.GONE);
                holder.iv44.setVisibility(View.GONE);
                holder.tv4.setVisibility(View.GONE);

                holder.cancel.setVisibility(View.GONE);
                holder.view_invoice_btn.setVisibility(View.GONE);
                holder.view_item.setVisibility(View.GONE);
                break;

        }
        holder.track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideup = AnimationUtils.loadAnimation(mctx, R.anim.slide_up);
                slideup.setDuration(300);
                if (holder.track_layout.getVisibility() == View.GONE) {
                    holder.track_layout.startAnimation(slideup);
                    holder.track_layout.setVisibility(View.VISIBLE);
                } else {
                    holder.track_layout.setVisibility(View.GONE);
                }
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(mctx);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                dialog.setCanceledOnTouchOutside(false);
                TextView title, message;
                Button yes, no;
                title = dialog.findViewById(R.id.alert_title);
                message = dialog.findViewById(R.id.alert_message);
                yes = dialog.findViewById(R.id.yes_btn);
                no = dialog.findViewById(R.id.no_btn);
                dialog.setCancelable(true);
                title.setText("ARE U SURE?");
                message.setText("Selected Order will be Cancelled");
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("orders");
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("status", "cancelled");
                        hashMap.put("assigned_area", "cancelled_by_user");
                        reference.child(orderData.getOrderID()).updateChildren(hashMap);
                        dialog.dismiss();
                    }
                });


                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
        holder.view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference_cart = FirebaseDatabase.getInstance().getReference("orders").child(orderData.getOrderID());
                databaseReference_cart.child("orderItems").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<cart_data> cart_dataList=new ArrayList<>();
                        Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                        for (DataSnapshot itemSnapshot : childrens) {
                            cart_data cartData = itemSnapshot.getValue(cart_data.class);
                            cart_dataList.add(cartData);
                        }
                        myorders.back.setVisibility(View.VISIBLE);
                      myorders.order_text.setText(orderData.getOrderID());
                        orderItem_dataAdapter adapter = new orderItem_dataAdapter(mctx, cart_dataList,orderData.getStatus(),orderData.getOrderID());
                        myorders.recyclerView.setAdapter(adapter);
                        myorders.databaseReference_orders.removeEventListener(myorders.listener);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
        holder.date.setText(orderData.getDate());
        holder.view_invoice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(mctx);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progreedialog);
                progressDialog.setCanceledOnTouchOutside(false);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                String filename = String.format("%s.pdf", order_dataList.get(position).getOrderID());
                final File pdfFile = new File(String.format("%s/docs/%s", mctx.getExternalFilesDir(""), filename));
                if(!pdfFile.exists()) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference().child("invoices").child(String.format("%s.pdf", orderData.getOrderID()));

                    String extStorageDirectory = mctx.getExternalFilesDir("").getPath();
                    File rootPath = new File(extStorageDirectory, "docs");
                    if (!rootPath.exists()) {
                        rootPath.mkdirs();
                    }

                    final File localFile = new File(rootPath, filename);

                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Uri path = FileProvider.getUriForFile(mctx, mctx.getPackageName()+".provider", pdfFile);
                            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setDataAndType(path, "application/pdf");
                            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            //  updateDb(timestamp,localFile.toString(),position);
                            progressDialog.dismiss();
                            mctx.startActivity(pdfIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                }else
                {
                Uri path = FileProvider.getUriForFile(mctx, mctx.getPackageName()+".provider", pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                progressDialog.dismiss();
                    mctx.startActivity(pdfIntent);

                }


            }

        });
       // setAnimaton(holder.itemView, position);
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
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return order_dataList.size();
    }


}

class order_dataViewHolder extends RecyclerView.ViewHolder {

    TextView orderID,date, tv1, tv2, tv3, tv4;
    ImageView iv1, iv2, iv3, iv4;
    ImageView iv11, iv22, iv33, iv44;
    ImageView c1, c2, c3;
    Button view_invoice_btn, track_btn;
    ImageButton view_item, cancel;
    RelativeLayout track_layout;

    order_dataViewHolder(View itemView) {
        super(itemView);

        tv1 = itemView.findViewById(R.id.o_tv_1);
        tv2 = itemView.findViewById(R.id.o_tv_2);
        tv3 = itemView.findViewById(R.id.o_tv_3);
        tv4 = itemView.findViewById(R.id.o_tv_4);

        iv1 = itemView.findViewById(R.id.orderPlacedDone);
        iv2 = itemView.findViewById(R.id.orderacceptedDone);
        iv3 = itemView.findViewById(R.id.ordershippingdDone);
        iv4 = itemView.findViewById(R.id.orderdeliverydDone);

        iv11 = itemView.findViewById(R.id.orderPlacedicon);
        iv22 = itemView.findViewById(R.id.orderacceptedicon);
        iv33 = itemView.findViewById(R.id.ordershippedicon);
        iv44 = itemView.findViewById(R.id.orderdeliveredicon);

        c1 = itemView.findViewById(R.id.chain1);
        c2 = itemView.findViewById(R.id.chain2);
        c3 = itemView.findViewById(R.id.chain3);


        orderID = itemView.findViewById(R.id.orderid);
        view_invoice_btn = itemView.findViewById(R.id.viewinvoice_btn);
        track_btn = itemView.findViewById(R.id.track_btn);
        view_item = itemView.findViewById(R.id.item_view_btn);
        cancel = itemView.findViewById(R.id.cancel_btn);
        track_layout = itemView.findViewById(R.id.track_layout);
        date=itemView.findViewById(R.id.date);
    }
}