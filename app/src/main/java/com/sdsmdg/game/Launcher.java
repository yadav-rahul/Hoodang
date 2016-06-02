package com.sdsmdg.game;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sdsmdg.game.Bluetooth.MainActivity;
import com.sdsmdg.game.GameWorld.GameWorld;

public class Launcher extends AppCompatActivity implements View.OnClickListener {


    public static boolean isDialog = false;
    public static int winner=1;
    public String TAG = "com.sdsmdg.game";
    Button sP, mP;
    GameWorld gameWorld = new GameWorld(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sP = (Button) findViewById(R.id.singlePlayerButton);
        mP = (Button) findViewById(R.id.multiPlayerButton);
        sP.setOnClickListener(this);
        mP.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singlePlayerButton: {
                Toast.makeText(Launcher.this, "Work in Progress", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.multiPlayerButton: {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Log.i(TAG, "MP Buttton clicked");
                startActivity(i);

                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog(isDialog);
    }

    public void dialog(boolean check) {
        if (check) {
            isDialog = false;
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.my_dialog);
            dialog.show();

            Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
            Button btn_no = (Button) dialog.findViewById(R.id.btn_no);

            btn_yes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(Launcher.this, Launcher.class);
                    startActivity(i);
                }
            });
            btn_no.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    System.exit(0);
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }
            });


        }

    }
}
