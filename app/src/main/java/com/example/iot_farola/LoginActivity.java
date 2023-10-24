package com.example.iot_farola;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private FirebaseAuth auth;
    private ProgressDialog dialog;

    private static final int RC_SIGN_IN = 123;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
        imageView = findViewById(R.id.imagen2);
        Button btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);
        Button btnSignInWithEmail = findViewById(R.id.btnSignInWithEmail);
        Button btnSignInWithFacebook = findViewById(R.id.btnSignInWithFacebook);
        Button btnSignInWithAnonim = findViewById(R.id.btnSignInWithAnonim);
        Button btnSignInWithTwitter = findViewById(R.id.btnSignInWithTwitter);
        Button btnSignInWithTelf = findViewById(R.id.btnSignInWithPhone);

        btnSignInWithTelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN
                );
            }
        });
        btnSignInWithTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.TwitterBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN
                );
            }
        });

        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN
                );
            }
        });
        btnSignInWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                //.setTheme(R.style.CustomFirebaseUI) // Establece el tema personalizado
                                .build(),
                        RC_SIGN_IN
                );
            }
        });
        btnSignInWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.FacebookBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN
                );
            }
        });
        // Crear un ObjectAnimator para la propiedad de traslación en el eje Y (mueve hacia arriba y luego hacia abajo)
        ObjectAnimator translationY = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -50f, 0f); // Empieza en 0, sube 50px y vuelve a 0
        translationY.setDuration(2000);

        // Crear un AnimatorSet para agrupar la animación original y configurar la repetición
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translationY); // Agregar la animación original

        // Configurar la repetición infinita
        animatorSet.setStartDelay(0);
        animatorSet.setDuration(2000); // Duración de la repetición
        animatorSet.setInterpolator(null); // Sin interpolación
        animatorSet.setTarget(imageView);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reiniciar la animación al finalizar
                animatorSet.start();
            }
        });

        // Iniciar la animación
        animatorSet.start();

    }
    private void login() {

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            Toast.makeText(this, "Inicia sesión: " + usuario.getDisplayName(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, AppActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // Mostrar la pantalla de inicio de sesión personalizada en activity_login.xml
            setContentView(R.layout.activity_login);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                login();
            } else {
                String s = "";
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) s = "Cancelado";
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK: s="Sin conexión a Internet"; break;
                    case ErrorCodes.PROVIDER_ERROR: s="Error en proveedor"; break;
                    case ErrorCodes.DEVELOPER_ERROR: s="Error desarrollador"; break;
                    default: s="Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }
} //Para cerrar la clase