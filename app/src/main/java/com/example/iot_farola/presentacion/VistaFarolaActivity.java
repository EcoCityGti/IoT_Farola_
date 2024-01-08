package com.example.iot_farola.presentacion;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VistaFarolaActivity extends AppCompatActivity {
    private static int RESQUEST_CODE =1234;
    private StorageReference storageRef;
    private RepositorioFarolas farolas;
    private Uri uriUltimaFoto;
    private String id;
    private Farola farola;
    private ImageView foto;
    private ImageView borrar;
    private TextView nombre,direcciontxt;
    ActivityResultLauncher<Intent> tomarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK && uriUltimaFoto!=null) {
                        farola.setFoto(uriUltimaFoto.toString());
                        ponerFoto(foto, farola.getFoto());
                    } else {
                        Toast.makeText(VistaFarolaActivity.this,
                                "Error en captura", Toast.LENGTH_LONG).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        farola.setFoto(uri.toString());
                        ponerFoto(foto, uri.toString());
                    } else {
                        Toast.makeText(VistaFarolaActivity.this,
                                "Foto no cargada", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_farola);
        farolas = ((Aplicacion) getApplication()).farolas;
        Bundle extras = getIntent().getExtras();
        id = extras.getString("nombreFarola", "id");
        nombre = findViewById(R.id.nombre);
        nombre.setText(id);
        Aplicacion aplicacion = (Aplicacion) getApplication();
        aplicacion.farolaId=nombre.getText().toString();
        foto = findViewById(R.id.foto);
        borrar =findViewById(R.id.borrar);
        storageRef = FirebaseStorage.getInstance().getReference();

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarFoto(view);
            }
        });
    }
    public void actualizaVistas() {
        nombre = findViewById(R.id.nombre);
        ImageView logoTipo = findViewById(R.id.logo_tipo);
        TextView tipo = findViewById(R.id.tipo);
        direcciontxt = findViewById(R.id.direccion);
        TextView telefono = findViewById(R.id.telefono);
        RatingBar valoracion = findViewById(R.id.valoracion);
        ImageView foto = findViewById(R.id.foto);
        nombre.setText(id);
        descargarYMostrarImagen();
        obtenerDireccion();
    }
    public void fotoDeGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }
    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null && !uri.isEmpty() && !uri.equals("null")) {
            imageView.setImageURI(Uri.parse(uri));
        } else {
            imageView.setImageBitmap(null);
        }
    }
    public void tomarFoto(View view) {
        try {
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis()/ 1000), ".jpg" ,
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        this, "es.upv.jtomas.mislugares.fileProvider", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent   = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra (MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            tomarFotoLauncher.launch(intent);
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void eliminarFoto(View view) {
        farola.setFoto("");
        ponerFoto(foto, "");
    }
    @Override
    public void onResume() {
        super.onResume();
        actualizaVistas();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESQUEST_CODE && resultCode==RESULT_OK) {
            actualizaVistas();
        }
    }
    public void borrarFoto(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Borrar Foto")
                .setMessage("¿Estás seguro que quieres eliminar esta foto?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        farola.setFoto(""); // Borra la referencia a la foto en el objeto Lugar
                        ponerFoto(foto, ""); // Limpia la vista de la foto
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    public void descargarYMostrarImagen() {
        // Reemplaza "imagenes/imagen.jpg" con la referencia correcta en tu Firebase Storage
        String name = nombre.getText().toString();
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
        ImageView imageView = findViewById(R.id.imageFarola);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);
    }
    private void obtenerDireccion() {
        // Obtiene una referencia al documento dentro de la colección "farola" con el nombre proporcionado
        String nombreFarola = nombre.getText().toString();
        DocumentReference farolaRef = FirebaseFirestore.getInstance().collection("farolas").document(nombreFarola);

        // Realiza la lectura del documento
        farolaRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Verifica si el documento existe
                    if (task.getResult().exists()) {
                        // Obtiene el valor del campo "direccion"
                        String direccion = task.getResult().getString("direccion");
                        direcciontxt.setText(direccion);
                    } else {
                        // El documento no existe
                        // Puedes manejar esta situación según tus necesidades
                    }
                } else {
                    // Maneja cualquier error que pueda ocurrir durante la lectura
                    Log.e(TAG, "Error al obtener la dirección: " + task.getException());
                }
            }
        });
    }


}
