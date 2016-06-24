package com.sdsmdg.game.LeaderBoard.API;

import com.sdsmdg.game.LeaderBoard.model.Scores;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Rahul Yadav on 6/23/2016.
 */
public interface dbapi {

    @GET("/scores/?format=json")
    public void getScores(Callback<List<Scores>> response);
}
