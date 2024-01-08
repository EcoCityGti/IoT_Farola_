package com.example.iot_farola.presentacion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.iot_farola.R;
import com.example.iot_farola.datos.Usuarios;
import com.example.iot_farola.modelo.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 123;
//---------------------------onCreate()--------------------------------------------
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
        imageView = findViewById(R.id.imagen2);
        ImageButton btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);
        ImageButton btnSignInWithEmail = findViewById(R.id.btnSignInWithEmail);
        ImageButton btnSignInWithAnonim = findViewById(R.id.btnSignInWithAnonim);
        ImageButton btnSignInWithTwitter = findViewById(R.id.btnSignInWithTwitter);
        Button registrar = findViewById(R.id.button3);
//-----------------------------Botones()--------------------------------------------
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,Registro.class);
                startActivity(i);
            }
        });
//-----------------------------Boton Anonimo()--------------------------------------------
        btnSignInWithAnonim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la autenticación anónima
                auth = FirebaseAuth.getInstance();
                auth.signInAnonymously()
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Autenticación anónima exitosa
                                    FirebaseUser user = auth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, R.string.anonimtrue, Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), AppActivity.class);
                                    startActivity(i);

                                    // Puedes redirigir al usuario a la siguiente actividad aquí si lo deseas
                                } else {
                                    // Error en la autenticación anónima
                                    Toast.makeText(LoginActivity.this, R.string.anonimfalse, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });//Boton Anonimo()
//-----------------------------Boton Twitter()--------------------------------------------
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
        });//Boton Twitter()
//-----------------------------Boton Google()--------------------------------------------
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
        });//Boton Google()
//-----------------------------Boton Email()--------------------------------------------
        btnSignInWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginCorreo.class);
                startActivity(i);
                /*List<AuthUI.IdpConfig> providers = Arrays.asList(
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
                );*/
            }
        });//BotonEmail()

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

    }//onCreate()
    private void login() {

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            Usuarios.guardarUsuario(usuario);
            Toast.makeText(this, getString(R.string.iniciarses) + usuario.getDisplayName(), Toast.LENGTH_LONG).show();
            checkUserRoleAndStartActivity();
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
                if (response == null) s = String.valueOf(R.string.cancelado);
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK: s= String.valueOf(R.string.nointernet); break;
                    case ErrorCodes.PROVIDER_ERROR: s= R.string.errorprov+ response.getError().getLocalizedMessage(); break;
                    case ErrorCodes.DEVELOPER_ERROR: s= String.valueOf(R.string.errordesarroll); break;
                    default: s= String.valueOf(R.string.otros_errores);
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void checkUserRoleAndStartActivity() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("usuarios").document(usuario.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtén el valor del campo "rol" del documento
                        String rol = document.getString("rol");

                        // Comprueba el valor del campo "rol" y inicia la actividad correspondiente
                        if ("tecnico".equals(rol)) {
                            // Inicia la actividad de administrador
                            Intent adminIntent = new Intent(this, TecnicoActivity.class);
                            startActivity(adminIntent);
                        }else if("admin".equals(rol)){
                            Intent adminIntent = new Intent(this, AdminActivity.class);
                            startActivity(adminIntent);
                        } else {
                            // Por defecto, inicia la actividad estándar
                            Intent defaultIntent = new Intent(this, AppActivity.class);
                            startActivity(defaultIntent);
                        }
                    } else {
                        // El documento no existe
                        // Aquí puedes manejar la situación de acuerdo a tus necesidades
                    }
                } else {
                    // Error al obtener el documento
                    // Aquí puedes manejar el error de acuerdo a tus necesidades
                }
            });
        } else {
            // Usuario no autenticado
            // Puedes manejar esta situación de acuerdo a tus necesidades
        }
    }

} //Para cerrar la clase