package com.example.iot_farola.presentacion;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.datos.AdaptadorFarolas;
import com.example.iot_farola.R;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tab1 extends Fragment {
    private StorageReference storageRef;
    private ImageView fotoUsuario;
    private FirebaseUser usuario;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ViewPager2 viewpager = v.findViewById(R.id.viewPaGer);
        viewpager.setAdapter(new com.example.iot_farola.MiPageAdapter1(requireActivity()));
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = v.findViewById(R.id.nombre);
        storageRef = FirebaseStorage.getInstance().getReference();
        fotoUsuario = v.findViewById(R.id.perfil);
        nombre.setText(usuario.getDisplayName());
        if(usuario.isAnonymous()){
            nombre.setText("Invitado/a");
        }

        //String[] nombres = new String[]{"Listas","Mapa de la zona"};
        TabLayout tabs = v.findViewById(R.id.tabs1);
        new TabLayoutMediator(tabs, viewpager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        Drawable iconDrawable = null;
                        int iconSizeInDp = 150; // Tamaño del icono en dp (ajusta este valor según tus necesidades)

                        switch (position) {
                            case 0:
                                tab.setText(R.string.lista);
                                tab.setIcon(R.drawable.baseline_format_list_bulleted_24);
                                break;
                            case 1:
                                tab.setText(R.string.mapa);
                                tab.setIcon(R.drawable.baseline_map_24);
                                break;
                        }
                    }
                }
        ).attach();
        descargarYMostrarImagen();
        return v;
    }
    public void descargarYMostrarImagen() {
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
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap roundedBitmap = redondearImagen(bitmap);
        fotoUsuario.setImageBitmap(roundedBitmap);
    }
    private Bitmap redondearImagen(Bitmap bitmap) {
        // Obtén el mínimo entre el ancho y el alto de la imagen
        int minSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        // Crea un objeto Bitmap con el mismo tamaño (ancho y alto iguales) y configuración ARGB
        Bitmap output = Bitmap.createBitmap(minSize, minSize, Bitmap.Config.ARGB_8888);

        // Crea un objeto Canvas para dibujar
        Canvas canvas = new Canvas(output);

        // Crea un objeto Paint para configurar la apariencia del dibujo
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        // Crea un objeto RectF con el mismo tamaño que el Bitmap
        final RectF rectF = new RectF(0, 0, minSize, minSize);

        // Dibuja un círculo utilizando el objeto Canvas y Paint
        canvas.drawRoundRect(rectF, minSize / 2, minSize / 2, paint);

        return output;
    }
    @Override
    public void onResume() {
descargarYMostrarImagen();
        super.onResume();
    }
}
