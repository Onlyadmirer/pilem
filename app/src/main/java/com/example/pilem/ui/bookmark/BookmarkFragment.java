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
import java.util.Locale;

public class BookmarkFragment extends Fragment {

    private RecyclerView rvBookmark;
    private TextView tvNoData;
    private TextView tvBookmarkCount;
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
        tvBookmarkCount = view.findViewById(R.id.tv_bookmark_count);

        adapter = new MovieAdapter();
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
            
            List<Movie> movies = new ArrayList<>();
            for (MovieEntity entity : entities) {
                Movie movie = new Movie();
                movie.setId(entity.getId());
                movie.setTitle(entity.getTitle());
                movie.setPosterPath(entity.getPosterPath());
                movies.add(movie);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    int count = movies.size();
                    tvBookmarkCount.setText(String.format(Locale.getDefault(), "Terdapat %d film tersimpan", count));

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
