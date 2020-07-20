package com.tecnovajet.iposti.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static String TAG = "SignInActivity";
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parentLayout = findViewById(android.R.id.content);
        final TextInputLayout email = findViewById(R.id.email);
        final TextInputLayout password = findViewById(R.id.password);
        Button signin = findViewById(R.id.signin);

        mAuth = FirebaseAuth.getInstance();

        password.setPasswordVisibilityToggleEnabled(true);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    hideSoftKeyboard();
                    boolean ok = true;

                    String emailT = email.getEditText().getText().toString();
                    String passwordT = password.getEditText().getText().toString();

                    if (!isValidEmailAddress(emailT)) {
                        email.setError("Formato email errato");
                        ok = false;
                    }else
                        email.setErrorEnabled(false);

                    if (passwordT.length() < 8) {
                        password.setError("Almeno 8 caratteri");
                        ok = false;
                    }else
                        password.setErrorEnabled(false);

                    if(ok){
                        mAuth.signInWithEmailAndPassword(emailT, passwordT)
                                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                                            if (task.getException().getClass() == FirebaseNetworkException.class){
                                                Snackbar.make(parentLayout, "Nessuna connessione ad internet", Snackbar.LENGTH_LONG)
                                                        .show();
                                            }else if (task.getException().getClass() == FirebaseAuthInvalidCredentialsException.class){
                                                password.setError("Password errata");
                                            }else if (task.getException().getClass() == FirebaseAuthInvalidUserException.class) {
                                                email.setError("Email non valida");
                                            }
                                        }

                                        // ...
                                    }
                                });

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
