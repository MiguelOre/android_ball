package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mg.aoe.upc.Entities.Gyroscope;
import com.app.mg.aoe.upc.Entities.Accelerometer;
import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.Util.Variables;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;
import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.Helpers.InputHelper;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;

import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WSAndControlActivity extends AppCompatActivity {

    String port = "8080";
    String ipAddress;
    int valor_sensibilidad;

    ImageButton btnUp, btnLeft, btnDown, btnRight;
    TextView txtRoom;

    ConnectMethods connectMethods = new ConnectMethods();
    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();
    WebsocketServer wsServer;
    WebsocketClient wsClient;
    InetSocketAddress inetSockAddress;
    Vibrator vibrator;
    MediaPlayer mp;

    ServerSocket ss;

    boolean slot2full =false;
    boolean slot3full =false;
    boolean slot4full =false;

    boolean firstAction = false;

    //btnTemporal, borrar cuando se capture el mensaje desde celular
    Button btnIngresarNombre;

    //giroscopio
    private Gyroscope gyroscope;

    //acelerómetro
    private Accelerometer accelerometer;

    Button btnDeathZone;

    //float sensibilityValue = 0;

    Bundle parametros;

    float maxAcceleratorValue = -1.0f;

    public class BackgroundTask extends AsyncTask<String,Void,String> {
        Socket s;
        DataOutputStream dos;
        String ip,message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            //Toast.makeText(getApplicationContext(),"ip: "+ip,Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(),"Waiting for client",Toast.LENGTH_SHORT).show();
                    }
                });
                while(true){
                    mysocket = ss.accept();
                    dis = new DataInputStream(mysocket.getInputStream());
                    message = dis.readUTF();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(),"message recieve from client: " + message, Toast.LENGTH_SHORT).show();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsand_control);

         System.out.println("Nueva instancia creada!!!");

        //obteniendo valores de DeathZoneActivity
        parametros = this.getIntent().getExtras();
        if(parametros == null){
            Variables.sensibilityValue = 5.5f;
        } else {
            Variables.sensibilityValue = (float) (((float)parametros.getInt("sensibilityValue"))/10 + 0.5);
            System.out.println("inside if -> sensibilityValue = "+Variables.sensibilityValue);

        }

        valor_sensibilidad = (int)((Variables.sensibilityValue - 0.5)*10);

        //valor_sensibilidad = (int)((Variables.sensibilityValue - 0.5)*10);

        //obteniendo valores de DeathZoneActivity
        /*
        parametros = this.getIntent().getExtras();
        if(parametros != null){
            valor_sensibilidad = (float) (((float)parametros.getInt("sensibilityValue"))/10 + 0.5);
            System.out.println("inside if -> sensibilityValue = "+valor_sensibilidad);
        }
        */


        //giroscopio
        gyroscope = new Gyroscope(this);
        System.out.println("gyroscope created");

        //acelerómetro
        accelerometer = new Accelerometer(this);
        System.out.println("accelerometer created");

        //server
        Thread myThread = new Thread(new MyServer());
        myThread.start();
        //server

        ipAddress = connectMethods.FindMyIpAddress(this);

        mp = MediaPlayer.create(this, R.raw.button_press);
        btnUp = findViewById(R.id.ib_up);
        btnLeft = findViewById(R.id.ib_left);
        btnDown = findViewById(R.id.ib_down);
        btnRight = findViewById(R.id.ib_right);
        txtRoom = findViewById(R.id.txt_room);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        btnIngresarNombre = findViewById(R.id.btnIngresarNombre);
        btnIngresarNombre.setEnabled(true);
        btnDeathZone = findViewById(R.id.btnDeathZone);

        btnUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("ARROWUP1");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                SendMessageBody("STOPARROWUP1");
            }
            return false;
        });

        btnLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("ARROWLEFT1");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                SendMessageBody("STOPARROWLEFT1");
            }
            return false;
        });

        btnDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("ARROWDOWN1");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                SendMessageBody("STOPARROWDOWN1");
            }
            return false;
        });

        btnRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("ARROWRIGHT1");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                SendMessageBody("STOPARROWRIGHT1");
            }
            return false;
        });


        btnIngresarNombre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(view.getContext(), Player1SetName.class);
                //startActivity(intent);
                //finish();
            }
        });

        btnDeathZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeathZoneActivity.class);
                intent.putExtra("valor_sensibilidad", valor_sensibilidad);
                startActivity(intent);
                finish();

            }
        });

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {

                if(tx > 2.5f) {
                    SendMessageBody("AccelerometerXUP1:" + tx);
                }else{
                    if(tx < -2.5f){
                        SendMessageBody("AccelerometerXDOWN1:" + tx);
                    }
                }

                if(ty > 2.5f) {
                    if (ty > maxAcceleratorValue) {
                        maxAcceleratorValue = ty;
                    }else{
                        //envía el valor capturado del acelerómetro
                        SendMessageBody("AccelerometerY1:" + maxAcceleratorValue);
                        //System.out.println("enviando movimiento del sensor AcceleromterY = " + maxAcceleratorValue);
                        maxAcceleratorValue = -1;
                    }
                }
                /*else{  //esto no se usa en el juego
                    if(ty < -2.5f) {
                        SendMessageBody("AccelerometerYDOWN:" + ty);
                    }
                }
                */

                /*
                if(tz > 2.5f) {
                    SendMessageBody("AccelerometerXUP:" + tz);
                }else{
                    if(tz < -2.5f){
                        SendMessageBody("AccelerometerY:" + maxAcceleratorValue);
                    }
                }
                */
            }
        });

        //giroscopio listener
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                System.out.println("======================sensibilityValue======================="+Variables.sensibilityValue);
                if(rx > Variables.sensibilityValue){
                    //System.out.println("sensibilityValue = "+Variables.sensibilityValue);
                    //System.out.println("enviando movimiento del sensor GyroscopeXRIGHT = "+rx);
                    SendMessageBody("GyroscopeXRIGHT1:" + rx); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(rx < (-1)*Variables.sensibilityValue) {
                        //System.out.println("sensibilityValue = "+ (-Variables.sensibilityValue));
                        //System.out.println("enviando movimiento del sensor GyroscopeXLEFT = " + rx);
                        SendMessageBody("GyroscopeXLEFT1:" + rx);//el giroscopio se ha girado hacia la derecha
                    }else{//rx<variable y rx>-varible
                        SendMessageBody("GyroscopeX1:" + 0);
                    }
                }

                if(ry > Variables.sensibilityValue){
                    //System.out.println("sensibilityValue = "+Variables.sensibilityValue);
                    //System.out.println("enviando movimiento del sensor GyroscopeYUP = "+ry);
                    SendMessageBody("GyroscopeYUP1:" + ry); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(ry < (-1)*Variables.sensibilityValue) {
                        //System.out.println("sensibilityValue = "+(-Variables.sensibilityValue));
                        //System.out.println("enviando movimiento del sensor GyroscopeYDOWN = " + ry);
                        SendMessageBody("GyroscopeYDOWN1:" + ry);//el giroscopio se ha girado hacia la derecha
                    }else{//ry<variable y ry>-varible
                        SendMessageBody("GyroscopeY1:" + 0);
                    }
                }

                if(rz > Variables.sensibilityValue){
                    //System.out.println("sensibilityValue = "+Variables.sensibilityValue);
                    //System.out.println("enviando movimiento del sensor GyroscopeYUP = "+rz);
                    SendMessageBody("GyroscopeZCLOCKWISE1:" + rz); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(rz < (-1)*Variables.sensibilityValue) {
                        //System.out.println("sensibilityValue = "+(-Variables.sensibilityValue));
                        //System.out.println("enviando movimiento del sensor GyroscopeYDOWN = " + rz);
                        SendMessageBody("GyroscopeZANTICLOCKWISE1:" + rz);//el giroscopio se ha girado hacia la derecha
                    }
                }
            }
        });
        //fin giroscopio listener

        SetWServerAndStart();

        if (wsServer != null) {
            String nombreDeLaSalaCreada = Preferences.getPrefs("nameRoom",WSAndControlActivity.this);
            txtRoom.setText("SALA " + nombreDeLaSalaCreada);

            //wwun
            //System.out.println("Mando: nombre de la sala: "+nombreDeLaSalaCreada);
        }
        Handler handler = new Handler();
        handler.postDelayed(this::connectWebSocket, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume(); // TODO: Revisar esto
        gyroscope.register();
        accelerometer.register();
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
        finish();
    }


    private void sendMessageStop() {
        SendMessageBody("STOPRIGHT1");
    }

    private void connectWebSocket() {
        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress, port));
        wsClient.connect();
        //Toast.makeText(getApplicationContext(), "Server Abierto", Toast.LENGTH_SHORT).show();
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
                    .setMessage("NAME1"+ "." +Preferences.getPrefs("name",WSAndControlActivity.this))
                    .setSender(ipAddress)
                    .setToTV(true);
            messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);
            firstAction = true;
        }
        //Toast.makeText(getApplicationContext(),"ipAddress: "+ipAddress,Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        //finish();
        //ventana emergente para cerrar las conexiones
        AlertDialog.Builder myBulid = new AlertDialog.Builder(this);
        myBulid.setMessage("Se finalizará la conexión con el Smart TV");
        myBulid.setTitle("Mensaje");
        myBulid.setPositiveButton("Si", (dialog, which) -> {
            try {
                SetWServerClose();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ss != null && !ss.isClosed()) {
                try {
                    ss.close();
                } catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }
            }
            finish();
        });
        myBulid.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }
}