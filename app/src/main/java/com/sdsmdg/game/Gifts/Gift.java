package com.sdsmdg.game.Gifts;

import java.util.Random;

/**
 * Created by Rahul Yadav on 6/12/2016.
 */
public class Gift {

    //TODO Add ball velocity in gift.

    private static int[] gift = new int[]{1, 2};

    public static int getGift() {
        return (gift[new Random().nextInt(gift.length)]);
    }

    public static void showGift() {
        switch (getGift()) {
            case 1:
                BoardSize.start();
                break;
            case 2:
                BallSize.start();
                break;
            case 3:
                BallVelocity.start();
                break;
        }
    }
}
