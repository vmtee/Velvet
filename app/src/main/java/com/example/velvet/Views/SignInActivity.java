package com.example.velvet.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.velvet.R;
import com.example.velvet.Views.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    SignInButton signInBtn ;
    GoogleSignInClient signInClient;
    protected int request_code = 10;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInBtn = findViewById(R.id.sign_in_btn);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = signInClient.getSignInIntent();
                /**deprecated procedure replace with custom method**/
                startActivityForResult(intent,request_code);
            }
        });
        firebaseAuth = firebaseAuth.getInstance();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == request_code){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                String success ="Sign : Successful";
                proveSuccess(success);
            }
            try {
                GoogleSignInAccount account = signInAccountTask.getResult(ApiException.class);

                if(account!=null){
                   AuthCredential authCredential = GoogleAuthProvider
                           .getCredential(account.getIdToken(),null);

                    firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUI(user);
                            }else{
                                Log.w("email_failure","",task.getException());
                               // throw new RuntimeException("Email Authentication Failure");
                                emailFailure();
                            }
                        }
                    });
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    private void proveSuccess(String success) {
        Toast.makeText(getApplicationContext(),success,Toast.LENGTH_SHORT).show();
    }

    private void emailFailure(){
        Toast.makeText(SignInActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate too project page
     * @param account Firebase User
     * **/
    public void updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"Sign In: Successful",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }else {
            Toast.makeText(this,"Sign in: Failure",Toast.LENGTH_LONG).show();
        }
    }
}