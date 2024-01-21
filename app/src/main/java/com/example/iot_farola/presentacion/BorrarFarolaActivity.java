package com.example.iot_farola.presentacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class BorrarFarolaActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button btnBorrarFarola;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrarfarola);
        db = FirebaseFirestore.getInstance();
        editText = findViewById(R.id.editTextText2);
        btnBorrarFarola = findViewById(R.id.borrarFarola);

        btnBorrarFarola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarYBorrarFarola();
            }
        });
    }
    private void buscarYMostrarFarola() {
        // Obtener el nombre de la farola desde el EditText
        String nombreFarola = editText.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("farolas")
                .document(nombreFarola)  // Buscar directamente por el nombre del documento
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener datos y cargar en los EditText correspondientes
                        confirmarYBorrarFarola();
                    } else {
                        // Manejar el caso cuando el documento no existe

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    // Manejar errores de consulta
                });
    }
    private void confirmarYBorrarFarola() {
        // Obtener el nombre de la farola desde el EditText
        String nombreFarola = editText.getText().toString();

        // Crear un cuadro de diálogo para confirmar el borrado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar borrado");
        builder.setMessage("¿Estás seguro de que deseas borrar la farola '" + nombreFarola + "'?");

        // Agregar botones de confirmación y cancelación
        builder.setPositiveButton("Sí", (dialog, which) -> {
            // Borrar la farola si el usuario confirma
            borrarFarola(nombreFarola);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // No hacer nada si el usuario cancela
            dialog.dismiss();
        });

        // Mostrar el cuadro de diálogo
        builder.create().show();
    }

    private void borrarFarola(String nombreFarola) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("farolas")
                .document(nombreFarola)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Borrado exitoso
                    Toast.makeText(this, "Farola eliminada correctamente", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores durante el borrado
                    Toast.makeText(this, "Error al borrar la farola: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



}
