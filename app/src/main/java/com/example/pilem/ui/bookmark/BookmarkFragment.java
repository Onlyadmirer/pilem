package com.example.pilem.ui.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.MovieEntity;
import com.example.pilem.data.model.Movie;
import com.example.pilem.ui.home.MovieAdapter;
import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {

    private RecyclerView rvBookmark;
    private TextView tvNoData;
    private MovieAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBookmark = view.findViewById(R.id.rv_bookmark);
        tvNoData = view.findViewById(R.id.tv_no_data);

        adapter = new MovieAdapter();
        // Menggunakan GridLayoutManager dengan 2 kolom untuk tampilan yang lebih modern
        rvBookmark.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvBookmark.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarks();
    }

    private void loadBookmarks() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MovieEntity> entities = AppDatabase.getDatabase(requireContext()).movieDao().getAllBookmarkedMovies();
            
            // Konversi MovieEntity ke Movie agar bisa menggunakan adapter yang sama
            List<Movie> movies = new ArrayList<>();
            for (MovieEntity entity : entities) {
                Movie movie = new Movie();
                movie.setId(entity.getId());
                movie.setTitle(entity.getTitle());
                movie.setPosterPath(entity.getPosterPath());
                // Fields lain opsional untuk list favorit
                movies.add(movie);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (movies.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                        rvBookmark.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        rvBookmark.setVisibility(View.VISIBLE);
                        adapter.setMovies(movies);
                    }
                });
            }
        });
    }
}
