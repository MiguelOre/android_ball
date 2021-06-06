package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;

import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EventListener;

public class Player1SetName extends AppCompatActivity {

    String port = "8080";

    String ipAddress;

    EditText nombreDelJugador1;
    Button btnIngresarNombreJ1;

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

    //AvatarSelection
    int images[] = {R.drawable.icon_player, R.drawable.batman, R.drawable.captain, R.drawable.blackwidow, R.drawable.ironman, R.drawable.tombraider, R.drawable.wonderwomen};
    ViewFlipper v_flipper;
    Button prev_Button, next_Button;
    int imagesPos = 0;
    //

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
                            Toast.makeText(getApplicationContext(),"ip: "+ip,Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_player1_set_name);

        //server
        Thread myThread = new Thread(new MyServer());
        myThread.start();
        //server

        ipAddress = connectMethods.FindMyIpAddress(this);
        Toast.makeText(getApplicationContext(),"ipAddress"+ipAddress,Toast.LENGTH_SHORT).show();

        mp = MediaPlayer.create(this, R.raw.button_press);

        nombreDelJugador1 = findViewById(R.id.et_NombreJugador1);

        nombreDelJugador1.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(btnIngresarNombreJ1.isEnabled()==false) {
                    if (!nombreDelJugador1.getText().toString().matches(""))
                        btnIngresarNombreJ1.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnIngresarNombreJ1 = findViewById(R.id.btnIngresarNombreJ1);
        btnIngresarNombreJ1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //envía a la vista del mando

                mp.start();

                //envía avatar seleccionado
                //obtiene la cadena que contiene el nombre
                int currentImagePosition = v_flipper.getCurrentView().getResources().getResourceName(images[(int)v_flipper.getDisplayedChild()]).indexOf("/") + 1;
                String avatarSelectedName = v_flipper.getCurrentView().getResources().getResourceName(images[(int)v_flipper.getDisplayedChild()]).substring((currentImagePosition));

                // envía el nombre del jugador
                SendMessageBody("nombreDelJugador1:"+nombreDelJugador1.getText().toString()+"|"+avatarSelectedName);//+"avatarSelectedName1:"+avatarSelectedName.toString());

                // habilita el mando
                Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                startActivity(intent);
                System.out.println("Cerrando Player1SetName  - Creando Activity");
                finish();
            }
        });

        SetWServerAndStart();

        if (wsServer != null) {
            Preferences.setPrefs("nombreDelJugador1", nombreDelJugador1.getText().toString(),Player1SetName.this);
            Preferences.setPrefs("avatarSelectedName1", "batman",Player1SetName.this);
            //wwun
            System.out.println("Player1SetName -> Nombre del Jugador 1: "+nombreDelJugador1.getText().toString());

            //ahora el nombre de la sala la recibe esta interfaz
            //revisar el contexto de donde se obtiene el nombre de la sala creada
            String nombreDeLaSalaCreada = Preferences.getPrefs("nameRoom", Player1SetName.this);
            System.out.println("Nombre de la sala creada: "+nombreDeLaSalaCreada);
            //Preferences.setPrefs("nameRoom", nombreDeLaSalaCreada,Player1SetName.this);
        }
        Handler handler = new Handler();
        handler.postDelayed(this::connectWebSocket, 2000);

        //avatarSelection
        prev_Button = findViewById(R.id.prev_button);
        next_Button = findViewById(R.id.next_button);

        v_flipper = findViewById(R.id.v_flipper);

        for(int i=0; i<images.length; i++){
            setFlipperImage(images[i]);
        }

        prev_Button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                v_flipper.showPrevious();
            }
        });

        next_Button.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                v_flipper.showNext();

                v_flipper.getCurrentView().getBackground();
            }
        }));
        //fin avatarSelection
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
                    .setMessage("NAME1"+ "." +Preferences.getPrefs("name",Player1SetName.this))
                    .setSender(ipAddress)
                    .setToTV(true);

            messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);

            firstAction = true;

        }

    }

    //avatarSelection
    private void setFlipperImage(int res){

        ImageView avatar = new ImageView(getApplicationContext());
        avatar.setBackgroundResource(res);

        v_flipper.addView(avatar);
    }

}