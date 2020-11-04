package com.max.hydro_flow;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    LinearLayout bnavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            finish();
        }
        bnavigation=findViewById(R.id.top_navigation_Linear);
        final ImageButton home,cart,orders,account;
        home=findViewById(R.id.home_bn_btn);
        cart=findViewById(R.id.cart_bn_btn);
        orders=findViewById(R.id.order_bn_btn);
        account=findViewById(R.id.account_bn_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.setBackground(getResources().getDrawable(R.drawable.green_border_bg,null));
        cart.setBackgroundColor(getResources().getColor(R.color.transparent,null));
        orders.setBackgroundColor(getResources().getColor(R.color.transparent,null));
        account.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();

            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.setBackground(getResources().getDrawable(R.drawable.blue_border_bg,null));
                home.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                orders.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                account.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new cart()).commit();

            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.setBackground(getResources().getDrawable(R.drawable.red_border_bg,null));
                cart.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                home.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                account.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new myorders()).commit();

            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account.setBackground(getResources().getDrawable(R.drawable.yellow_border_bg,null));
                cart.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                orders.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                home.setBackgroundColor(getResources().getColor(R.color.transparent,null));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new my_account()).commit();

            }
        });





        home.setBackground(getResources().getDrawable(R.drawable.green_border_bg,null));
        cart.setBackgroundColor(getResources().getColor(R.color.transparent,null));
        orders.setBackgroundColor(getResources().getColor(R.color.transparent,null));
        account.setBackgroundColor(getResources().getColor(R.color.transparent,null));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();
        String id = FirebaseAuth.getInstance().getUid();
        Query reference = FirebaseDatabase.getInstance().getReference("customers").orderByChild("customerID").equalTo(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_data user_data = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user_data = snapshot.getValue(user_data.class);
                }
                if (!dataSnapshot.exists()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert currentUser != null;
                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), login.class));
                                finish();
                            }
                        }
                    });
                }
                else
                {
                    assert user_data != null;
                    if (user_data.getName().equals("not_set") || user_data.getAreaName().equals("not_set") || user_data.getGstinNo().equals("not_set") || user_data.getPancardNo().equals("not_set") || user_data.getPicurl().equals("not_set")) {
                        Intent intent = new Intent(getApplicationContext(), add_customer.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
