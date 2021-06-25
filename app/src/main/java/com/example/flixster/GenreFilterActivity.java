package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenreFilterActivity extends AppCompatActivity {

    List<Integer> selectedGenres;
    List<CheckBox> cbGenres;
    CheckBox cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_filter);
        getSupportActionBar().setTitle("Genre Filter");

        Map<Integer, String> map = MainActivity.genreIdToString;
        selectedGenres = getIntent().getIntegerArrayListExtra("genres");
        LinearLayout layout = findViewById(R.id.genreList);
        cbGenres = new ArrayList<>();
        cbAll = findViewById(R.id.cbAll);

        List<Integer> allGenres = new ArrayList<>(map.keySet());
        allGenres.sort((id1, id2) -> map.get(id1).compareTo(map.get(id2)));

        for (Integer id: allGenres) {
            CheckBox cb = new CheckBox(this);
            cb.setTextSize(22);
            cb.setId(id);
            cb.setText(MainActivity.genreIdToString.get(id));
            cb.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
            cb.setPadding(15, 15, 15, 15);
            if (selectedGenres.contains(id)) cb.setChecked(true);
            layout.addView(cb);
            cbGenres.add(cb);
        }

        cbAll.setChecked(selectedGenres.containsAll(allGenres));
    }

    public void updateOnClick(View view) {
        selectedGenres.clear();
        for (CheckBox cb: cbGenres) {
            if (cb.isChecked()) selectedGenres.add(cb.getId());
        }

        // Pass the results of editing
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra(MainActivity.KEY_GENRE_FILTER,
                (ArrayList<Integer>) selectedGenres);

        // Set the result of the intent, finish activity, close the screen and go back
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cbAllOnClick(View view) {
        boolean state = cbAll.isChecked();
        for (CheckBox cb: cbGenres) cb.setChecked(state);
    }
}