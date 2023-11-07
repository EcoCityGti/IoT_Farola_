package com.example.iot_farola.presentacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginCorreo extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etCorreo;
    private EditText etContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_correo);

        auth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication
        etCorreo = findViewById(R.id.corr);
        etContraseña = findViewById(R.id.password);
        Button inicio = findViewById(R.id.iniciar_sesion);
        TextView reestablecer = findViewById(R.id.Reestableecer);

        reestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Reestablecer.class);
                startActivity(i);
            }
        });
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesiónCorreo(v);
            }
        });

        TextView Registro = findViewById(R.id.Registrarte);
        Registro.setPaintFlags(Registro.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        reestablecer.setPaintFlags(reestablecer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.example.iot_farola.presentacion.Registro.class);
                startActivity(i);
            }
        });
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

        if (correo.isEmpty()) {
            etCorreo.setError(getString(R.string.loginerr1));
        } else if (!correo.matches(".+@.+[.].+")) {
            etCorreo.setError(getString(R.string.loginerr2));
        } else if (contraseña.isEmpty()) {
            etContraseña.setError(getString(R.string.loginerr3));
        } else if (contraseña.length() < 6) {
            etContraseña.setError(getString(R.string.loginerr4));
        } else if (!contraseña.matches(".*[0-9].*")) {
            etContraseña.setError(getString(R.string.loginerr5));
        } else if (!contraseña.matches(".*[A-Z].*")) {
            etContraseña.setError(getString(R.string.loginerr6));
        } else {
            return true;
        }
        return false;
    }
    private void mensaje(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Aviso")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void inicioSesiónCorreo(View view) {
        if (verificaCampos()) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();

            String correo = etCorreo.getText().toString();
            String contraseña = etContraseña.getText().toString();

            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Successfully logged in, perform further actions
                                verificaSiUsuarioValidado();
                            } else {
                                // Failed to log in, show an error message
                                String errorMessage = task.getException().getLocalizedMessage();
                                mensaje(errorMessage);
                            }
                        }
                    });
        }
    }
}
