package com.example.iot_farola.presentacion;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {
    private Button registrar,borrar,admin,cerrarses;
    private TextView nombre;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        registrar = findViewById(R.id.Registrar);
        borrar = findViewById(R.id.Borrar);
        admin = findViewById(R.id.AdminUsuario);
        nombre = findViewById(R.id.textView14);
        cerrarses = findViewById(R.id.cerrarses);
        nombre.setText(usuario.getDisplayName());

        cerrarses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v);
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarRegistrar(v);
            }
        });
    }
    public void lanzarRegistrar(View v){
        Intent i = new Intent(this, RegistrarFarolaActivity.class);
        startActivity(i);
    }
    public void cerrarSesion(View view) {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish(); // Finaliza la actividad actual
                        } else {
                            // Manejar cualquier error en el cierre de sesión
                            Toast.makeText(getApplicationContext(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
