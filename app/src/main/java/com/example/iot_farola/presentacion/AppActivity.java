package com.example.iot_farola.presentacion;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iot_farola.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

//-------------------------App--------------------------------------------
public class AppActivity extends AppCompatActivity {
    private BroadcastReceiver chargingStateReceiver;

    //---------------------On Create()--------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        setContentView(R.layout.tabs_barra);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new com.example.iot_farola.MiPageAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        //---------------------Configuracion TabsBarra()--------------------------------------------

        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        Drawable iconDrawable = null;
                        int iconSizeInDp = 150; // Tamaño del icono en dp (ajusta este valor según tus necesidades)
//----------------------------------Insertar iconos en cada tab--------------------------------------------------------------
                        switch (position) {
                            case 0:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.twotone_home_24);
                                break;
                            case 1:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.farolasin);
                                break;
                            case 2:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_electric_bolt_24);
                                break;
                            case 3:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_account_circle_24);
                                break;
                        }
//--------------------------------------------------Resize Icon(ignorar)--------------------------------------------------------------------
                        if (iconDrawable != null) {
                            iconDrawable.setBounds(0, 0, (int) (iconSizeInDp * getResources().getDisplayMetrics().density), (int) (iconSizeInDp * getResources().getDisplayMetrics().density));
                            //iconDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.my_dark_tertiary), PorterDuff.Mode.SRC_ATOP);
                            tab.setIcon(iconDrawable);
                        }
                    }
                }
        ).attach();//Configuracion TabBarra()
// Registrar el receptor para la desconexión del cable
        ChargingStateReceiver chargingDisconnectedReceiver = new ChargingStateReceiver();
        IntentFilter disconnectedFilter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(chargingDisconnectedReceiver, disconnectedFilter);

        // Registrar el receptor para la conexión del cable
        ChargingStateReceiver chargingConnectedReceiver = new ChargingStateReceiver();
        IntentFilter connectedFilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(chargingConnectedReceiver, connectedFilter);
    }//On create()

    //---------------------Charging State BroadcastReceiver--------------------------------------------
    private class ChargingStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                // Crear y mostrar la notificación al desconectar el cargador
                mostrarNotificacion();
            }else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                // Cargador conectado
                cancelarNotificacion();
            }
        }

        private void mostrarNotificacion() {
            // Crea un Intent para abrir la actividad cuando se hace clic en la notificación
            Intent intent = new Intent(AppActivity.this, AppActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(AppActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Crea un canal de notificación (debes hacerlo una vez, normalmente en el inicio de tu aplicación)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Canal de Notificación";
                String description = "Descripción del canal";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("canal_id", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Construye la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(AppActivity.this, "canal_id")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Cargador Desconectado")
                    .setContentText("El cargador ha sido desconectado.")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Muestra la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AppActivity.this);
            if (ActivityCompat.checkSelfPermission(AppActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }
    private void cancelarNotificacion() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AppActivity.this);
        notificationManager.cancel(1);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el BroadcastReceiver al destruir la actividad
        unregisterReceiver(chargingStateReceiver);
    }
}