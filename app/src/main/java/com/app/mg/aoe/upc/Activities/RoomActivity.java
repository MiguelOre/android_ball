package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;
import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.R;

public class RoomActivity extends AppCompatActivity {

    Button btnCrearSala;
    EditText e1;

    String port = "8080";
    String ipAddress;
    ConnectMethods connectMethods = new ConnectMethods();
    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();
    WebsocketServer wsServer;
    WebsocketClient wsClient;
    InetSocketAddress inetSockAddress;
    ServerSocket ss;

    boolean slot2full =false;
    boolean slot3full =false;
    boolean slot4full =false;
    boolean firstAction = false;

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
                            Toast.makeText(getApplicationContext(),"ip: " + ip, Toast.LENGTH_SHORT).show();

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

        //crea el activity
        setContentView(R.layout.activity_room);
        btnCrearSala = findViewById(R.id.btnCrearSala2);

        e1= findViewById(R.id.et_NombreSala);

        //obtiene valor almacenado
        String value = Preferences.getPrefs("name",RoomActivity.this);

        //carga el nombre de la sala si se ha encontrado almacenado en memoria
        if(value!="") //nombre de la sala
            e1.setText(value);

        //conexión con la sala
        Thread myThread = new Thread(new MyServer());
        myThread.start();
        //server

        ipAddress = connectMethods.FindMyIpAddress(this);
        Toast.makeText(getApplicationContext(),"ipAddress: "+ipAddress,Toast.LENGTH_SHORT).show();


        SetWServerAndStart();

        if (wsServer != null) {
            String nombreDeLaSalaCreada = Preferences.getPrefs("nameRoom",RoomActivity.this);
            //txtRoom.setText("SALA " + nombreDeLaSalaCreada + "    ");

            //wwun
            System.out.println("Mando: nombre de la sala: "+nombreDeLaSalaCreada);
        }
        Handler handler = new Handler();
        handler.postDelayed(this::connectWebSocket, 2000);
        //

        btnCrearSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreDeSalaCreada = e1.getText().toString();
                System.out.println("El nombre de la sala creada es: "+nombreDeSalaCreada);

                //GRABARNOMBRE
                Preferences.setPrefs("nameRoom", nombreDeSalaCreada,RoomActivity.this);

                //Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                //startActivity(intent);

                //cambiando el flujo

                Intent intent = new Intent(v.getContext(), Player1SetName.class);
                startActivity(intent);
            }
        });

    }

    public String getSalaName(){
        EditText nombreDeSala = findViewById(R.id.et_NombreSala);
        return nombreDeSala.getText().toString();
    }

    private void sendMessageStop() {
        SendMessageBody("STOPRIGHT");
    }

    private void connectWebSocket() {


        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress, port));
        wsClient.connect();
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
                    .setMessage("NAME1"+ "." +Preferences.getPrefs("name",RoomActivity.this))
                    .setSender(ipAddress)
                    .setToTV(true);
            messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);
            firstAction = true;
        }
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

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}










