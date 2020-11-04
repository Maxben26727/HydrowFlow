package com.max.hydro_flow;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class add_customer extends AppCompatActivity {
    final int IMAGE_REQUEST_CODE = 999;
    EditText et_name, et_address, et_panno, et_gstno;
    String sname, saddress , spanno, sgstno, imgdata, sarea;
    
    Button add_cust, update_cust;
    ProgressDialog progressDialog;
    private CircleImageView civ_profile;
    private ImageButton profile_select_btn;
    Bitmap bitmap=null;
    private Uri filepath=null;
    List<area_data> area_names = new ArrayList<>();
    private Spinner spinner;

    area_data areaData;

    private DatabaseReference databaseReference_area;
    private DatabaseReference databaseReference_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        et_name = findViewById(R.id.ed_cust_name);
        et_address = findViewById(R.id.ed_cust_address);
        et_panno = findViewById(R.id.ed_cust_panno);
        et_gstno = findViewById(R.id.ed_cust_gstno);
        profile_select_btn = findViewById(R.id.profile_select_btn);
        update_cust = findViewById(R.id.update_custdata_btn);
        civ_profile = findViewById(R.id.customer_profile_image);
        spinner = findViewById(R.id.spinner);


        databaseReference_area = FirebaseDatabase.getInstance().getReference("area");
        databaseReference_customer = FirebaseDatabase.getInstance().getReference("customers");

        databaseReference_area.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                area_names.clear();
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    areaData = itemSnapshot.getValue(area_data.class);
                    // categoryData.setKey(itemSnapshot.getKey());
                    area_names.add(areaData);

                }
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(0, "Select Area");
                for (int i = 0; i < area_names.size(); i++) {
                    arrayList.add(area_names.get(i).getAreaName());
                }
                progressDialog.dismiss();
                ArrayAdapter<String> aa = new ArrayAdapter<>(add_customer.this, R.layout.spinner_item_layout, arrayList);
                aa.setDropDownViewResource(R.layout.spinner_item_layout);
                spinner.setAdapter(aa);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            spinner.setSelected(false);
                        } else {
                            sarea = spinner.getSelectedItem().toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        progressDialog = new ProgressDialog(add_customer.this);



        update_cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = et_name.getText().toString();
                saddress = et_address.getText().toString();
                spanno = et_panno.getText().toString();
                sgstno = et_gstno.getText().toString();
                if(TextUtils.isEmpty(sname)||TextUtils.isEmpty(saddress)||TextUtils.isEmpty(sarea)||TextUtils.isEmpty(sgstno)||TextUtils.isEmpty(spanno))
                {
                    Toast.makeText(add_customer.this, "All fields Required", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else if(bitmap==null)
                {
                    Toast.makeText(add_customer.this, "Select Profile Pic", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progreedialog);
                    progressDialog.setCanceledOnTouchOutside(false);
                    Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                    getImageUrl();
                }
            }
        });


        profile_select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(add_customer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST_CODE);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(add_customer.this);


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filepath = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filepath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    civ_profile.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void update_customer_data() {

            String id = FirebaseAuth.getInstance().getUid();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", sname);
            hashMap.put("address", saddress);
            hashMap.put("areaName", sarea);
            hashMap.put("balance", "0");
            hashMap.put("gstinNo", sgstno);
            hashMap.put("pancardNo", spanno);
            hashMap.put("picurl", imgdata);

            databaseReference_customer.child(id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(add_customer.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void getImageUrl() {
        if(filepath!=null) {
            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference().child("customer_dp").child(filepath.getLastPathSegment());
            storageReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgdata = uri.toString();
                            update_customer_data();
                        }
                    });
                }
            });
        }
        else
        {
            Toast.makeText(this, "input All info", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

}

