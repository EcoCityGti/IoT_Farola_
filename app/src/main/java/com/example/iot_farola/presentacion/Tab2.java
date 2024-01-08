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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tab2 extends Fragment implements SearchView.OnQueryTextListener{
    private static final int TU_CODIGO_DE_SOLICITUD_DE_PERMISO = 1; // Puedes elegir cualquier número
    private TextView nombre;
    private FirebaseFirestore db;
    private List<String> dataList = new ArrayList<>();  // Declara tu lista de datos como un miembro de la clase
    SensorDataAdapter sensorAdapter;
    private TextView luz;
    private int valorNumerico;
    private Button mas,menos;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        Aplicacion aplicacion = (Aplicacion) requireActivity().getApplication();
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        Button emergency = v.findViewById(R.id.Emergencias);
        nombre = v.findViewById(R.id.textView24);  // Asegúrate de inicializar farolas
        luz = v.findViewById(R.id.luminosidad);
        mas = v.findViewById(R.id.btnmas);
        menos = v.findViewById(R.id.btnmenos);
        nombre.setText(aplicacion.farolaId);
        db = FirebaseFirestore.getInstance();
        valorNumerico=50;
        obtenerValorTemperatura();
        obtenerValorRuido();
        obtenerValorHumedad();
        actualizarTextView();
        obtenerValorHumo();
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
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_mas(v);
                //subirLuminosidad(v);
            }
        });
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_menos(v);
                //subirLuminosidad(v);
            }
        });
        GridView gridView = v.findViewById(R.id.tabla_sensores);

// Create a list of dummy sensor data
        List<String> sensorDataList = DataList();

