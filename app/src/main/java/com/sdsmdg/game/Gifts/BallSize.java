package com.sdsmdg.game.Gifts;

import android.util.Log;

import com.sdsmdg.game.GameWorld.SinglePlayerView;
import com.sdsmdg.game.Launcher;

import java.util.Random;

/**
 * Created by Rahul Yadav on 6/12/2016.
 */
public class BallSize {
    private static int[] radius = new int[]{15, 20, 25, 30, 35};

    public static void start() {
        Log.i("com.sdsmdg.game", "Ball Size changed !");
        SinglePlayerView.radius = (Launcher.width) / (radius[new Random().nextInt(radius.length)]);

    }
}
