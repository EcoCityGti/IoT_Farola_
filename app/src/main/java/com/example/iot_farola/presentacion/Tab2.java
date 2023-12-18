package com.example.iot_farola.presentacion;



import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.SensorDataAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener{
    private static final int TU_CODIGO_DE_SOLICITUD_DE_PERMISO = 1; // Puedes elegir cualquier número
    private TextView nombre;
    private String id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        Button emergency = v.findViewById(R.id.Emergencias);
        nombre = v.findViewById(R.id.textView24);  // Asegúrate de inicializar farolas

        nombre.setText("id");
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba si tienes el permiso CALL_PHONE
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Si tienes permiso, inicia la llamada de emergencia (por ejemplo, 112)
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:112")));
                } else {
                    // Si no tienes permiso, solicita el permiso
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, TU_CODIGO_DE_SOLICITUD_DE_PERMISO);
                }
            }
        });

        RequestQueue colaPeticiones = Volley.newRequestQueue(requireActivity());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        Bitmap output = cache.get(url);

                        if (output == null) {
                            // La imagen no está en la caché, no podemos aplicar el recorte circular.
                            return null;
                        }

                        int width = output.getWidth();
                        int height = output.getHeight();
                        int diameter = Math.min(width, height);
                        Bitmap circularBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(circularBitmap);
                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, diameter, diameter);
                        final RectF rectF = new RectF(rect);
                        final float roundPx = diameter / 2;

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(output, rect, rect, paint);

                        return circularBitmap;
                    }

                });
// Foto de usuario
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.imagen3);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        GridView gridView = v.findViewById(R.id.tabla_sensores);

// Create a list of dummy sensor data
        List<String> sensorDataList = DataList();

// Create a custom adapter with the data
        SensorDataAdapter sensorAdapter = new SensorDataAdapter(requireContext(), sensorDataList);

// Set the adapter for the GridView
        gridView.setAdapter(sensorAdapter);
        SearchView searchView = v.findViewById(R.id.searchView2);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        return v;

    }
    public void llamarTelefono(View view) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:112")));
        } else {
            // Si no tienes permiso, solicita permiso antes de realizar la llamada
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, TU_CODIGO_DE_SOLICITUD_DE_PERMISO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == TU_CODIGO_DE_SOLICITUD_DE_PERMISO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realiza la llamada
                llamarTelefono(getView());
            } else {
                // Permiso denegado, muestra un mensaje al usuario o toma alguna acción adicional
                Toast.makeText(requireContext(), "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private List<String> DataList() {
        List<String> Data = new ArrayList<>();
        Data.add("25°C");
        Data.add("60%");
        Data.add("50dB");
        Data.add("800l");
        // Add more dummy data as needed
        return Data;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        buscarEnFirestore(newText);
        return false;
    }
    private void buscarEnFirestore(String consulta) {
        // Accede a la colección "farolas" en Firestore
        CollectionReference farolasCollection = FirebaseFirestore.getInstance().collection("farolas");

        // Realiza la consulta con el filtro según el nombre (puedes ajustar esto según tu estructura de datos)
        Query query = farolasCollection.whereEqualTo("nombre", consulta);

        // Realiza la consulta y maneja los resultados
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Maneja los resultados de la consulta y actualiza dinámicamente la interfaz de usuario
                    List<DocumentSnapshot> resultados = task.getResult().getDocuments();
                    // Actualiza tu interfaz de usuario con los resultados (puedes usar un adaptador, por ejemplo)
                } else {
                    // Maneja cualquier error que pueda ocurrir durante la consulta
                    Log.e(TAG, "Error al buscar en Firestore: " + task.getException());
                }
            }
        });
    }

}
