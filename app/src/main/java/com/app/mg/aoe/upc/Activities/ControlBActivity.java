package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;
import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ControlBActivity extends AppCompatActivity {

    String port = "8080";
    String ipAddress;

    ImageButton btnUp, btnLeft, btnDown, btnRight, btnA,btnB,btnX,btnY;

    ConnectMethods connectMethods = new ConnectMethods();
    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();

    WebsocketClient wsClient;
    InetSocketAddress inetSockAddress;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_b);

        ipAddress = connectMethods.FindMyIpAddress(this);

        btnUp = findViewById(R.id.ib_up);
        btnLeft = findViewById(R.id.ib_left);
        btnDown = findViewById(R.id.ib_down);
        btnRight = findViewById(R.id.ib_right);
        btnA = findViewById(R.id.ib_x);
        btnB = findViewById(R.id.ib_b);
        btnX = findViewById(R.id.ib_y);
        btnY = findViewById(R.id.ib_a);

        btnUp.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("UP");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPUP");
            }
            return false;
        });

        btnLeft.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("LEFT");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPLEFT");
            }
            return false;
        });

        btnDown.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("DOWN");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPDOWN");
            }
            return false;
        });
        btnRight.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("RIGHT");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPRIGHT");
            }

            return false;
        });

        btnA.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("A");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPA");
            }
            return false;
        });

        btnB.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("B");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPB");
            }
            return false;
        });

        btnY.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("Y");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPY");
            }
            return false;
        });
        btnX.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                SendMessageBody("X");
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                SendMessageBody("STOPX");
            }

            return false;
        });

        SetWServerAndStart();

        Handler handler = new Handler();
        handler.postDelayed(this::connectWebSocket, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsClient != null) wsClient.close();
    }


    private void sendMessageStop(){
        SendMessageBody("STOPRIGHT");
    }

    private void connectWebSocket() {

        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress, port));
        wsClient.connect();
        Toast.makeText(getApplicationContext(),"Server Abierto",Toast.LENGTH_SHORT).show();
    }

    private void SendMessageBody(String message) {
        if(wsClient == null) return;
        MessageBody messageBody = new MessageBody()
                .setMessage(message)
                .setSender(ipAddress)
                .setToTV(true);

        messageMethods.SendMessageBody(messageBody, wsClient, ipAddress);
    }

    private void SetWServerAndStart() {
        inetSockAddress = connectMethods.GetISocketAddres(this, port);
        WebsocketServer wsServer = new WebsocketServer(inetSockAddress);
        wsServer.start();

    }

    private void SetWServerClose() throws IOException, InterruptedException {
        inetSockAddress = connectMethods.GetISocketAddres(this, port);
        WebsocketServer wsServer = new WebsocketServer(inetSockAddress);
        wsServer.stop();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(this);
        myBulid.setMessage("Se finalizara la conección con el Smart TV");
        myBulid.setTitle("Mensaje");
        myBulid.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    SetWServerClose();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        myBulid.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }
}
