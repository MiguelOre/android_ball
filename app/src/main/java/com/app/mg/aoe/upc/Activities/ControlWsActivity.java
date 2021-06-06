package com.app.mg.aoe.upc.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.mg.aoe.upc.Util.Variables;
import com.app.mg.connectionlibraryandroid.Implementations.ClientMessageMethods;
import com.app.mg.connectionlibraryandroid.Implementations.ConnectMethods;
import com.app.mg.aoe.upc.Entities.MessageBody;
import com.app.mg.aoe.upc.Entities.RepeatListener;
import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.WebSocket.WebsocketClient;


public class ControlWsActivity extends AppCompatActivity {
    String ipAddress = "";
    String myIpAddress = "";
    String port = "8080";

    WebsocketClient wsClient;

    ConnectMethods connectMethods = new ConnectMethods();
    ClientMessageMethods<MessageBody,WebsocketClient> messageMethods = new ClientMessageMethods<>();

    Button btnSendMessage;
    EditText etMessage;

    ImageButton btnUp, btnLeft, btnDown, btnRight;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_ws);
        btnSendMessage = findViewById(R.id.btn_send_message);
        etMessage = findViewById(R.id.et_message);

        btnUp = findViewById(R.id.ib_up);
        btnLeft = findViewById(R.id.ib_left);
        btnDown = findViewById(R.id.ib_down);
        btnRight = findViewById(R.id.ib_right);

        ipAddress = getIntent().getStringExtra("networkIp");

        myIpAddress = connectMethods.FindMyIpAddress(this);

        connectWebSocket();

        btnSendMessage.setOnClickListener(v -> {
            SendMessageBody(etMessage.getText().toString());
        });

        btnUp.setOnTouchListener(new RepeatListener(50, 50, v -> SendMessageBody("UP")));

        btnLeft.setOnTouchListener(new RepeatListener(50, 50, v -> SendMessageBody("LEFT")));

        btnDown.setOnTouchListener(new RepeatListener(50, 50, v -> SendMessageBody("DOWN")));

        btnRight.setOnTouchListener(new RepeatListener(50, 50, v -> SendMessageBody("RIGHT")));
    }

    protected void sensibilityControls(Bundle savedInstanceState) {

        int btnUp_value = 0;
        int btnDown_value = 0;
        int rx_btnLeft = 100;
        int rx_btnRight = 100;

        if(rx_btnLeft > Variables.sensibilityValue || rx_btnLeft < (-1)*Variables.sensibilityValue){
            btnUp_value =  Math.round(rx_btnLeft * 20.0f);
            if(Math.abs(btnUp_value) > 150){
                btnUp_value = 150 * (btnUp_value/Math.abs(btnUp_value));
            }
            //Toast.makeText(getApplicationContext(), "posxPuntoCentral: "+posxPuntoCentral, Toast.LENGTH_SHORT).show();
        }else{
            btnUp_value = 0;
        }

        if(rx_btnRight > Variables.sensibilityValue || rx_btnRight < (-1)*Variables.sensibilityValue){
            btnDown_value =  Math.round(rx_btnRight * 20.0f);
            if(Math.abs(btnDown_value) > 150){
                btnDown_value = 150 * (btnDown_value/Math.abs(btnDown_value));
            }
        }else{
            btnDown_value = 0;
        }
    }

    private void connectWebSocket() {
        wsClient = new WebsocketClient(connectMethods.GetUriServer(ipAddress,port));
        wsClient.connect();
    }

    private void SendMessageBody(String message) {
        MessageBody messageBody = new MessageBody()
                .setMessage(message)
                .setSender(myIpAddress)
                .setToTV(true);
        messageMethods.SendMessageBody(messageBody,wsClient,myIpAddress);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(this);
        myBulid.setMessage("Deseas salir de la APP?");
        myBulid.setTitle("Mensaje");
        myBulid.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
