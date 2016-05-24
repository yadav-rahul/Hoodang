package com.sdsmdg.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sdsmdg.game.Bluetooth.MainActivity;

public class Launcher extends AppCompatActivity implements View.OnClickListener {

    public String TAG = "com.sdsmdg.game";
    Button sP, mP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sP = (Button) findViewById(R.id.singlePlayerButton);
        mP = (Button) findViewById(R.id.multiPlayerButton);
        sP.setOnClickListener(this);
        mP.setOnClickListener(this);
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

}
