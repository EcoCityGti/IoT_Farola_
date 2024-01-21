package com.example.iot_farola.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.R;
import com.example.iot_farola.modelo.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tab4 extends Fragment {
    Button btnAnonimo,subirFoto,guardar;
    private  EditText correo, telf, nusu, postal, contr, nombre;
    private DocumentReference userRef;
    private StorageReference storageRef;
    private FirebaseUser usuario;
    private ImageView fotoUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edicion_cuenta, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar1);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        fotoUsuario = v.findViewById(R.id.imageView8);
        subirFoto = v.findViewById(R.id.subirFoto);
        subirFoto.setEnabled(false);
        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    subirArchivo(v);
                    Log.d("Subir foto perfil","va bien");
                }catch (Exception e){
                    Log.d("Subir foto perfil",e.toString());
                }
            }
        });
        fotoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    descargarYMostrarImagen(v);
                    Log.d("Subir foto perfil","va bien");
                }catch (Exception e){
                    Log.d("Subir foto perfil",e.toString());
                }
            }
        });
        btnAnonimo = v.findViewById(R.id.UnificarCuenta);
        nombre = v.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        if(usuario.isAnonymous()){
            nombre.setText("Invitado/a");
        }

        correo = v.findViewById(R.id.correoE);
        correo.setText(usuario.getEmail());

        telf = v.findViewById(R.id.telefono);
        nusu = v.findViewById(R.id.usuario);
        postal = v.findViewById(R.id.postal);
        contr = v.findViewById(R.id.contrasenya);
        telf.setText(usuario.getPhoneNumber());

        TextView uid = v.findViewById(R.id.Uid);
        uid.setText(usuario.getUid());

        Button button = v.findViewById(R.id.btn_cerrar_sesion1);
       // Button editar = v.findViewById(R.id.toggleButton);
        guardar = v.findViewById(R.id.Guardar);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the updated data from EditText fields
                String nuevoNombre = nombre.getText().toString();
                String nuevoTelefono = telf.getText().toString();
                String nuevoCorreo = correo.getText().toString();
                String nuevaDireccion = postal.getText().toString();
                String nuevoNombreUsu = nusu.getText().toString();

                // Update user data in the database (Firestore, Realtime Database, etc.)
                // You may need to call a method to update the user data in your database
                updateUserData(usuario.getUid(), nuevoNombre, nuevoTelefono, nuevoCorreo, nuevaDireccion, nuevoNombreUsu);

                // Display a message indicating the update
                Toast.makeText(requireActivity(), "Datos actualizados", Toast.LENGTH_SHORT).show();
            }
        });
        userRef = FirebaseFirestore.getInstance().collection("usuarios").document(usuario.getUid());

        // Establece un Listener para escuchar cambios en tiempo real
        userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Maneja el error aquí
                Log.w("Firestore", "Error al escuchar cambios en el documento", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Maneja los datos actualizados del usuario aquí
                String nuevoNombre = documentSnapshot.getString("nombre");
                String nuevoTelefono = documentSnapshot.getString("telefono");
                String nuevoCorreo = documentSnapshot.getString("correo");
                String nuevaDireccion = documentSnapshot.getString("direccion");
                String nuevoNombreUsu = documentSnapshot.getString("nombreUsuario");
                //String nuevaContraseña = documentSnapshot.getString("contrasenya");

                // Actualiza los campos de la interfaz de usuario con los nuevos datos
                nombre.setText(nuevoNombre);
                telf.setText(nuevoTelefono);
                correo.setText(nuevoCorreo);
                postal.setText(nuevaDireccion);
                nusu.setText(nuevoNombreUsu);
                //contr.setText(nuevaContraseña);
            } else {
                // Maneja el caso en que el documento no existe
                Log.d("Firestore", "El documento no existe");
            }
        });

        // Set an OnClickListener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the cerrarSesion method when the button is clicked
                cerrarSesion(view);
            }
        });

        btnAnonimo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Registro.class);
                i.putExtra("unificar",true);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });

        if (usuario != null && usuario.isAnonymous()) {
            // El usuario actual es anónimo, por lo que muestras el botón
            button.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            //editar.setVisibility(View.GONE);
            btnAnonimo.setVisibility(View.VISIBLE);
        } else {
            // El usuario no es anónimo, ocultas el botón
            button.setVisibility(View.VISIBLE);
            btnAnonimo.setVisibility(View.GONE);
            guardar.setVisibility(View.VISIBLE);
           // editar.setVisibility(View.VISIBLE);
        }
        if (usuario != null) {
            obtenerYMostrarTelefono(usuario.getUid());
            obtenerYMostrarNombreUsuario(usuario.getUid());
            obtenerYMostrarDireccion(usuario.getUid());
        } else {
            // Manejar el caso en el que el usuario es nulo
        }
        return v;
    }
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Manejar la selección del elemento de menú "action_settings".
            return true;
        }
        if (id == R.id.acercaDe){
            lanzarAcercaDe();
            return true;
        }
        if (id == R.id.action_editar){
            editar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void lanzarAcercaDe() {
        Intent i = new Intent(requireActivity(), AcercaDeActivity.class);
        //mp.pause();
        startActivity(i);
    }
    public void cerrarSesion(View view) {
        AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(requireActivity(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        requireActivity().finish();
                    }
                });
    }
    private void updateUserData(String uid, String nuevoNombre, String nuevoTelefono, String nuevoCorreo,String nuevaDireccion, String nuevoNombreUsu) {
        // Get the reference to the Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);

        // Create a map with the updated user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nuevoNombre);
        userData.put("telefono", nuevoTelefono);
        userData.put("correo", nuevoCorreo);
        userData.put("direccion", nuevaDireccion);
        userData.put("nombreUsuario", nuevoNombreUsu);
        //userData.put("contrasenya", nuevaContraseña);

        // Update the document in Firestore
        userRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document updated successfully
                        Toast.makeText(requireActivity(), "Datos actualizados en Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Toast.makeText(requireActivity(), "Error al actualizar datos en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void editar(){
     if(nombre.isEnabled()){
         nombre.setEnabled(false);
         correo.setEnabled(false);
         telf.setEnabled(false);
         nusu.setEnabled(false);
         postal.setEnabled(false);
         contr.setEnabled(false);
         subirFoto.setEnabled(false);
         guardar.setEnabled(false);
     }else{
         nombre.setEnabled(true);
         correo.setEnabled(true);
         telf.setEnabled(true);
         nusu.setEnabled(true);
         postal.setEnabled(true);
         contr.setEnabled(true);
         subirFoto.setEnabled(true);
         guardar.setEnabled(true);
     }
 }
    private void obtenerYMostrarTelefono(String uidUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(uidUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Obtener el número de teléfono del documento
                                String numeroTelefono = document.getString("telefono");

                                if (numeroTelefono != null) {
                                    // Establecer el número de teléfono en tu TextView
                                    telf.setText(numeroTelefono);
                                } else {
                                    // Manejar el caso en el que el campo de teléfono es nulo
                                    telf.setText("Número de teléfono no disponible");
                                }
                            } else {
                                // Manejar el caso en el que el documento no existe
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            // Manejar errores en la obtención del documento
                            Log.d("TAG", "Error getting document", task.getException());
                        }
                    }
                });
    }
    private void obtenerYMostrarNombreUsuario(String uidUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(uidUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Obtener el número de teléfono del documento
                                String nombreUsuario = document.getString("nombreUsuario");

                                if (nombreUsuario != null) {
                                    // Establecer el número de teléfono en tu TextView
                                    nusu.setText(nombreUsuario);
                                } else {
                                    // Manejar el caso en el que el campo de teléfono es nulo
                                    nusu.setText("Nombre de usuario no disponible");
                                }
                            } else {
                                // Manejar el caso en el que el documento no existe
                                Log.d("Nombre Usuario", "No such document");
                            }
                        } else {
                            // Manejar errores en la obtención del documento
                            Log.d("Nombre Usuario", "Error getting document", task.getException());
                        }
                    }
                });
    }
    private void obtenerYMostrarDireccion(String uidUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(uidUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Obtener el número de teléfono del documento
                                String direccion = document.getString("direccion");
                                if (direccion != null) {
                                    // Establecer el número de teléfono en tu TextView
                                    postal.setText(direccion);
                                } else {
                                    // Manejar el caso en el que el campo de teléfono es nulo
                                    postal.setText("Direccion no disponible");
                                }
                            } else {
                                // Manejar el caso en el que el documento no existe
                                Log.d("direccion", "No such document");
                            }
                        } else {
                            // Manejar errores en la obtención del documento
                            Log.d("direccion", "Error getting document", task.getException());
                        }
                    }
                });
    }
    public void subirArchivo(View v){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }
    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = usuario.getUid().toString();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                subirFichero(data.getData(), "usuarios/"+name+"/"+name);
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
        String name = usuario.getUid().toString();
        String referenciaFirebase = "usuarios/"+name+"/"+name;

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

        // Reemplaza con el ID de tu ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        fotoUsuario.setImageBitmap(bitmap);
    }

}
