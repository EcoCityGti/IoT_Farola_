package com.example.iot_farola.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegistrarFarolaActivity extends AppCompatActivity {

    private EditText etNombreFarola, etDireccionFarola, etAtributo1, etAtributo2;
    private Button btnRegistrarFarola,btnSubirImagen,btnBuscarFarola;
    private FirebaseFirestore db;
    private RepositorioFarolas farolas;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_farola);
        db = FirebaseFirestore.getInstance();

        // Initialize EditText and Button
        etNombreFarola = findViewById(R.id.etNombreFarola);
        etDireccionFarola = findViewById(R.id.etDireccionFarola);
        etAtributo1 = findViewById(R.id.etAtributo1);
        etAtributo2 = findViewById(R.id.etAtributo2);
        //etAtributo3 = findViewById(R.id.etAtributo3);
        farolas = ((Aplicacion) getApplication()).farolas;  // Asegúrate de inicializar farolas
        storageRef = FirebaseStorage.getInstance().getReference();

        btnRegistrarFarola = findViewById(R.id.btnRegistrarFarola);
        btnSubirImagen = findViewById(R.id.btnSubirFoto);
        btnBuscarFarola = findViewById(R.id.btnBuscarFarola);
        ImageView foto= findViewById(R.id.fotoFarola);

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descargarYMostrarImagen(v);
            }
        });

        btnBuscarFarola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    buscarYMostrarFarola();
                }catch (Exception e){
                    Log.d("Buscar_Farola",e.toString());
                }
            }
        });
        btnSubirImagen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                subirArchivo(v);
                    Toast.makeText(RegistrarFarolaActivity.this, "Foto subida correctamente", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(RegistrarFarolaActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        // Set click listener for the button
        btnRegistrarFarola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create a new Farola object with values from EditText fields
                    Farola nuevaFarola = crearFarolaDesdeEditTexts();
                    agregarFarolaAFirebase(nuevaFarola);
                    Toast.makeText(RegistrarFarolaActivity.this, "Nueva Farola: "+nuevaFarola.toString(), Toast.LENGTH_LONG).show();
                    // Now you can use 'nuevaFarola' as needed, for example, print its information
                    Log.d("Farola","Nueva Farola: " + nuevaFarola.toString());
                } catch (Exception e) {
                    Toast.makeText(RegistrarFarolaActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Helper method to create a Farola object from EditText values
    private Farola crearFarolaDesdeEditTexts() {
        String nombre = etNombreFarola.getText().toString();
        String direccion = etDireccionFarola.getText().toString();
        // Assuming the rest are Double and Integer values
        double atributo1 = Double.parseDouble(etAtributo1.getText().toString());
        double atributo2 = Double.parseDouble(etAtributo2.getText().toString());
        String atributo3 = "farolas/"+nombre+"/"+nombre;


        // Assuming default values for the other attributes, you can modify as needed
        Farola farola = new Farola(nombre, direccion, atributo1, atributo2, atributo3, 0.0, 0.0, 0.0, 0, 0.0);
        farolas.añade(farola);
        return farola;
    }
    private void agregarFarolaAFirebase(Farola farola) {
        String nombreDocumento = farola.getNombre();

        // Access the "farolas" collection in Firestore
        db.collection("farolas")
                .document(nombreDocumento)
                .set(farola)
                .addOnSuccessListener(aVoid -> {
                    // DocumentSnapshot added successfully
                    System.out.println("Farola agregada a Firebase correctamente.");
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    System.err.println("Error al agregar la farola a Firebase: " + e.getMessage());
                });
    }
    private void buscarYMostrarFarola() {
        // Obtener el nombre de la farola desde el EditText
        String nombreFarola = etNombreFarola.getText().toString();

        // Aquí debes implementar la lógica para buscar la farola en la base de datos
        // y cargar los datos encontrados en los otros EditText

        // Por ejemplo, puedes usar Firebase Firestore para realizar la consulta.
        // Aquí se muestra un ejemplo básico:

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("farolas")
                .whereEqualTo("nombre", nombreFarola)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Obtener datos y cargar en los EditText correspondientes
                        String direccion = document.getString("direccion");

                        // Verificar si el campo 'posicion' está presente
                        if (document.contains("posicion")) {
                            // Obtener el campo 'posicion' como un objeto Map
                            Map<String, Object> posicionMap = (Map<String, Object>) document.get("posicion");

                            // Verificar si los campos 'longitud' y 'latitud' están presentes
                            if (posicionMap != null && posicionMap.containsKey("longitud") && posicionMap.containsKey("latitud")) {
                                // Obtener datos y cargar en los EditText correspondientes
                                String atributo1 = posicionMap.get("longitud").toString();
                                String atributo2 = posicionMap.get("latitud").toString();

                                etAtributo1.setText(atributo1);
                                etAtributo2.setText(atributo2);
                            } else {
                                // Manejar el caso cuando los campos 'longitud' y/o 'latitud' no están presentes
                                // Puedes dejar los EditText en blanco o mostrar un mensaje
                                etAtributo1.setText("");
                                etAtributo2.setText("");
                                // También puedes mostrar un mensaje indicando que la información está incompleta
                                Toast.makeText(this, "La información de posición está incompleta", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Manejar el caso cuando el campo 'posicion' no está presente
                            // Puedes dejar los EditText en blanco o mostrar un mensaje
                            etAtributo1.setText("");
                            etAtributo2.setText("");
                            // También puedes mostrar un mensaje indicando que la información de posición no está disponible
                            Toast.makeText(this, "La información de posición no está disponible", Toast.LENGTH_LONG).show();
                        }

                        etDireccionFarola.setText(direccion);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    // Manejar errores de consulta
                });
    }

    public void subirArchivo(View v){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = etNombreFarola.getText().toString();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                subirFichero(data.getData(), "farolas/"+name+"/"+name);
            }
        }
    }
    public void subirFichero(Uri uri, String referenciaFirebase) {
        if (uri != null) {
            // Obtener la referencia de Firebase Storage utilizando la referencia proporcionada
            StorageReference ficheroRef = storageRef.child(referenciaFirebase);

            // Subir el archivo a Firebase Storage
            ficheroRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // La subida fue exitosa
                        // Aquí puedes realizar acciones adicionales después de la subida exitosa
                        // como mostrar un mensaje de éxito o actualizar la interfaz de usuario
                    })
                    .addOnFailureListener(exception -> {
                        // La subida falló. Maneja el error aquí.
                        // Puedes mostrar un mensaje de error o realizar acciones de recuperación.
                    });
        } else {
            // El URI es nulo, maneja este caso según sea necesario
        }
    }
    public void descargarYMostrarImagen(View v) {
        // Reemplaza "imagenes/imagen.jpg" con la referencia correcta en tu Firebase Storage
        String name = etNombreFarola.getText().toString();
        String referenciaFirebase = "farolas/"+name+"/"+name;

        // Crear una referencia a la ubicación del archivo en Firebase Storage
        StorageReference ficheroRef = storageRef.child(referenciaFirebase);

        try {
            // Crear un archivo temporal local donde se guardará la imagen descargada
            File localFile = File.createTempFile("imagen", "jpg");

            // Descargar la imagen en el archivo temporal local
            ficheroRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // La descarga fue exitosa
                        // Aquí puedes realizar acciones adicionales después de la descarga exitosa
                        // como mostrar la imagen en tu interfaz de usuario
                        mostrarImagen(localFile.getAbsolutePath());
                    })
                    .addOnFailureListener(exception -> {
                        // La descarga falló. Maneja el error aquí.
                        // Puedes mostrar un mensaje de error o realizar acciones de recuperación.
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mostrarImagen(String filePath) {
        // Este método puede ser personalizado según la forma en que desees mostrar la imagen.
        // Por ejemplo, puedes establecer la imagen en un ImageView.
        // Aquí hay un ejemplo básico:

        ImageView imageView = findViewById(R.id.fotoFarola); // Reemplaza con el ID de tu ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);
    }
}

