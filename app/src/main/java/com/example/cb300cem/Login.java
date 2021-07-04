package com.example.cb300cem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private GoogleSignInClient client;
    private FirebaseAuth auth;
    private static int signInRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //region Google Sign In
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        client = GoogleSignIn.getClient(this, signInOptions);

        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        auth = FirebaseAuth.getInstance();
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // google sign in
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent, signInRequestCode);
            }
        });
        //endregion

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if request code is from sign in
        if(requestCode == signInRequestCode){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // successful google login
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);

                fbGoogleAuth(account); // firebase auth

            } catch (Exception e) {
                // google log in failed


            }
        }
    }

    private void fbGoogleAuth(GoogleSignInAccount account){
        AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(cred)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Logged in
                        FirebaseUser user = auth.getCurrentUser();
                        String userId = user.getUid();
                        String email = user.getEmail();

                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            // account created for new user
                            Toast.makeText(Login.this,"Account created", Toast.LENGTH_SHORT).show();
                        }else{
                            // account already exists -> logged in
                            Toast.makeText(Login.this,"Logged In", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Login.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();

                        // login finished
                        startActivity(new Intent(Login.this, Main.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // login failed
                    }
                });
    }
}