package com.example.iot_farola.presentacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.iot_farola.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Registro extends AppActivity {
    private boolean passwordVisible = false;
    private FirebaseAuth auth;
    private EditText etCorreo;
    private EditText etContraseña;
    private EditText confContraseña;
    private EditText nombre;
    private CheckBox acepto;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication
        etCorreo = findViewById(R.id.editTextTextEmailAddress);
        etContraseña = findViewById(R.id.editTextTextPassword);
        confContraseña = findViewById(R.id.editTextTextPassword2);
        nombre = findViewById(R.id.editTextText);
        acepto = findViewById(R.id.checkBox2);
        confContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftDrawableWidth = confContraseña.getCompoundDrawables()[0].getBounds().width();
                    if (event.getRawX() <= (confContraseña.getLeft() + leftDrawableWidth)) {
                        togglePasswordVisibility1();
                        return true;
                    }
                }
                return false;
            }
        });
        etContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftDrawableWidth = etContraseña.getCompoundDrawables()[0].getBounds().width();
                    if (event.getRawX() <= (etContraseña.getLeft() + leftDrawableWidth)) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });
        Button registro = findViewById(R.id.Registrarse);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registroCorreo(v);
            }
        });
    }
    private void mensaje(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Aviso")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void verificaSiUsuarioValidado() {
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, AppActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
    private boolean verificaCampos() {
        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();
        String confcontraseña = confContraseña.getText().toString();

        if (correo.isEmpty()) {
            etCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            etCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            etContraseña.setError("Introduce una contraseña");
        }else if (!contraseña.equals(confcontraseña)){
            etContraseña.setError("Los dos campos deben coincidir");
            confContraseña.setError("Los dos campos deben coincidir");
        }else if(!acepto.isChecked()){
            acepto.setError("Debes aceptar los términos y condiciones");
        } else if (contraseña.length() < 6) {
            etContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            etContraseña.setError("Ha de contener un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            etContraseña.setError("Ha de contener una letra mayúscula");
        } else {
            return true;
        }
        return false;
    }
    public void registroCorreo(View view) {
        if (verificaCampos()) {
            // Mostrar un cuadro de diálogo de carga
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registrando usuario...");
            progressDialog.show();

            String correo = etCorreo.getText().toString();
            String contraseña = etContraseña.getText().toString();
            String nombreDeUsuario = nombre.getText().toString();

            auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss(); // Ocultar el cuadro de diálogo de carga

                            if (task.isSuccessful()) {
                                // Registro exitoso, realizar acciones adicionales
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nombreDeUsuario) // Reemplaza "nombreDeUsuario" con el valor real
                                            .build();
                                    // Envía un correo de verificación
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> emailTask) {
                                                    if (emailTask.isSuccessful()) {
                                                        // Envío de correo de verificación exitoso
                                                        // Puedes mostrar un mensaje al usuario
                                                        Intent i = new Intent(Registro.this, LoginCorreo.class);
                                                        startActivity(i);
                                                        mensaje("Se ha enviado un correo de verificación a su dirección de correo.");
                                                    } else {
                                                        // Fallo en el envío del correo de verificación
                                                        mensaje("No se pudo enviar el correo de verificación. Intente nuevamente más tarde.");
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // Fallo en el registro, mostrar un mensaje de error al usuario
                                String errorMessage = task.getException().getLocalizedMessage();
                                mensaje(errorMessage);
                            }
                        }
                    });
        }
    }
    private void togglePasswordVisibility() {
        if (passwordVisible) {
            etContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            etContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        passwordVisible = !passwordVisible;
    }
    private void togglePasswordVisibility1() {
        if (passwordVisible) {
            confContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            confContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        passwordVisible = !passwordVisible;
    }

}
