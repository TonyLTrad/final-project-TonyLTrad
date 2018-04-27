package com.trad.anthony.bucketlist;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginSignUpActivity extends AppCompatActivity {

    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference databaseReference;

    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        if(firebaseAuth.getCurrentUser() != null){
            loggedIn();
        }

        emailEditText = (EditText) findViewById(R.id.email_EditText);
        passwordEditText = (EditText) findViewById(R.id.password_EditText);
    }

    private  void loggedIn(){
        startActivity(new Intent(LoginSignUpActivity.this, BucketListActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }
        return valid;
    }

    public void signUp(View view){

        if (!validateForm()) {
            return;
        }

        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loggedIn();
                        } else {
                            Toast.makeText(LoginSignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(View view){

        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        if (!validateForm()) {
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginSignUpActivity.this, "Success: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            loggedIn();
                        } else {
                            Toast.makeText(LoginSignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}





