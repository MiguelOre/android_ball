package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class WSAndControlActivityClient extends AppCompatActivity {

    String ipAddress;

    ImageButton btnUp, btnLeft, btnDown, btnRight, btnA, btnB, btnX, btnY, btnPause, btnStart;
    TextView txtRoom;

    ConnectMethods connectMethods = new ConnectMethods();
    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();

    WebsocketServer wsServer;
    WebsocketClient wsClient;

    Vibrator vibrator;
    MediaPlayer mp;

    String ipKey = "";
    String jugador = "";
    ServerSocket ss;

    boolean firstAction = false;

    //giroscopio
    private Gyroscope gyroscope;

    //acelerómetro
    private Accelerometer accelerometer;

    Button btnDeathZone;

    //float sensibilityValue = 0;

    Bundle parametros;

    float maxAcceleratorValue = -1.0f;

    //RUNNER PARA ENVIO DE MENSAJES; POR AHORA SIEMPRE SE EJECUTA PERO NO ES NECESARIO
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
                            if(message.equals("2")){
                                  jugador = "2";
                                String nombre = Preferences.getPrefs("name",WSAndControlActivityClient.this);
                                txtRoom.setText("" + jugador + ": "+ nombre + "    ");  //txtRoom.setText("J" + jugador + ": "+ nombre + "    ");
                                //ENVIO NOMBRE
                                BackgroundTask b2 = new BackgroundTask();
                                b2.execute(ipKey,"NAME"+jugador+ "." +nombre);
                            }
                            /*
                            else if(message.equals("3")){
                                jugador = "3";
                                String nombre = Preferences.getPrefs("name",WSAndControlActivityClient.this);
                                txtRoom.setText("J" + jugador + ": "+ nombre);
                                //ENVIO NOMBRE
                                BackgroundTask b2 = new BackgroundTask();
                                b2.execute(ipKey,"NAME"+jugador+ "." +nombre);
                            }
                            else if(message.equals("4")){
                                jugador = "4";
                                String nombre = Preferences.getPrefs("name",WSAndControlActivityClient.this);
                                txtRoom.setText("J" + jugador + ": "+ nombre);
                                //ENVIO NOMBRE
                                BackgroundTask b2 = new BackgroundTask();
                                b2.execute(ipKey,"NAME"+jugador+ "." +nombre);
                            }
                            else if(message.equals("0")){
                                jugador = "0";
                                String nombre = Preferences.getPrefs("name",WSAndControlActivityClient.this);
                                txtRoom.setText("J" + jugador + ": "+ nombre);
                                //ENVIO NOMBRE
                                BackgroundTask b2 = new BackgroundTask();
                                b2.execute(ipKey,"NAME"+jugador+ "." +nombre);
                                Toast.makeText(getApplicationContext(),"La sala esta llena",  Toast.LENGTH_SHORT).show();
                                txtRoom.setText("SALA LLENA");
                            }
                            */
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
        setContentView(R.layout.activity_wsand_control);
        //obteniendo valores de DeathZoneActivity
        parametros = this.getIntent().getExtras();
        if(parametros != null){
            Variables.sensibilityValue = (float) (((float)parametros.getInt("sensibilityValue"))/10 + 0.5);
            System.out.println("inside if -> sensibilityValue = "+Variables.sensibilityValue);
        }

        //giroscopio
        gyroscope = new Gyroscope(this);
        System.out.println("gyroscope created");

        //acelerómetro
        accelerometer = new Accelerometer(this);
        System.out.println("accelerometer created");
        //correr websocket cliente
        Thread myThread = new Thread(new WSAndControlActivityClient.MyServer());
        myThread.start();

        ipAddress = connectMethods.FindMyIpAddress(this);

        //correr websocket cliente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           ipKey  = extras.getString("ip");
           jugador = extras.getString("jugador");
            //The key argument here must match that used in the other activity
            System.out.println(ipKey);
        }

        mp = MediaPlayer.create(this, R.raw.button_press);
        btnUp = findViewById(R.id.ib_up);
        btnLeft = findViewById(R.id.ib_left);
        btnDown = findViewById(R.id.ib_down);
        btnRight = findViewById(R.id.ib_right);
        btnA = findViewById(R.id.ib_x);
        btnB = findViewById(R.id.ib_b);
        btnX = findViewById(R.id.ib_y);
        btnY = findViewById(R.id.ib_a);
        //btnPause = findViewById(R.id.ib_pause);
        //btnStart = findViewById(R.id.ib_play);
        txtRoom = findViewById(R.id.txt_room);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        String nombre = Preferences.getPrefs("name",WSAndControlActivityClient.this);

        txtRoom.setText("J" + jugador + ": "+ nombre);

        //ENVIO GUARDAR SLOT
        BackgroundTask b3 = new BackgroundTask();
        b3.execute(ipKey,"START2");

        //btnIngresarNombre = findViewById(R.id.btnIngresarNombre);
        //btnIngresarNombre.setEnabled(true);
        btnDeathZone = findViewById(R.id.btnDeathZone);

        btnUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"UP2"+jugador);
                SendMessageBody("ARROWUP2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPUP2"+jugador);
                SendMessageBody("STOPARROWUP2");
            }
            return false;
        });

        btnLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"LEFT2"+jugador);
                SendMessageBody("ARROWLEFT2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPLEFT2"+jugador);
                SendMessageBody("STOPARROWLEFT2");
            }
            return false;
        });

        btnDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"DOWN2"+jugador);
                SendMessageBody("ARROWDOWN2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPDOWN2"+jugador);
                SendMessageBody("STOPARROWDOWN2");
            }
            return false;
        });

        btnRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"RIGHT2"+jugador);
                SendMessageBody("ARROWRIGHT2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPRIGHT2"+jugador);
                SendMessageBody("STOPARROWRIGHT2");
            }
            return false;
        });

        btnA.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"A2"+jugador);
                SendMessageBody("BA2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPA2"+jugador);
                SendMessageBody("STOPBA2");
            }
            return false;
        });

        btnB.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"B2"+jugador);
                SendMessageBody("BB2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPB2"+jugador);
                SendMessageBody("STOPBB2");
            }
            return false;
        });

        btnY.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"Y2"+jugador);
                SendMessageBody("BY2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPY2"+jugador);
                SendMessageBody("STOPBY2");
            }
            return false;
        });

        btnX.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"X2"+jugador);
                SendMessageBody("BX2");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                InputHelper.Vibrate(vibrator);
                //BackgroundTask b = new BackgroundTask();
                //b.execute(ipKey,"STOPX2"+jugador);
                SendMessageBody("STOPBX2");
            }
            return false;
        });

        btnPause.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("PAUSE");
            }
            return false;
        });

        btnStart.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputHelper.Vibrate(vibrator);
                mp.start();
                SendMessageBody("START");
            }
            return false;
        });

        btnDeathZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeathZoneActivity.class);
                startActivity(intent);
                finish();
            }
        });

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float tz) {
                if(tx > 2.5f) {
                    SendMessageBody("AccelerometerXUP2:" + tx);
                }else{
                    if(tx < -2.5f){
                        SendMessageBody("AccelerometerXDOWN2:" + tx);
                    }
                }

                if(ty > 2.5f) {
                    if (ty > maxAcceleratorValue) {
                        maxAcceleratorValue = ty;
                    }else{
                        //envía el valor capturado del acelerómetro
                        SendMessageBody("AccelerometerY2:" + maxAcceleratorValue);
                        maxAcceleratorValue = -1;
                    }
                }
            }
        });

        //giroscopio listener
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                System.out.println("======================sensibilityValue======================="+Variables.sensibilityValue);
                if(rx > Variables.sensibilityValue){
                    SendMessageBody("GyroscopeXRIGHT2:" + rx); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(rx < (-1)*Variables.sensibilityValue) {
                        SendMessageBody("GyroscopeXLEFT2:" + rx);//el giroscopio se ha girado hacia la derecha
                    }else{
                        SendMessageBody("GyroscopeX2:" + 0);
                    }
                }

                if(ry > Variables.sensibilityValue){
                    SendMessageBody("GyroscopeYUP2:" + ry); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(ry < (-1)*Variables.sensibilityValue) {
                        SendMessageBody("GyroscopeYDOWN2:" + ry);//el giroscopio se ha girado hacia la derecha
                    }else{
                        SendMessageBody("GyroscopeY2:" + 0);
                    }
                }

                if(rz > Variables.sensibilityValue){
                    SendMessageBody("GyroscopeZCLOCKWISE2:" + rz); //el giroscopio se ha girado hacia la izquierda
                }else{
                    if(rz < (-1)*Variables.sensibilityValue) {
                        SendMessageBody("GyroscopeZANTICLOCKWISE2:" + rz);//el giroscopio se ha girado hacia la derecha
                    }
                }
            }
        });
        //fin giroscopio listener
    }

    @Override
    protected void onResume() {
        super.onResume(); // TODO: Revisar esto
        gyroscope.register();
        accelerometer.register();
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
                    .setMessage("NAME2"+ "." +Preferences.getPrefs("name",WSAndControlActivityClient.this))
                    .setSender(ipAddress)
                    .setToTV(true);
            messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);
            firstAction = true;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(this);
        myBulid.setMessage("Se finalizará la conección con el servidor");
        myBulid.setTitle("Mensaje");
        myBulid.setPositiveButton("Si", (dialog, which) -> {
            BackgroundTask b = new BackgroundTask();
            b.execute(ipKey,"END"+jugador);
            BackgroundTask b2 = new BackgroundTask();
            b2.execute(ipKey,"NAME"+jugador+ "." + " ");
            finish();
            if (ss != null && !ss.isClosed()) {
                try {
                    ss.close();
                } catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }
            }
        });
        myBulid.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackgroundTask b = new BackgroundTask();
        b.execute(ipKey,"END"+jugador);

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
}