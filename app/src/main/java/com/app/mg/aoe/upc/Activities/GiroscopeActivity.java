package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;

import android.support.v7.app.AppCompatActivity;

import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GiroscopeActivity extends AppCompatActivity {
    String port = "8080";

    String ipAddress;

    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();

    WebsocketServer wsServer;
    WebsocketClient wsClient;

    MediaPlayer mp;

    ServerSocket ss;

    boolean slot2full =false;
    boolean slot3full =false;
    boolean slot4full =false;

    boolean firstAction = false;

    InetSocketAddress inetSockAddress;

    ConnectMethods connectMethods = new ConnectMethods();

    class MyServer implements Runnable{

        Socket mysocket;
        DataInputStream dis;
        String message;
        Handler handler = new Handler();

        @Override
        public void run() {

            try {
                ss = new ServerSocket(9700);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Waiting for client",Toast.LENGTH_SHORT).show();
                    }
                });
                while(true){
                    mysocket = ss.accept();
                    dis = new DataInputStream(mysocket.getInputStream());

                    message = dis.readUTF();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"message recieve from client: " + message, Toast.LENGTH_SHORT).show();
                            System.out.println(message);

                            //
                            String ip = mysocket.getInetAddress().getHostAddress();

                            //
                            if(message.equals("END2")){
                                slot2full=false;
                            }

                            if(message.equals("END3")){
                                slot3full=false;
                            }

                            if(message.equals("END4")){
                                slot4full=false;
                            }


                            if(message.equals("START2")){

                                if(slot2full==false) {
                                    BackgroundTask b2 = new BackgroundTask();
                                    b2.execute(ip,"2");
                                    slot2full=true;}
                                else {
                                    BackgroundTask b2 = new BackgroundTask();

                                    if(slot3full==false){
                                        b2.execute(ip,"3");
                                        slot3full = true;
                                        System.out.println(ip);
                                    }
                                    else if(slot4full==false){
                                        b2.execute(ip,"4");
                                        slot4full = true;
                                        System.out.println(ip);
                                    }
                                    else{
                                        b2.execute(ip,"0");
                                        System.out.println(ip);
                                    }
                                }
                            }


                            System.out.println("J2-J3-J4");
                            System.out.println(slot2full +"-"+ slot3full + "-"+ slot4full);
                            SendMessageBody(message);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class BackgroundTask extends AsyncTask<String,Void,String> {
        Socket s;
        DataOutputStream dos;
        String ip,message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];

            try {
                s= new Socket(ip,9700);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);

                dos.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giroscope);

        //server
        Thread myThread = new Thread(new MyServer());
        myThread.start();
        //server

        ipAddress = connectMethods.FindMyIpAddress(this);

        mp = MediaPlayer.create(this, R.raw.button_press);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //obtener datos de giroscpio

        Gyroscope gyroscope = new Gyroscope(this);

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {

                String movimiento = "";

                if(rx > 0.5f){
                    movimiento = "LEFT";
                }else{
                    if(rx < -0.5f){
                        movimiento = "RIGHT";
                    }
                }

                mp.start();

                // envía el nombre del jugador
                SendMessageBody("movimientoDelGiroscopio:"+movimiento);
            }
        });

        //fin de obtener datos de giroscopio

        /* /borrar si funciona giroscopio
        btnIngresarNombreJ1.setOnClickListener(new View.OnClickListener(){ //reemplazar este evento
            @Override
            public void onClick(View v){
                //envía a la vista del mando

                mp.start();

                // envía el nombre del jugador
                SendMessageBody("movimientoDelGiroscopio:"+nombreDelJugador1.getText().toString());

                // habilita el mando nuevamente
                Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                startActivity(intent);
            }
        });
         */

        SetWServerAndStart();

        if (wsServer != null) {
            //wwun
            System.out.println("movimientoDelGiroscopio: if (wsServer != null)");
        }
        Handler handler = new Handler();
        handler.postDelayed(this::connectWebSocket, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsClient != null) wsClient.close();
        if (wsServer != null) {
            try {
                SetWServerClose();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (ss != null && !ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
        }
    }

    private void connectWebSocket() {
        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress, port));
        wsClient.connect();
        Toast.makeText(getApplicationContext(), "Server Abierto", Toast.LENGTH_SHORT).show();
    }

    private void SetWServerAndStart() {
        inetSockAddress = connectMethods.GetISocketAddres(this, port);
        wsServer = new WebsocketServer(inetSockAddress);
        wsServer.setReuseAddr(true);
        wsServer.start();
        System.out.println("wwun: wsServer.start()");
    }

    private void SetWServerClose() throws IOException, InterruptedException {
        wsServer.stop();
    }

    private void SendMessageBody(String message) {
        if (wsClient == null || wsServer == null || !wsClient.isOpen()) return;

        MessageBody messageBody = new MessageBody()
                .setMessage(message)
                .setSender(ipAddress)
                .setToTV(true);

        messageMethods.SendMessageBody(messageBody, wsClient, ipAddress);

        if(firstAction==false)
        {
            MessageBody messageBody2 = new MessageBody()
                    .setMessage("NAME1"+ "." +Preferences.getPrefs("name",this))
                    .setSender(ipAddress)
                    .setToTV(true);

            messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);

            firstAction = true;
        }
    }

    //captura de datos para giroscopio

        public static class Gyroscope {

            float timestamp;

            public interface Listener {
                void onRotation(float rx, float ry, float rz);
            }

            private Listener listener;

            public void setListener(Listener l) {
                listener = l;
            }

            private SensorManager sensorManager;
            private Sensor sensor;
            private SensorEventListener sensorEventListener;

            Gyroscope(Context context) {
                sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

                sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        //sensorEvent.timestamp;
                        if (listener != null && sensorEvent.timestamp != 0) {
                            long mills = (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
                            //System.out.println("mills = " + mills);
                            listener.onRotation(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
            }

            public void register() {
                sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_NORMAL);
            }

            public void unregister() {
                sensorManager.unregisterListener(sensorEventListener);
            }
        }
    //fin de captura de datos para giroscopio

}
