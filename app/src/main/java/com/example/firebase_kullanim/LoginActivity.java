package com.example.firebase_kullanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginSifre,etLoginGuncelle;
    private TextView tvKayitol;
    private Button btnGirisyap,btnGuncelle,btnSil;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String txtEmail,txtSifre,txtGuncelle;
    private DatabaseReference mReference;
    private HashMap<String,Object> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tanimla();
        buttonclickevents();

    }

    private void tanimla(){
        tvKayitol = (TextView) findViewById(R.id.tvKayitol);
        btnGirisyap = (Button) findViewById(R.id.btnGiris);
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginSifre = (EditText) findViewById(R.id.etLoginSifre);
        etLoginGuncelle = (EditText) findViewById(R.id.etGuncelleMail);
        btnGuncelle = (Button) findViewById(R.id.btnGuncelle);
        btnSil = (Button) findViewById(R.id.btnSil);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


    }

    private void gecisyap2(){
        Intent view = new Intent(this,MainActivity.class);
        startActivity(view);
        finish();
    }

    private void girisyap(){
        txtEmail = etLoginEmail.getText().toString().trim();
        txtSifre = etLoginSifre.getText().toString().trim();
        
        if (!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtSifre)) {
            mAuth.signInWithEmailAndPassword(txtEmail,txtSifre)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mUser = mAuth.getCurrentUser();

                            assert mUser != null;
                            verileriGetir(mUser.getUid());


                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else
            Toast.makeText(this, "Email ve Sifre Bos Olamaz", Toast.LENGTH_SHORT).show();
    }

    private void verileriGetir(String uid){

        mReference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(uid);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snp : snapshot.getChildren()){
                    System.out.println(snp.getKey() + "=" + snp.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

     private void veriyiGuncelle(HashMap<String,Object> hashMap, String uid){
        mReference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(uid);
        mReference.updateChildren(hashMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Veri Basariyla Guncellendi",Toast.LENGTH_SHORT).show();
                            System.out.println("--------- Guncellenen Veri-------------");
                            verileriGetir(uid);
                        }
                    }
                });
    }

    private void isimGuncelle(){
        txtGuncelle = etLoginGuncelle.getText().toString().trim();

        if (!TextUtils.isEmpty(txtGuncelle)){
            mData = new HashMap<>();
            mData.put("kullaniciAdi",txtGuncelle);



            assert mUser != null;
            veriyiGuncelle(mData,mUser.getUid());
        }
        else
            Toast.makeText(this, "Guncellenecek Deger Bos Olamaz", Toast.LENGTH_SHORT).show();
    }

    private void datayisil(){
        mReference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mUser.getUid());
        mReference.removeValue()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            Toast.makeText(LoginActivity.this, "Data basariyla silindi", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void buttonclickevents(){

        tvKayitol.setOnClickListener(view ->{
            gecisyap2();
        });


        btnGirisyap.setOnClickListener(view -> {
            girisyap();
        });

        btnGuncelle.setOnClickListener(view ->{
            isimGuncelle();
        });

        btnSil.setOnClickListener(view -> {
            datayisil();
        });

    }
}