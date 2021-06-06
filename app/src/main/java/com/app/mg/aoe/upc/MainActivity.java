package com.app.mg.aoe.upc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.app.mg.aoe.upc.Activities.ControlBActivity;
import com.app.mg.aoe.upc.Activities.ReadQRActvity;
import com.app.mg.aoe.upc.Activities.RoomActivity;
import com.app.mg.aoe.upc.Activities.WSAndControlActivity;

public class MainActivity extends AppCompatActivity {

    Button btnWSC;
    Button btncreateRoom;
    Button btnC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btncreateRoom = findViewById(R.id.btnCrearSala);
        btnWSC = findViewById(R.id.btnBuscarSala);

        //btnC = findViewById(R.id.btnC);

        btncreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RoomActivity.class);
                startActivity(intent);
            }
        });

        btnWSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReadQRActvity.class);
                    startActivity(intent);
            }
        });

        /*
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ControlActivity.class);
                startActivity(intent);
            }
        });*/
    }
}