// Create a custom adapter with the data
        sensorAdapter = new SensorDataAdapter(requireContext(), sensorDataList);

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
        // Llena tu lista de datos con los valores actuales o inicia con valores predeterminados
        dataList.clear();  // Limpia la lista antes de agregar nuevos elementos
        dataList.add("25°C");
        dataList.add("60%");
        dataList.add("50dB");
        dataList.add("800l");
        // Puedes agregar más elementos o realizar cualquier otra lógica necesaria
        return dataList;
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
    public void btn_menos(View v) {
        // Resta 5 al valor numérico
        valorNumerico -= 50;

        // Asegúrate de que el valor no sea menor que 0
        if (valorNumerico < 50) {
            valorNumerico = 50;
        }

        // Actualiza el TextView
        actualizarTextView();
        subirLuminosidad();
    }
    public void btn_mas(View v) {
        // Suma 5 al valor numérico
        valorNumerico += 50;

        // Asegúrate de que el valor no supere el límite superior
        if (valorNumerico > 100) {
            valorNumerico = 100;
        }

        // Actualiza el TextView
        actualizarTextView();
        subirLuminosidad();
    }

    private void actualizarTextView() {
        // Muestra el valor numérico actual en el TextView
        luz.setText(String.valueOf(valorNumerico));
    }
    public void subirLuminosidad() {
        // Obtiene el valor de luminosidad del EditText
        String nombredoc = nombre.getText().toString();
        if (!nombredoc.equals("id")) {
            // Obtiene una referencia a la colección "farolas" y un nuevo documento con nombre "luminosidad"
            DocumentReference farolaRef = db.collection("farolas").document(nombredoc);

            // Actualiza el campo "luminosidad" en el documento
            farolaRef.update("luminosidad", valorNumerico)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Operación exitosa
                            // Puedes agregar aquí cualquier lógica adicional después de subir la luminosidad
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejo de errores
                        }
                    });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        obtenerValorHumedad();
        obtenerValorTemperatura();
        obtenerValorHumo();
        obtenerValorRuido();
        // Actualiza el texto de nombre cada vez que el fragmento está en primer plano
        actualizarNombre();
    }
    private void actualizarNombre() {
        // Obtiene la instancia de la aplicación
        Aplicacion aplicacion = (Aplicacion) requireActivity().getApplication();

        // Actualiza el texto de nombre con el valor de farolaId
        nombre.setText(aplicacion.farolaId);
    }
    private void obtenerValorHumedad() {
        // Obtén el nombre de la farola
        String nombreFarola = nombre.getText().toString();

        // Accede a la colección "medidas" en Firestore
        CollectionReference medidasCollection = FirebaseFirestore.getInstance().collection("medidas");

        // Obtiene una referencia al documento específico
        DocumentReference documentoRef = medidasCollection.document(nombreFarola);

        // Realiza la lectura del documento
        documentoRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error
                Log.e(TAG, "Error al obtener el valor de humedad: ", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento existe, ahora verifica si contiene el campo "humedad"
                if (documentSnapshot.contains("humedad")) {
                    // Obtiene el valor de "humedad"
                    Double valorHumedad = documentSnapshot.getDouble("humedad");
                    if(valorHumedad!=null){
                    // Haz algo con el valor de humedad, por ejemplo, imprímelo en el log
                    Log.d(TAG, "Valor de humedad: " + valorHumedad);

                    // Actualiza la lista de datos con el nuevo valor de humedad
                    dataList.set(1, String.valueOf(valorHumedad) + "%");
                    // Actualiza la interfaz de usuario con la nueva lista de datos
                    sensorAdapter.notifyDataSetChanged();
                } else {
                        // El valor de humo es nulo, maneja la situación según tus necesidades
                        Log.d(TAG, "El valor de humo es nulo");
                    }
                } else {
                    // El documento no contiene el campo "humedad"
                    Log.d(TAG, "El documento no contiene el campo 'humedad'");
                }
            } else {
                // El documento no existe
                Log.d(TAG, "El documento no existe");
            }
        });
    }
    private void obtenerValorTemperatura() {
        // Obtén el nombre de la farola
        String nombreFarola = nombre.getText().toString();

        // Accede a la colección "medidas" en Firestore
        CollectionReference medidasCollection = FirebaseFirestore.getInstance().collection("medidas");

        // Obtiene una referencia al documento específico
        DocumentReference documentoRef = medidasCollection.document(nombreFarola);

        // Realiza la lectura del documento
        documentoRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error
                Log.e(TAG, "Error al obtener el valor de temperatura: ", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento existe, ahora verifica si contiene el campo "humedad"
                if (documentSnapshot.contains("temperatura")) {
                    // Obtiene el valor de "humedad"
                    Double valorHumedad = documentSnapshot.getDouble("temperatura");
                    if(valorHumedad!=null){
                    // Haz algo con el valor de humedad, por ejemplo, imprímelo en el log
                    Log.d(TAG, "Valor de temperatura: " + valorHumedad);

                    // Actualiza la lista de datos con el nuevo valor de humedad
                    dataList.set(0, String.valueOf(valorHumedad) + "ºC");
                    // Actualiza la interfaz de usuario con la nueva lista de datos
                    sensorAdapter.notifyDataSetChanged();
                } else {
                        // El valor de humo es nulo, maneja la situación según tus necesidades
                        Log.d(TAG, "El valor de temperatura es nulo");
                    }
                } else {
                    // El documento no contiene el campo "humedad"
                    Log.d(TAG, "El documento no contiene el campo 'temperatura'");
                }
            } else {
                // El documento no existe
                Log.d(TAG, "El documento no existe");
            }
        });
    }
    private void obtenerValorRuido() {
        // Obtén el nombre de la farola
        String nombreFarola = nombre.getText().toString();

        // Accede a la colección "medidas" en Firestore
        CollectionReference medidasCollection = FirebaseFirestore.getInstance().collection("medidas");

        // Obtiene una referencia al documento específico
        DocumentReference documentoRef = medidasCollection.document(nombreFarola);

        // Realiza la lectura del documento
        documentoRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error
                Log.e(TAG, "Error al obtener el valor de temperatura: ", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento existe, ahora verifica si contiene el campo "humedad"
                if (documentSnapshot.contains("ruido")) {
                    // Obtiene el valor de "humedad"
                    Double valorHumedad = documentSnapshot.getDouble("ruido");
                    if(valorHumedad!=null){
                    // Haz algo con el valor de humedad, por ejemplo, imprímelo en el log
                    Log.d(TAG, "Valor de ruido: " + valorHumedad);

                    // Actualiza la lista de datos con el nuevo valor de humedad
                    dataList.set(2, String.valueOf(valorHumedad) + "dB");
                    // Actualiza la interfaz de usuario con la nueva lista de datos
                    sensorAdapter.notifyDataSetChanged();
                }else {
                        // El valor de humo es nulo, maneja la situación según tus necesidades
                        Log.d(TAG, "El valor de ruido es nulo");
                    }
                } else {
                    // El documento no contiene el campo "humedad"
                    Log.d(TAG, "El documento no contiene el campo 'ruido'");
                }
            } else {
                // El documento no existe
                Log.d(TAG, "El documento no existe");
            }
        });
    }
    private void obtenerValorHumo() {
        // Obtén el nombre de la farola
        String nombreFarola = nombre.getText().toString();

        // Accede a la colección "medidas" en Firestore
        CollectionReference medidasCollection = FirebaseFirestore.getInstance().collection("medidas");

        // Obtiene una referencia al documento específico
        DocumentReference documentoRef = medidasCollection.document(nombreFarola);

        // Realiza la lectura del documento
        documentoRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error
                Log.e(TAG, "Error al obtener el valor de humo: ", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento existe, ahora verifica si contiene el campo "humedad"
                if (documentSnapshot.contains("humo")) {
                    // Obtiene el valor de "humedad"
                    Double valorHumedad = documentSnapshot.getDouble("humo");
                    if(valorHumedad!=null){
                    // Haz algo con el valor de humedad, por ejemplo, imprímelo en el log
                    Log.d(TAG, "Valor de humo: " + valorHumedad);

                    // Actualiza la lista de datos con el nuevo valor de humedad
                    dataList.set(3, String.valueOf(valorHumedad) + "l");
                    // Actualiza la interfaz de usuario con la nueva lista de datos
                    sensorAdapter.notifyDataSetChanged();
                    }else {
                        // El valor de humo es nulo, maneja la situación según tus necesidades
                        Log.d(TAG, "El valor de humo es nulo");
                    }
                } else {
                    // El documento no contiene el campo "humedad"
                    Log.d(TAG, "El documento no contiene el campo 'humo'");
                }
            } else {
                // El documento no existe
                Log.d(TAG, "El documento no existe");
            }
        });
    }
}
