package com.app.mg.aoe.upc.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.mg.aoe.upc.Helpers.Preferences;
import com.app.mg.aoe.upc.R;

public class RoomFinishActivity extends AppCompatActivity {
    Button btnCrearSala;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room2);

        btnCrearSala = findViewById(R.id.btnCrearSala3);

        btnCrearSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);



                int width = metrics.widthPixels; // ancho absoluto en pixels
                int height = metrics.heightPixels;
                if (height == 1920 || width == 720) {

                    Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                    startActivity(intent);

                }
                else{
                    Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                    startActivity(intent);
                    // WSAndControlActivity
                }
            }
        });


    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
