package com.sdsmdg.game.LeaderBoard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdsmdg.game.LeaderBoard.model.Scores;
import com.sdsmdg.game.R;

import java.util.List;

/**
 * Created by Rahul Yadav on 6/23/2016.
 */
class CustomAdapter extends ArrayAdapter<Scores> {
    public CustomAdapter(Context context, List<Scores> scores) {
        super(context, R.layout.score_list, scores);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.score_list, parent, false);

        Scores score = getItem(position);
        TextView userName = (TextView) customView.findViewById(R.id.userName);
        TextView userScore = (TextView) customView.findViewById(R.id.userScore);

        userName.setText(score.getName());
        userScore.setText(score.getScore().toString());

        return customView;
    }
}
