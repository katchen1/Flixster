package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.example.flixster.databinding.ActivityGenreFilterBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenreFilterActivity extends AppCompatActivity {

    List<Integer> selectedGenres;
    List<CheckBox> cbGenres;
    CheckBox cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view (apply the view binding library to reduce view boilerplate)
        ActivityGenreFilterBinding binding = ActivityGenreFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Genre Filter");

        // Retrieve the selected genres passed in from the main activity
        selectedGenres = getIntent().getIntegerArrayListExtra("genres");

        // Get list of all genres ids and sort them alphabetically by corresponding genre names
        Map<Integer, String> map = MainActivity.genreIdToString;
        List<Integer> allGenres = new ArrayList<>(map.keySet());
        allGenres.sort((id1, id2) -> map.get(id1).compareTo(map.get(id2)));

        // Create a checkbox for each genre
        cbGenres = new ArrayList<>();
        for (Integer id: allGenres) {
            CheckBox cb = new CheckBox(this);
            cb.setTextSize(22);
            cb.setId(id);
            cb.setText(MainActivity.genreIdToString.get(id));
            cb.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
            cb.setPadding(15, 15, 15, 15);
            if (selectedGenres.contains(id)) cb.setChecked(true);
            binding.genreList.addView(cb);
            cbGenres.add(cb);
        }

        // Check the "All" checkbox only if all genres are selected
        cbAll = binding.cbAll;
        cbAll.setChecked(selectedGenres.containsAll(allGenres));
    }

    /* After the user clicks update, pass the edited genre selections to the main activity. */
    public void updateOnClick(View view) {
        // Fetch the newly selected genres
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

    /* If the user checks/unchecks the "All" checkbox, check/uncheck all genres on the screen. */
    public void cbAllOnClick(View view) {
        boolean state = cbAll.isChecked();
        for (CheckBox cb: cbGenres) cb.setChecked(state);
    }
}