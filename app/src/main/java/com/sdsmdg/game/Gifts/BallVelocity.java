package com.sdsmdg.game.Gifts;

import android.util.Log;

import com.sdsmdg.game.GameWorld.SinglePlayerView;

/**
 * Created by Rahul Yadav on 6/12/2016.
 */
public class BallVelocity {

    public static void start() {
        Log.i("com.sdsmdg.game", "Ball Velocity changed !");
        SinglePlayerView.vBallX = (SinglePlayerView.vBallX) * 8 / 10;
        SinglePlayerView.vBallY = (SinglePlayerView.vBallY) * 8 / 10;
    }
}
