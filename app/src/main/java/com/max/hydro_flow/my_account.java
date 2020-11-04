package com.max.hydro_flow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.willy.ratingbar.ScaleRatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class my_account extends Fragment {
    Context mctx;
    View view;
    TextView tv_name, tv_gst, tv_pan;
    CircleImageView circleImageView;
    Button logout;
    ImageButton about_btn, feedback_btn;
    customer_data customerData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        mctx = view.getContext();
        tv_name = view.findViewById(R.id.textViewName);
        tv_gst = view.findViewById(R.id.textViewgstinno);
        tv_pan = view.findViewById(R.id.textViewpanno);
        about_btn = view.findViewById(R.id.about_btn);
        feedback_btn = view.findViewById(R.id.feedback_btn);
        circleImageView = view.findViewById(R.id.iv_cust_pic);
        logout = view.findViewById(R.id.logout_btn);
        Animation slideup = AnimationUtils.loadAnimation(mctx, R.anim.slide_up);
        slideup.setDuration(500);
        LinearLayout linearLayout = view.findViewById(R.id.myacc_display);
        linearLayout.startAnimation(slideup);
        linearLayout.setVisibility(View.VISIBLE);

        String id = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("customers").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    customerData = dataSnapshot.getValue(customer_data.class);
                    tv_name.setText(customerData.getName());
                    tv_gst.setText(customerData.getGstinNo());
                    tv_pan.setText(customerData.getPancardNo());
                    Glide.with(mctx).load(customerData.getPicurl()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(true).into(circleImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                my_account.this.startActivity(new Intent(mctx, login.class));
                ((MainActivity) mctx).finish();
            }
        });
        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog about = new Dialog(mctx);
                about.setContentView(R.layout.about_layout);
                about.setCancelable(true);
                about.setCanceledOnTouchOutside(true);
                Objects.requireNonNull(about.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Objects.requireNonNull(about.getWindow()).setBackgroundDrawableResource(R.drawable.black_transparent);
                about.show();
            }
        });
        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog feedback = new Dialog(mctx);
                feedback.setContentView(R.layout.feedback_dialog);
                feedback.setCancelable(true);
                feedback.setCanceledOnTouchOutside(true);
                feedback.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Objects.requireNonNull(feedback.getWindow()).setBackgroundDrawableResource(R.drawable.black_transparent);
                feedback.show();

                final EditText feedback_text = feedback.findViewById(R.id.feedback_text);
                final Button feedback_btn = feedback.findViewById(R.id.send_btn);
                final ScaleRatingBar ratingBar = feedback.findViewById(R.id.simpleRatingBar);
                feedback_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        feedback_btn.setEnabled(false);
                        DatabaseReference fbr = FirebaseDatabase.getInstance().getReference("feedbacks");
                        String id1 = FirebaseAuth.getInstance().getUid();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        hashMap.put("customerID", id1);
                        hashMap.put("feedback", String.valueOf(feedback_text.getText()));
                        hashMap.put("customerName", customerData.getName());
                        hashMap.put("picurl", customerData.getPicurl());
                        hashMap.put("rating", ratingBar.getRating());
                        hashMap.put("date", date);
                        fbr.child(id1).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                feedback.dismiss();
                                feedback_btn.setEnabled(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mctx, "Feedback Not sent,Try again", Toast.LENGTH_SHORT).show();
                                feedback_btn.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
        return view;
    }

}
