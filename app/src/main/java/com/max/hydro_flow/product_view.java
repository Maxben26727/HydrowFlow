package com.max.hydro_flow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.util.Objects;
import java.util.Random;

public class product_view extends AppCompatActivity {
    product_data productData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        final ImageView product_image=findViewById(R.id.iv_product_image);
        final TextView product_name=findViewById(R.id.tv_product_name2);
        final TextView product_desc=findViewById(R.id.tv_product_description);
        final TextView product_unit=findViewById(R.id.tv_product_unit);
        final TextView product_price=findViewById(R.id.tv_product_price2);
        final TextView average_rate=findViewById(R.id.average_rate);
        final TextView raters_count_tv=findViewById(R.id.raters_count);
        final RatingBar ratingBar=findViewById(R.id.ratingBar);
        ImageButton close=findViewById(R.id.close_btn);
       Button add_to_cart=findViewById(R.id.addtocart_btn);
        final ElegantNumberButton numberButton=findViewById(R.id.number_button2);
        final TextView btShowmore=findViewById(R.id.show_more_btn);
        Intent i=getIntent();
        String pid=i.getStringExtra("productID");
        btShowmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btShowmore.getText().toString().equalsIgnoreCase("show more..."))
                {
                    product_desc.setMaxLines(Integer.MAX_VALUE);//your TextView
                    btShowmore.setText("Show less");
                }
                else
                {
                    product_desc.setMaxLines(5);//your TextView
                    btShowmore.setText("Show more...");
                }
            }
        });
        final RatingReviews ratingReviews =findViewById(R.id.rating_reviews);

        final Pair colors[] = new Pair[]{
                new Pair<>(Color.parseColor("#0c96c7"), Color.parseColor("#00fe77")),
                new Pair<>(Color.parseColor("#7b0ab4"), Color.parseColor("#ff069c")),
                new Pair<>(Color.parseColor("#fe6522"), Color.parseColor("#fdd116")),
                new Pair<>(Color.parseColor("#104bff"), Color.parseColor("#67cef6")),
                new Pair<>(Color.parseColor("#ff5d9b"), Color.parseColor("#ffaa69"))
        };


        final DatabaseReference review_reference=FirebaseDatabase.getInstance().getReference("products").child(pid).child("reviews");
        review_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float raters_count=dataSnapshot.getChildrenCount();
                float total_rate_count=0;
                int fivestar=0;
                int fourstar=0;
                int threestar=0;
                int twostar=0;
                int onestar=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    review_model reviewModel=snapshot.getValue(review_model.class);
                    total_rate_count+=reviewModel.getRating();
                    if(reviewModel.getRating()>4 && reviewModel.getRating()<=5)
                    {
                        fivestar++;
                    }
                    else if(reviewModel.getRating()>3 && reviewModel.getRating()<=4)
                    {
                        fourstar++;
                    }
                    else if(reviewModel.getRating()>2 && reviewModel.getRating()<=3)
                    {
                        threestar++;
                    }
                    else if(reviewModel.getRating()>1 && reviewModel.getRating()<=2)
                    {
                        twostar++;
                    }
                    else {
                        onestar++;
                    }
                }



                int raters[] = new int[]{
                        fivestar,
                        fourstar,
                        threestar,
                        twostar,
                       onestar
                };

                float average=total_rate_count/raters_count;
                average /= Math.pow(10, (int) Math.log10(average));
                average = ((int) (average * 10)) / 10.0f;
                average_rate.setText(String.valueOf(average));
                ratingBar.setRating(average);
                raters_count_tv.setText(String.valueOf((int)raters_count));
                ratingReviews.createRatingBars((int) raters_count,BarLabels.STYPE1,colors,raters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("products").child(pid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                     productData = dataSnapshot.getValue(product_data.class);
                    Glide.with(product_view.this).load(productData.getProductpicurl()).into(product_image);
                    product_name.setText(productData.getProductName());
                    product_desc.setText(productData.getProductDesc());
                    product_unit.setText(productData.getProductUnit());
                    product_price.setText("Rs."+productData.getProductPrice());
               // }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

            }
        });
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference_cart= FirebaseDatabase.getInstance().getReference("cart");
                databaseReference_cart.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(productData.getProductID()))
                        {
                            Toast.makeText(product_view.this, "Already Added", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            int a = Integer.parseInt(numberButton.getNumber());
                            int b = Integer.parseInt(productData.getProductPrice());
                            int c = a * b;
                            cart_data cartData=new cart_data(productData.getProductID(),productData.getProductName(),productData.getProductPrice(),
                                    productData.getProductpicurl(), numberButton.getNumber(),String.valueOf(c),productData.getProductUnit(),false);
                            databaseReference_cart.child(FirebaseAuth.getInstance().getUid()).child(productData.getProductID()).setValue(cartData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(product_view.this, "Added to Cart", Toast.LENGTH_SHORT).show();
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
    }
}
