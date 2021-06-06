package com.app.mg.aoe.upc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.app.mg.aoe.upc.Entities.Gyroscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mg.aoe.upc.R;
import com.app.mg.aoe.upc.Util.Variables;



public class DeathZoneActivity extends AppCompatActivity {

    Button btnAceptar;
    int sensibilityValue = 0;
    int valor_sensibilidad;
    int progretion = 0;

    int posxPuntoCentral = 0;
    int posyPuntoCentral = 0;

    Bundle parametros;

    //giroscopio
    private Gyroscope gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_death_zone);


        //obteniendo valores de DeathZoneActivity
        parametros = this.getIntent().getExtras();
        if(parametros == null){
            valor_sensibilidad = 10;
        } else {
            valor_sensibilidad = parametros.getInt("valor_sensibilidad");
            System.out.println("inside if -> sensibilityValue = "+valor_sensibilidad);
        }


        SeekBar seekBar = findViewById(R.id.seekBar);
        // Valor Inicial
        seekBar.setProgress(valor_sensibilidad);

        // Valot Final
        seekBar.setMax(100);
        //se debe guardar el valor que se ha configurado

        ConstraintLayout circleDeathZoneLayout = findViewById(R.id.circleDeathZoneLayout);

        Context miContext = this;

        Lienzo fondo = new Lienzo(miContext, 0);
        circleDeathZoneLayout.addView(fondo);

        TextView labelSeekBarValue = (TextView) findViewById(R.id.labelSeekBarValue);
        labelSeekBarValue.setText(""+valor_sensibilidad);

        btnAceptar = findViewById(R.id.btnAceptar);

        //giroscopio
        gyroscope = new Gyroscope(this);
        System.out.println("gyroscope created");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                labelSeekBarValue.setText("" + progress);
                sensibilityValue = progress;
                Lienzo fondo = new Lienzo(miContext, progress);
                circleDeathZoneLayout.addView(fondo);
                System.out.println("SeekBar Progress: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        sensibilityValue = progretion;
        btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), WSAndControlActivity.class);
                if(sensibilityValue == 0){
                    intent.putExtra("sensibilityValue", valor_sensibilidad);
                } else {
                    intent.putExtra("sensibilityValue", sensibilityValue);
                }
                startActivity(intent);
                System.out.println("Cerrando Death Zone  - Creando Activity");
                finish();
            }
        });

        //giroscopio listener
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                //Toast.makeText(getApplicationContext(), "Movimiento del sensor", Toast.LENGTH_SHORT).show();
                System.out.println("======================sensibilityValue======================="+ Variables.sensibilityValue);


                if(rx > Variables.sensibilityValue || rx < (-1)*Variables.sensibilityValue){
                    posxPuntoCentral =  Math.round(rx * 20.0f);
                    if(Math.abs(posxPuntoCentral) > 150){
                        posxPuntoCentral = 150 * (posxPuntoCentral/Math.abs(posxPuntoCentral));
                    }
                    //Toast.makeText(getApplicationContext(), "posxPuntoCentral: "+posxPuntoCentral, Toast.LENGTH_SHORT).show();
                }else{
                    posxPuntoCentral = 0;
                }

                if(ry > Variables.sensibilityValue || ry < (-1)*Variables.sensibilityValue){
                    posyPuntoCentral =  Math.round(ry * 20.0f);
                    if(Math.abs(posyPuntoCentral) > 150){
                        posyPuntoCentral = 150 * (posyPuntoCentral/Math.abs(posyPuntoCentral));
                    }
                    //Toast.makeText(getApplicationContext(), "posyPuntoCentral: "+posyPuntoCentral, Toast.LENGTH_SHORT).show();
                }else{
                    posyPuntoCentral = 0;
                }

                Lienzo fondo = new Lienzo(miContext, sensibilityValue);
                circleDeathZoneLayout.addView(fondo);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        gyroscope.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class Lienzo extends View{

        int radiusSize = 0;

        public Lienzo(Context context, int radiusSize){
            super(context);
            this.radiusSize = radiusSize;
        }

        protected void onDraw(Canvas canvas){
            canvas.drawRGB(255, 255, 255);
            int ancho = canvas.getWidth();
            int alto = canvas.getHeight();

            Paint pincelCenter = new Paint();
            pincelCenter.setARGB(255, 0, 0, 0);
            pincelCenter.setStyle(Paint.Style.FILL_AND_STROKE);

            Paint pincelMaxStroke = new Paint();
            pincelMaxStroke.setARGB(255,0,0, 0);
            pincelMaxStroke.setStrokeWidth(10);
            pincelMaxStroke.setStyle(Paint.Style.STROKE);

            Paint pincelFill = new Paint();
            pincelFill.setARGB(255, 200, 10, 10);
            pincelFill.setStyle(Paint.Style.FILL_AND_STROKE);

            canvas.drawCircle(ancho/2, alto/2, 300, pincelMaxStroke);
            canvas.drawCircle(ancho / 2, alto / 2, 3 * radiusSize, pincelFill);

            //el punto se debe mover de acuerdo con el movimiento del giroscopio
            canvas.drawCircle((ancho/2) + posxPuntoCentral, (alto/2) + posyPuntoCentral, 10, pincelCenter);
            //canvas.drawCircle(ancho/2, alto/2, 5, pincelCenter);
        }
    }

}


/*
if(rx > Variables.sensibilityValue){
    System.out.println("sensibilityValue = "+Variables.sensibilityValue);
    System.out.println("enviando movimiento del sensor GyroscopeXLEFT = "+rx);
    //SendMessageBody("GyroscopeXLEFT:" + rx); //el giroscopio se ha girado hacia la izquierda
    posxPuntoCentral =  Math.round(rx * 15.0f);
    Toast.makeText(getApplicationContext(), "posxPuntoCentral: "+posxPuntoCentral, Toast.LENGTH_SHORT).show();
}else{
    if(rx < (-1)*Variables.sensibilityValue) {
        System.out.println("sensibilityValue = "+ (-Variables.sensibilityValue));
        System.out.println("enviando movimiento del sensor GyroscopeXRIGHT = " + rx);
        //SendMessageBody("GyroscopeXRIGHT:" + rx);//el giroscopio se ha girado hacia la derecha
        posxPuntoCentral =  Math.round(rx * 15.0f);
        Toast.makeText(getApplicationContext(), "posxPuntoCentral: "+posxPuntoCentral, Toast.LENGTH_SHORT).show();
    }
}
*/

/*
if(ry > Variables.sensibilityValue){
    System.out.println("sensibilityValue = "+Variables.sensibilityValue);
    System.out.println("enviando movimiento del sensor GyroscopeYUP = "+ry);
    //SendMessageBody("GyroscopeYUP:" + ry); //el giroscopio se ha girado hacia la izquierda
}else{
    if(ry < (-1)*Variables.sensibilityValue) {
        System.out.println("sensibilityValue = "+(-Variables.sensibilityValue));
        System.out.println("enviando movimiento del sensor GyroscopeYDOWN = " + ry);
        //SendMessageBody("GyroscopeYDOWN:" + ry);//el giroscopio se ha girado hacia la derecha
    }
}
*/