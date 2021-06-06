package com.app.mg.aoe.upc.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;
import com.app.mg.aoe.upc.WebSocket.WebsocketServer;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.connectionlibraryandroid.Implementations.MessageMethods;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class SearchRoomActivity extends AppCompatActivity  {
    Button btnqr, btnIngresar;
    TextView tvBarCode;
    EditText nombreDeLaSalaIp,nombreDelJugador;

    //wwun
    WebsocketClient wsClient;
    WebsocketServer wsServer;
    String ipAddress;
    MessageMethods<MessageBody, WebsocketClient, WebSocket> messageMethods = new MessageMethods<>();
    boolean firstAction = false;
    ConnectMethods connectMethods = new ConnectMethods();
    String port = "8080";
    InetSocketAddress inetSockAddress;

    //AvatarSelection
    int images[] = {R.drawable.batman, R.drawable.captain, R.drawable.blackwidow, R.drawable.ironman, R.drawable.tombraider, R.drawable.wonderwomen};
    ViewFlipper v_flipper;
    Button prev_Button, next_Button;
    int imagesPos = 0;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readqr);
        btnqr = findViewById(R.id.btnScan);
        btnIngresar = findViewById(R.id.button);
        //tvBarCode = findViewById(R.id.tvScan);
        btnqr.setOnClickListener(mOnClickListener);
        nombreDeLaSalaIp= findViewById(R.id.editText);    //Nombre de la sala //ip
        nombreDelJugador= findViewById(R.id.editText3);   //Nombre del jugador //nombre

        String name = Preferences.getPrefs("name",SearchRoomActivity.this);
        String value = Preferences.getPrefs("ip",SearchRoomActivity.this);   //Nombre de la sala

        /* wwun test
        if(nombreDeLaSalaIp.getText().toString().isEmpty()){
            nombreDeLaSalaIp.setText("salaWun");  //test
            nombreDeLaSalaIp.setTextColor(Color.BLACK);
        }
        */

        //nombreDeLaSalaIp.setBackgroundColor(Color.GRAY);

        if(nombreDeLaSalaIp.getText().toString().isEmpty()){
            btnIngresar.setEnabled(false);
        }

        if(value!="") {
            nombreDeLaSalaIp.setText(value);
            nombreDeLaSalaIp.setTextColor(Color.BLACK);
            btnIngresar.setEnabled(true);

            //wwun
            ipAddress = value;
        }

        if(name!="")
            nombreDelJugador.setText(name);

        //  Thread myThread = new Thread(new MyServer());
        // myThread.start();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SendMessageBody("nombreDelJugador2:"+nombreDelJugador.getText().toString());

                int currentImagePosition = v_flipper.getCurrentView().getResources().getResourceName(images[(int)v_flipper.getDisplayedChild()]).indexOf("/") + 1;
                String avatarSelectedName = v_flipper.getCurrentView().getResources().getResourceName(images[(int)v_flipper.getDisplayedChild()]).substring((currentImagePosition));
                SendMessageBody("nombreDelJugador2:"+nombreDelJugador.getText().toString()+"|"+avatarSelectedName);//+"avatarSelectedName1:"+avatarSelectedName.toString());

                Intent intent = new Intent(view.getContext(), WSAndControlActivity.class);
                startActivity(intent);
            }
        });

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

    class MyServer implements Runnable{

        ServerSocket ss;
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
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public void button_click(View v){

        //  BackgroundTask b = new BackgroundTask();
        //  b.execute(e1.getText().toString(),e2.getText().toString());

        //GRABARNOMBRE
        Preferences.setPrefs("nombreDelJugador2",nombreDelJugador.getText().toString(),SearchRoomActivity.this);

        Intent i = new Intent(SearchRoomActivity.this, WSAndControlActivityClient.class);
        i.putExtra("ip",nombreDeLaSalaIp.getText().toString());
        i.putExtra("nombreDelJugador2",nombreDelJugador.getText().toString());
        startActivity(i);
    }
    public class BackgroundTask extends AsyncTask<String,Void,String>{
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

    private  View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnScan:
                    new IntentIntegrator(SearchRoomActivity.this).initiateScan();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
            if(result.getContents() != null){
                String ip = result.getContents();
                String[] numbers = ip.split(":");
                String element = numbers[1];
                String ipfinal = element.substring(2, element.length());
                nombreDeLaSalaIp.setText(ipfinal);
                Preferences.setPrefs("ip",ipfinal,SearchRoomActivity.this);  //ip escaneado
                System.out.println("ip escaneado: "+ipfinal);
                //Toast.makeText(getApplicationContext(), "ip leido = "+ip, Toast.LENGTH_SHORT).show();
            }else {
                // e1.setText("");
            }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //wwun
    private void SendMessageBody(String message) {

        //Toast.makeText(getApplicationContext(), "ipAddress"+ipAddress, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "port "+port, Toast.LENGTH_SHORT).show();

        //
        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress, port));
        wsClient.connect();
        //

        //
        inetSockAddress = connectMethods.GetISocketAddres(this, port);
        wsServer = new WebsocketServer(inetSockAddress);
        wsServer.setReuseAddr(true);
        wsServer.start();
        //

        //Toast.makeText(getApplicationContext(), "WsClient "+wsClient, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "wsServer "+wsClient.isOpen(), Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "WsClient isOpen "+wsClient.isOpen(), Toast.LENGTH_LONG).show();
        System.out.println("WsClient: "+wsClient);
        System.out.println("wsServer: "+wsServer);
        System.out.println("WsClient isOpen: "+wsClient.isOpen());

        if (wsClient == null || wsServer == null || !wsClient.isOpen()) return;

        MessageBody messageBody = new MessageBody()
                .setMessage(message)
                .setSender(ipAddress)
                .setToTV(true);

        messageMethods.SendMessageBody(messageBody, wsClient, ipAddress);
        /*
        if(firstAction==false)
        {
            MessageBody messageBody2 = new MessageBody()
                    .setMessage("NAME2"+ "." +Preferences.getPrefs("nombreDelJugador2",this))
                    .setSender(ipAddress)
                    .setToTV(true);

            //messageMethods.SendMessageBody(messageBody2, wsClient, ipAddress);

            //firstAction = true;

        /*}*/

    }

    //avatarSelection
    private void setFlipperImage(int res){

        ImageView avatar = new ImageView(getApplicationContext());
        avatar.setBackgroundResource(res);

        v_flipper.addView(avatar);
    }

}
