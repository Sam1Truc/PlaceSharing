package placesharing.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ConnexionActivity extends AppCompatActivity {

    private static final String TAG = "CONNEXION";
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText email, password;
    private Button btnConnexion, btnInscription, btnResetPassword;
    private boolean leave=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }



        email = (EditText) findViewById(R.id.editTextId);
        password = (EditText) findViewById(R.id.editTextMotDePasse);

        btnConnexion = (Button) findViewById(R.id.buttonConnexion);
        btnInscription = (Button) findViewById(R.id.buttonInscription);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString().trim();
                final String strPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_password), Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                        .addOnCompleteListener(ConnexionActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        password.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.auth_echec), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });



        btnInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInscription = new Intent(getApplicationContext(),InscriptionActivity.class);
                startActivity(intentInscription);
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResetPassword = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                startActivity(intentResetPassword);
            }
        });



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(user.isEmailVerified() || user.getEmail().equals("root@root.com")){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplication(),getString(R.string.no_verfied_email),Toast.LENGTH_SHORT).show();
                    }
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("email",email.getText().toString());
        outState.putString("password",password.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            email.setText(savedInstanceState.getString("email"));
            password.setText(savedInstanceState.getString("password"));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(leave){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        else{
            leave = true;
            Toast.makeText(getApplicationContext(), getString(R.string.tap_one_more_to_leave), Toast.LENGTH_SHORT).show();
        }
        return;
    }
}
