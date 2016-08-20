package com.sdsmdg.game.Gifts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
            case R.drawable.speed_gift:
                BallVelocity.start();
                break;
            case R.drawable.size_gift:{
                switch ((gift[new Random().nextInt(gift.length - 1)])){
                    case 1:
                        BallSize.start();
                        break;
                    case 2:
                        BoardSize.start();
                        break;
                }
            }
            case R.drawable.multi_ball_gift:

                break;
        }
    }


    //Get the image type of gift and relative context
    public static int getTypeOfGitf() {
        switch (getGift()) {
            case 1:
                return R.drawable.speed_gift;
            case 2:
                return R.drawable.size_gift;
            case 3:
                return R.drawable.multi_ball_gift;
        }
        return 0;
    }
}
