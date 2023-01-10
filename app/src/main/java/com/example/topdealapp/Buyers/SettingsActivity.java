package com.example.topdealapp.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.example.topdealapp.Prevalent.Prevalent;
import com.example.topdealapp.R;
import com.example.topdealapp.Sellers.SellerAddNewProductActivity;
import com.example.topdealapp.Sellers.SellerHomeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;
    private Button securityQuestionBtn;

    private Uri imageUri;
    private String myUrl = "";
    //private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private String downloadImageUrl;


    @Override
    protected void onStart() {
        super.onStart();
        DisplayInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);
        securityQuestionBtn = (Button) findViewById(R.id.security_questions_btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

//        profileImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checker = "clicked";
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 33);
//            }
//        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                startActivity(intent);
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                // we need to add cropImage

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);



//                CropImage.activity(imageUri)
//                        .setAspectRatio(1,1)
//                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Info update successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 33 && resultCode == RESULT_OK && data != null){

//            Uri ProfileUri = data.getData();
//            profileImageView.setImageURI(ProfileUri);
//            Crop.ActivityResult result = CropImage.ActivityResult.getActivityResult(data);
//            imageUri = result.getUri();

            imageUri = data.getData();


            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Phone is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }

    }


    // function to upload image
    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageProfilePrictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            final UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            downloadImageUrl = fileRef.getDownloadUrl().toString();
                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                downloadImageUrl = task.getResult().toString();
                                Toast.makeText(SettingsActivity.this, "got the Profile image Url Successfully...", Toast.LENGTH_SHORT).show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name",fullNameEditText.getText().toString());
                                userMap.put("address",addressEditText.getText().toString());
                                userMap.put("phoneOrder",userPhoneEditText.getText().toString());
                                userMap.put("image",downloadImageUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "image is added successfully...", Toast.LENGTH_SHORT).show();

                                        }
                                        else{
                                            progressDialog.dismiss();
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            });



//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if(!task.isSuccessful()){
//                        throw task.getException();
//                    }
//                    return fileRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri> () {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if(task.isSuccessful()){
//                        Uri downloadUrl = task.getResult();
//                        myUrl = downloadUrl.toString();
//
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
//                        HashMap<String, Object> userMap = new HashMap<>();
//                        userMap.put("name",fullNameEditText.getText().toString());
//                        userMap.put("address",addressEditText.getText().toString());
//                        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
//                        userMap.put("image",myUrl);
//                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
//
//                        progressDialog.dismiss();
//
//                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
//                        Toast.makeText(SettingsActivity.this, "Profile Info update successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                    else {
//                        progressDialog.dismiss();
//                        Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
        else{
            Toast.makeText(this, "image is not selected", Toast.LENGTH_SHORT).show();
        }
    }


    // display the information
    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText) {

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
         UserRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     if(snapshot.child("image").exists()){
                         String image = snapshot.child("image").getValue().toString();
                         String name = snapshot.child("name").getValue().toString();
                         String phone = snapshot.child("phone").getValue().toString();
                         String address = snapshot.child("address").getValue().toString();
                         Picasso.get().load(image).into(profileImageView);
                         fullNameEditText.setText(name);
                         userPhoneEditText.setText(phone);
                         addressEditText.setText(address);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }

    private void DisplayInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        ref.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.getValue().toString();
                    fullNameEditText.setText(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String address = snapshot.getValue().toString();
                    addressEditText.setText(address);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phone = snapshot.getValue().toString();
                    userPhoneEditText.setText(phone);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}