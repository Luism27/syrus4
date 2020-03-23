package com.example.syrus4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView mensaje1;
    TextView ip, puerto, tiem;
    String   Text;
    String latitud;
    String longitud,ids;
    EditText syrus;
    int mes,dia,hora,segundo,minuto,time,c;
    long año;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip= (EditText)findViewById(R.id.dip);
        puerto= (EditText)findViewById(R.id.port);
        tiem= (EditText)findViewById(R.id.mili);
        mensaje1 = (TextView) findViewById(R.id.mostrar);
        syrus = (EditText)findViewById(R.id.ID);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
    }

    public void conectar(View view){
        String direccion = ip.getText().toString();
        String puert = puerto.getText().toString();
        if(direccion.length() == 0 || puert.length() == 0){
            Toast.makeText(this,"complete los campos (ip y puerto)",Toast.LENGTH_LONG).show();
        }else {

            String tiempo = tiem.getText().toString();
            if(tiempo.length() == 0){
                 time = 1000;
                Toast.makeText(this,"Tiempo en blanco, se asume 1000( 1segundo)",Toast.LENGTH_LONG).show();
            }
            if (tiempo.length() != 0){
                time = Integer.parseInt(tiempo);
            }

            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    client();
                }
            },0,time);

        }



    }

    public void client() {
        ids = syrus.getText().toString();
        if(ids.length()==0 ){
            ids="None";
        }
        Date fecha = new Date();
         año = fecha.getTime();
        Text = ">REV"+año+latitud+longitud+",ID="+ids+"<";
         System.out.println(año);
        try {
            String messageStr = Text;
            String gate = puerto.getText().toString();
            int server_port = Integer.parseInt(gate);
            InetAddress local = InetAddress.getByName(ip.getText().toString());
            int msg_length = messageStr.length();
            byte[] message = messageStr.getBytes();


            DatagramSocket s = new DatagramSocket();
            //

            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            s.send(p);//properly able to send data. i receive data to server
            mensaje1.setText(Text);

        } catch (Exception ex) {

        }


    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

        mensaje1.setText("Localizacion agregada");

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            DecimalFormat formato = new DecimalFormat("#.00000");
             latitud = formato.format(loc.getLatitude());
             longitud = formato.format(loc.getLongitude());

            //Fecha actual desglosada:



        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}