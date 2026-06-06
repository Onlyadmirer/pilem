package com.example.pilem.ui.watchlist;

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

public class WatchlistFragment extends Fragment {

    private RecyclerView rvWatchlist;
    private TextView tvNoData;
    private TextView tvWatchlistCount;
    private MovieAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvWatchlist = view.findViewById(R.id.rv_watchlist);
        tvNoData = view.findViewById(R.id.tv_no_data);
        tvWatchlistCount = view.findViewById(R.id.tv_watchlist_count);

        adapter = new MovieAdapter();
        rvWatchlist.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvWatchlist.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWatchlist();
    }

    private void loadWatchlist() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<MovieEntity> entities = AppDatabase.getDatabase(requireContext()).movieDao().getAllWatchlistMovies();
            
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
                    tvWatchlistCount.setText(String.format(Locale.getDefault(), "Terdapat %d film tersimpan", count));

                    if (movies.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                        rvWatchlist.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        rvWatchlist.setVisibility(View.VISIBLE);
                        adapter.setMovies(movies);
                    }
                });
            }
        });
    }
}
