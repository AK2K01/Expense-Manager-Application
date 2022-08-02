package com.example.expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class RegistrationActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPass;
    private EditText mConfirmPass;
    private Button btnReg;
    private TextView mSignIn;

    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        registration();
    }
    private void registration()
    {
        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignIn = findViewById(R.id.signIn_here);
        mConfirmPass = findViewById(R.id.confirmPass_reg);

        btnReg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                String confirmPass = mConfirmPass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("E-Mail Required....");
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    mPass.setError("Password Required....");
                    return;
                }
                if(TextUtils.isEmpty(confirmPass))
                {
                    mConfirmPass.setError("Please Confirm your Password!!");
                    return;
                }
                if(!(pass.equals(confirmPass)))
                {
                    mConfirmPass.setError("Please make sure both Passwords Match!!");
                    return;
                }
                mDialog.setMessage("Processing.....");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Completed!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}