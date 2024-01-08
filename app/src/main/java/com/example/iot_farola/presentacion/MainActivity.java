package com.example.iot_farola.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.iot_farola.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private FirebaseUser usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imagen);
        Button empezar = findViewById(R.id.empezar);
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        if(usuario!=null){
            checkUserRoleAndStartActivity();
        }
        empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la nueva actividad
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                // Opcionalmente, puedes pasar datos a la nueva actividad utilizando extras
                //intent.putExtra("clave", valor); // Reemplaza "clave" y "valor" con tus datos

                // Iniciar la nueva actividad
                startActivity(intent);
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
    private void checkUserRoleAndStartActivity() {

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
}