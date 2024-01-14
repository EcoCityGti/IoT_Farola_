package com.example.iot_farola.datos;

import com.example.iot_farola.modelo.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;

public class Usuarios {

public static void guardarUsuario(final FirebaseUser user) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usuariosRef = db.collection("usuarios");

    // Verificar si el correo electrónico ya está en la base de datos
    usuariosRef.document(user.getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // El usuario ya existe, no lo sobrescribas, simplemente carga la información si es necesario
                        Usuario usuarioExistente = document.toObject(Usuario.class);

                        // Puedes hacer algo con el usuario existente si lo necesitas
                    } else {
                        // El usuario no existe, crea un nuevo usuario
                        Usuario nuevoUsuario = new Usuario(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(),"","","" /* Otros campos si es necesario */);
                        usuariosRef.document(user.getUid()).set(nuevoUsuario)
                                .addOnSuccessListener(aVoid -> {
                                    // El usuario se ha guardado correctamente
                                    // Puedes realizar acciones adicionales si es necesario
                                })
                                .addOnFailureListener(e -> {
                                    // Ocurrió un error al guardar el usuario
                                    // Manejar el error según tus necesidades
                                });
                    }
                } else {
                    // Ocurrió un error al realizar la consulta
                    // Manejar el error según tus necesidades
}});}}
