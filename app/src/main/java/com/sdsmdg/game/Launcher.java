package com.sdsmdg.game;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.game.Bluetooth.Bluetooth;
import com.sdsmdg.game.GameWorld.MultiPlayer;
import com.sdsmdg.game.GameWorld.SinglePlayer;

public class Launcher extends AppCompatActivity implements View.OnClickListener {


    public static long startTime;
    public static boolean isDialog = false;
    public static int winner = 1;
    public static int height, width;
    public String TAG = "com.sdsmdg.game";
    Button sP, mP;
    ImageView left, right;
    MultiPlayer multiPlayer = new MultiPlayer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        left = (ImageView) findViewById(R.id.left_image);
        right = (ImageView) findViewById(R.id.right_image);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sP = (Button) findViewById(R.id.singlePlayerButton);
        mP = (Button) findViewById(R.id.multiPlayerButton);
        sP.setOnClickListener(this);
        mP.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singlePlayerButton: {
                Intent i = new Intent(getApplicationContext(), SinglePlayer.class);
                Log.i(TAG, "SP Button clicked");
                startActivity(i);
                break;
            }
            case R.id.multiPlayerButton: {
                Intent i = new Intent(getApplicationContext(), Bluetooth.class);
                Log.i(TAG, "MP Button clicked");
                startActivity(i);
                break;
            }
        }
    }

    public void infoClicked(View view) {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_instructions);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        left.setAnimation(animation);
        right.setAnimation(animation);
        dialog(isDialog);
    }

    public void dialog(boolean check) {
        if (check) {
            long finalTime = (System.currentTimeMillis()) / 1000;
            String result = "Your score is " + String.valueOf(finalTime - startTime);

            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.game_over_dialog);
            dialog.show();
            dialog.setCancelable(false);

            Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
            Button btn_no = (Button) dialog.findViewById(R.id.btn_no);

            TextView result_textView = (TextView) dialog.findViewById(R.id.result_textView);

            result_textView.setText(result);

            btn_yes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    isDialog = false;
                    dialog.dismiss();

                }
            });
            btn_no.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    System.exit(0);
                }
            });
        }

    }
}
