package com.example.pilem.ui.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.MovieEntity;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_POSTER = "extra_poster";

    private int movieId;
    private String movieTitle;
    private String moviePoster;
    private boolean isFavorite = false;
    private Button btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView ivPoster = findViewById(R.id.iv_detail_poster);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        btnFavorite = findViewById(R.id.btn_favorite);

        movieId = getIntent().getIntExtra(EXTRA_ID, 0);
        movieTitle = getIntent().getStringExtra(EXTRA_TITLE);
        moviePoster = getIntent().getStringExtra(EXTRA_POSTER);

        tvTitle.setText(movieTitle);
        String imageUrl = "https://image.tmdb.org/t/p/w500" + moviePoster;
        Glide.with(this).load(imageUrl).into(ivPoster);

        checkFavoriteStatus();

        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void checkFavoriteStatus() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MovieEntity movie = AppDatabase.getDatabase(this).movieDao().getMovieById(movieId);
            isFavorite = movie != null;
            runOnUiThread(() -> {
                if (isFavorite) {
                    btnFavorite.setText("Remove from Favorite");
                } else {
                    btnFavorite.setText("Add to Favorite");
                }
            });
        });
    }

    private void toggleFavorite() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MovieEntity movie = new MovieEntity(movieId, movieTitle, moviePoster);
            if (isFavorite) {
                AppDatabase.getDatabase(this).movieDao().delete(movie);
                isFavorite = false;
            } else {
                AppDatabase.getDatabase(this).movieDao().insert(movie);
                isFavorite = true;
            }
            runOnUiThread(() -> {
                if (isFavorite) {
                    btnFavorite.setText("Remove from Favorite");
                    Toast.makeText(this, "Added to Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    btnFavorite.setText("Removed from Favorite");
                    Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
