package com.sdsmdg.game.Gifts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sdsmdg.game.GameWorld.SinglePlayerView;
import com.sdsmdg.game.R;

import java.util.Random;

/**
 * Created by Rahul Yadav on 6/12/2016.
 */
public class Gift {
    private static int[] gift = new int[]{1, 2, 3};

    public static int getGift() {
        return (gift[new Random().nextInt(gift.length)]);
    }

    public static void showGift(int typeOfGift) {
        switch (typeOfGift) {
            case 1:
                BallVelocity.start();
                break;
            case 2: {
                switch (new Random().nextInt(2) + 1) {
                    case 1:
                        BallSize.start();
                        break;
                    case 2:
                        BoardSize.start();
                        break;
                }
                break;
            }
            case 3:
                SinglePlayerView.xSecondBallCenter = SinglePlayerView.xBallCenter;
                SinglePlayerView.ySecondBallCenter = SinglePlayerView.yBallCenter;
                SinglePlayerView.showSecondBall = true;
                break;
        }
    }


    //Get the image type of gift and relative context
    public static int getTypeOfGift() {
        return getGift();
    }
}
