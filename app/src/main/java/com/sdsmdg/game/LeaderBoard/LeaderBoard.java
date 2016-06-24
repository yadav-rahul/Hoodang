package com.sdsmdg.game.LeaderBoard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sdsmdg.game.LeaderBoard.API.dbapi;
import com.sdsmdg.game.LeaderBoard.model.Scores;
import com.sdsmdg.game.R;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LeaderBoard extends AppCompatActivity {
    private final String ROOT_URL = "http://rauhly247.pythonanywhere.com/";
    List<Scores> scoresList;
    ListView scoreListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        scoreListView = (ListView) findViewById(R.id.scoreListView);
        getScores();
    }


    public void getScores() {
        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL)
                .build();

        dbapi api = adapter.create(dbapi.class);
        api.getScores(new Callback<List<Scores>>() {
            @Override
            public void success(List<Scores> scores, Response response) {
                loading.dismiss();
                scoresList = scores;
                showList();
            }

            @Override
            public void failure(RetrofitError error) {
                //Handle the errors here
            }
        });
    }

    public void showList() {
        String[] scores = new String[scoresList.size()];

        ArrayAdapter adapter = new CustomAdapter(this, scoresList);
        scoreListView.setAdapter(adapter);
    }
}
