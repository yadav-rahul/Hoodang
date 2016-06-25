package com.sdsmdg.game.LeaderBoard.API;

import com.sdsmdg.game.LeaderBoard.model.Scores;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Rahul Yadav on 6/23/2016.
 */
public interface dbapi {

    @GET("/scores/?format=json")
    public void getScores(Callback<List<Scores>> cb);

    //Since Retrofit uses Gson by default, the FooRequest instances
    // will be serialized as JSON as the sole body of the request.
    @POST("/scores")
    public void postJson(@Body Scores scores,Callback<Scores> cb);
}
