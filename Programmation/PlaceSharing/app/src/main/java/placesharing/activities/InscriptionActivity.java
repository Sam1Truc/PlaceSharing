package placesharing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import placesharing.R;

public class InscriptionActivity extends AppCompatActivity {
    private static final String TAG = "INSCRIPTION";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText email;
    private EditText password1;
    private Button btnInscription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        email = (EditText) findViewById(R.id.editTextEmail);
        password1 = (EditText) findViewById(R.id.editTextMotDePasse);
        btnInscription = (Button) findViewById(R.id.buttonInscription);

        btnInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(email.getText().toString(),password1.getText().toString());
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification();
                    Toast.makeText(getApplication(),getString(R.string.email_verification_sent),Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void createAccount(String email, String password){

        if(email.isEmpty() || password.isEmpty()){
            this.email.setError(getString(R.string.champs_vide));
            password1.setError(getString(R.string.champs_vide));
            return;
        }
        else if(!email.contains("@") || !email.contains(".")){
            this.email.setError(getString(R.string.correct_email));
            return;
        }
        else if(password.length() < 6){
            password1.setError(getString(R.string.minimum_password));
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_sign_up) + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("email",email.getText().toString());
        outState.putString("password1",password1.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            email.setText(savedInstanceState.getString("email"));
            password1.setText(savedInstanceState.getString("password1"));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }
}
