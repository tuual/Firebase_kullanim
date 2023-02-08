package com.example.firebase_kullanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private Button btnKayit;
    private EditText etEmail,etSifre,etKadi;
    private String txtEmail,txtSifre,txtisim;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView tvgirisyap;
    private DatabaseReference mReference;
    private HashMap<String,Object> mData;
    private FirebaseFirestore mFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tanimla();
        btnKayit.setOnClickListener(view -> {
            kayitOl();
        });

        tvgirisyap.setOnClickListener(view ->{
            gecisyap();
        });
    }
    private  void tanimla(){
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        // Firebase Instance

        tvgirisyap = (TextView) findViewById(R.id.tvGirisyap);
        btnKayit = (Button) findViewById(R.id.btnKayit);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etSifre = (EditText) findViewById(R.id.etSifre);
        etKadi = (EditText) findViewById(R.id.etKadi);
    }
    private void gecisyap(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void kayitOl(){
        txtisim = etKadi.getText().toString().trim();
        txtEmail = etEmail.getText().toString().trim();
        txtSifre = etSifre.getText().toString().trim();

        if (!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtSifre) && !TextUtils.isEmpty(txtisim)){

            mAuth.createUserWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mData = new HashMap<>();
                                mUser = mAuth.getCurrentUser();
                                mData.put("kullaniciAdi",txtisim);
                                mData.put("kullaniciEmail",txtEmail);
                                mData.put("kullaniciSifre",txtSifre);
                                mData.put("kullaniciUid",mUser.getUid());

                                mFirestore.collection("Kullanicilar").document(mUser.getUid())
                                        .set(mData)
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                             if (task.isSuccessful()){
                                                 Toast.makeText(MainActivity.this,"Kayit Islemi Basarili",Toast.LENGTH_SHORT).show();

                                             }
                                             else
                                                 Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                // Realtime Database
                                /*
                                mReference.child("Kullanicilar").child(mUser.getUid())
                                        .setValue(mData)
                                        .addOnCompleteListener(MainActivity.this,task1 -> {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(MainActivity.this,"Kayit Islemi Basarili",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        });*/


                            }
                            else
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(this,"Email ve Şifre Boş Olamaz",Toast.LENGTH_SHORT).show();

    }



}