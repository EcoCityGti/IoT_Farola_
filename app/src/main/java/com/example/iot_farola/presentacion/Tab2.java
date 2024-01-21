package com.example.iot_farola.presentacion;



import static android.content.ContentValues.TAG;
import static com.example.iot_farola.datos.Mqtt.broker;
import static com.example.iot_farola.datos.Mqtt.clientId;
import static com.example.iot_farola.datos.Mqtt.qos;
import static com.example.iot_farola.datos.Mqtt.topicRoot;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.SensorDataAdapter;
import com.example.iot_farola.datos.AdaptadorFarolasFirestoreUI;
import com.example.iot_farola.datos.AdaptadorFarolasResultadosFirestoreUI;
import com.example.iot_farola.datos.RepositorioFarolas;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tab2 extends Fragment implements MqttCallback {
    private static final int TU_CODIGO_DE_SOLICITUD_DE_PERMISO = 1; // Puedes elegir cualquier número
    private TextView nombre;
    private  static MqttClient client;

    private StorageReference storageRef;

    private FirebaseUser usuario;

    private FirebaseFirestore db;
    private List<String> dataList = new ArrayList<>();  // Declara tu lista de datos como un miembro de la clase
    SensorDataAdapter sensorAdapter;
    private TextView luz;
    private ImageView fotoUsuario;
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
        Button emergency = v.findViewById(R.id.Emergencias);
        nombre = v.findViewById(R.id.textView24);  // Asegúrate de inicializar farolas
        luz = v.findViewById(R.id.luminosidad);
        mas = v.findViewById(R.id.btnmas);
        menos = v.findViewById(R.id.btnmenos);
        nombre.setText(aplicacion.farolaId);
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        fotoUsuario = v.findViewById(R.id.imagen3);
        valorNumerico=50;
        conectarMqtt();
        obtenerValorTemperatura();
        obtenerValorRuido();
        obtenerValorHumedad();
        actualizarTextView();
        obtenerValorHumo();
        descargarYMostrarImagen();
        //conectarMqtt();
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
                publicarMqtt("luminosidad",luz.getText().toString());
            }
        });
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_menos(v);
                //subirLuminosidad(v);
                publicarMqtt("luminosidad",luz.getText().toString());
            }
        });
        GridView gridView = v.findViewById(R.id.tabla_sensores);

// Create a list of dummy sensor data
        List<String> sensorDataList = DataList();

// Create a custom adapter with the data
        sensorAdapter = new SensorDataAdapter(requireContext(), sensorDataList);

// Set the adapter for the GridView
        gridView.setAdapter(sensorAdapter);
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
        descargarYMostrarImagen();
        conectarMqtt();
    }
    @Override
    public void onStop() {
        deconectarMqtt();
        super.onStop();
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
//--------------------------------------------------------
//---------------------CARGAR IMAGEN----------------------
//--------------------------------------------------------
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
//--------------------------------------------------------
//-------------------------MQTT---------------------------
//--------------------------------------------------------

    public static void conectarMqtt() {
    try {
        Log.i(TAG, "Conectando al broker " + broker);
        client = new MqttClient(broker, clientId, new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setWill(topicRoot+"WillTopic","App desconectada".getBytes(),
                qos, false);
        client.connect(connOpts);
    } catch (MqttException e) {
        Log.e(TAG, "Error al conectar.", e);
    }
}
    public static void publicarMqtt(String topic, String mensageStr) {
        try {
            MqttMessage message = new MqttMessage(mensageStr.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topicRoot + topic, message);
            Log.i(TAG, "Publicando mensaje: " + topic+ "->"+mensageStr);
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar." + e);
        }
    }
    public static void deconectarMqtt() {
        try {
            client.disconnect();
            Log.i(TAG, "Desconectado");
        } catch (MqttException e) {
            Log.e(TAG, "Error al desconectar.", e);
        }
    }
    @Override
    public void onDestroy() {
        //deconectarMqtt();
        super.onDestroy();
    }
    public static String suscribirMqtt(String topic, MqttCallback listener) {
        try {
            Log.i(TAG, "Suscrito a " + topicRoot + topic);
            client.subscribe(topicRoot + topic, qos);
            client.setCallback(listener);
            return "Suscrito a " + topicRoot + topic;
        } catch (MqttException e) {
            Log.e(TAG, "Error al suscribir.", e);
            return "Error al suscribir." + e;
        }
    }

    @Override public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexión perdida");
    }
    @Override public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "Entrega completa");
    }
    @Override public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, "Recibiendo: " + topic + "->" + payload);
    }


}
