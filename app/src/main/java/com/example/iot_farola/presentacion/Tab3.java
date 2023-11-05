package com.example.iot_farola.presentacion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab3 extends Fragment {
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private ImageButton imageButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        imageButton = v.findViewById(R.id.imageButton2);
        checkBox3 = v.findViewById(R.id.checkBox3);
        checkBox4 = v.findViewById(R.id.checkBox4);
        checkBox5 = v.findViewById(R.id.checkBox5);
        checkBox6 = v.findViewById(R.id.checkBox6);
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
            NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.imagen4);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar u ocultar las casillas de verificación al hacer clic en el ImageButton
                int visibility = checkBox3.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                checkBox3.setVisibility(visibility);
                checkBox4.setVisibility(visibility);
                checkBox5.setVisibility(visibility);
                checkBox6.setVisibility(visibility);
            }
        });
        return v;
    }

}
