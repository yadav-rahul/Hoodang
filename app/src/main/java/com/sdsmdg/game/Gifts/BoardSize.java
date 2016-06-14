package com.sdsmdg.game.Gifts;

import android.util.Log;

import com.sdsmdg.game.GameWorld.SinglePlayer;
import com.sdsmdg.game.GameWorld.SinglePlayerView;

import java.util.Random;

/**
 * Created by Rahul Yadav on 6/12/2016.
 */
public class BoardSize {
    private static int[] size = new int[]{4, 5, 6};

    public static void start() {
        Log.i("com.sdsmdg.game", "Board Size changed");
        SinglePlayerView.boardWidth1 = (SinglePlayer.width) / (size[new Random().nextInt(size.length)]);
        
    }

}