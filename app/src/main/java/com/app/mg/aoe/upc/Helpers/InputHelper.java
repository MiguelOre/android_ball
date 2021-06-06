package com.app.mg.aoe.upc.Helpers;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class InputHelper {
    public static void Vibrate(Vibrator vibrator){
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
        }else {
            vibrator.vibrate(5);
        }
    }
}
