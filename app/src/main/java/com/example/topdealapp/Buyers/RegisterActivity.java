package com.example.topdealapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.topdealapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);

        loadingBar = new ProgressDialog(this);

        // create user and connect to firebase
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

//    private void CreateAccount() {
//        String name = InputName.getText().toString();
//        String phone = InputPhoneNumber.getText().toString();
//        String password = InputPassword.getText().toString();
//        RequestQueue volleyQueue = Volley.newRequestQueue(RegisterActivity.this);
//
//                String url = "http://192.168.1.21:3000/create/?name=" + name + "&phone=" + phone + "&password=" + password;
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                        response -> {
//                            if (response.equals("Done!")) {
//                                Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
//
//                            } else
//                                Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
//                        }, error -> {
//                    Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                });
//                volleyQueue.add(stringRequest);
//    }



    // function to create an account
//    private void CreateAccount(){
//        String name = InputName.getText().toString();
//        String phone = InputPhoneNumber.getText().toString();
//        String password = InputPassword.getText().toString();
//
//        if(TextUtils.isEmpty(name)){
//            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(phone)){
//            Toast.makeText(this, "Please write your phone...", Toast.LENGTH_SHORT).show();
//        }
//        else if(TextUtils.isEmpty(password)){
//            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            loadingBar.setTitle("Create Account");
//            loadingBar.setMessage("Please wait, while we are checking the credentials");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
//
//            ValidatePhoneNumber(name, phone, password);
//        }
//    }

    private void CreateAccount(){

        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(phone).exists()){
                    Toast.makeText(RegisterActivity.this, "This "+ phone + " already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    RequestQueue volleyQueue = Volley.newRequestQueue(RegisterActivity.this);

                    String url = "http://192.168.1.21:3000/create/?name=" + name + "&phone=" + phone + "&password=" + password;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            response -> {
                                if (response.equals("Done!")) {
                                    Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                } else
                                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                            }, error -> {
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    });
                    volleyQueue.add(stringRequest);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    // function to check the validate of ohine number
    private void ValidatePhoneNumber(String name, String phone, String password){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists())){
                    HashMap<String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(RegisterActivity.this, "This "+ phone + " already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}