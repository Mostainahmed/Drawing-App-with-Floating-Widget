package com.mostain.neumetis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import top.defaults.colorpicker.ColorPickerPopup;
import yuku.ambilwarna.AmbilWarnaDialog;


public class CanvasActivity extends Activity{
    MyCanvas myCanvas;
    public int defaulColor;
    AlertDialog.Builder ab;
    AlertDialog alertDialog;
    private BroadcastReceiver receiver,receiverColor,receiverLineWidth;
    public SeekBar seekBar;
    public Float linewidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        myCanvas = findViewById(R.id.mycanvas);
        defaulColor = ContextCompat.getColor(this,R.color.colorPrimary);
        seekBar = findViewById(R.id.seekbar);
        seekBar.setMax(40);
    }

    public void onBackPressed() {
        ab = new AlertDialog.Builder(CanvasActivity.this);
        ab.setTitle("Confirm").
                setMessage("Are you sure you want to exit").
                setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = ab.create();
        alertDialog.show();
    }
    @Override
    protected void onResume(){
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(
                "mostain.steadfastworld.com.custombroadcastreceiver");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                myCanvas.clearCanvas();
            }
        };

        IntentFilter intentFilterOne = new IntentFilter(
                "color.neumetis.mostain");

        receiverColor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                openColorPicker();
            }
        };

        IntentFilter intentFilterTwo = new IntentFilter(
                "line.neumetis.mostain");

        receiverLineWidth = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lineWidth();
            }
        };

        this.registerReceiver(receiverLineWidth, intentFilterTwo);

        this.registerReceiver(receiverColor, intentFilterOne);

        this.registerReceiver(receiver, intentFilter);

    }
    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(this.receiver);
        unregisterReceiver(this.receiverColor);
        unregisterReceiver(this.receiverLineWidth);

    }

    public void lineWidth(){

        seekBar.setVisibility(View.VISIBLE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                linewidth = (float)progress;
                myCanvas.canvasLineWidth(linewidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setVisibility(View.GONE);
            }
        });

    }

    public void openColorPicker() {
        new ColorPickerPopup.Builder(this)
                .initialColor(defaulColor) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        Intent colorIntent = new Intent();
                        colorIntent.setAction("color.mostain.neumetis").putExtra("color", color);
                        colorIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        sendBroadcast(colorIntent);
                        myCanvas.changeColor(color);
                        defaulColor = color;
                    }

                    @Override
                    public void onColor(int color, boolean fromUser) {

                    }
                });
    }

}

