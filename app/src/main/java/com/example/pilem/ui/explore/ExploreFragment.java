package com.example.pilem.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pilem.R;
import com.example.pilem.data.model.MovieResponse;
import com.example.pilem.data.remote.RetrofitClient;
import com.example.pilem.ui.home.MovieAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private MovieAdapter exploreAdapter;
    private RecyclerView rvExplore;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private LinearLayout errorLayout;
    private Button btnRefresh;
    private SearchView searchView;
    private ChipGroup cgGenres;
    private String lastQuery = "";
    private String lastGenreId = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    lastQuery = query;
                    lastGenreId = "";
                    cgGenres.clearCheck();
                    performSearch(query);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && lastGenreId.isEmpty()) {
                    showEmptyState(true);
                    exploreAdapter.setMovies(null);
                }
                return true;
            }
        });

        cgGenres.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    String genreId = (String) chip.getTag();
                    lastGenreId = genreId;
                    lastQuery = "";
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    performGenreFilter(genreId);
                }
            } else {
                lastGenreId = "";
                if (lastQuery.isEmpty()) {
                    showEmptyState(true);
                    exploreAdapter.setMovies(null);
                }
            }
        });

        btnRefresh.setOnClickListener(v -> {
            if (!lastQuery.isEmpty()) {
                performSearch(lastQuery);
            } else if (!lastGenreId.isEmpty()) {
                performGenreFilter(lastGenreId);
            }
        });
    }

    private void initViews(View view) {
        searchView = view.findViewById(R.id.search_view_explore);
        cgGenres = view.findViewById(R.id.cg_genres);
        rvExplore = view.findViewById(R.id.rv_explore);
        progressBar = view.findViewById(R.id.pb_explore);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        errorLayout = view.findViewById(R.id.error_layout_explore);
        btnRefresh = view.findViewById(R.id.btn_refresh_explore);
    }

    private void setupRecyclerView() {
        exploreAdapter = new MovieAdapter();
        rvExplore.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvExplore.setAdapter(exploreAdapter);
    }

    private void performSearch(String query) {
        showLoading(true);
        showError(false);
        showEmptyState(false);

        RetrofitClient.getApiService().searchMovies(query, "en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showError(true);
            }
        });
    }

    private void performGenreFilter(String genreId) {
        showLoading(true);
        showError(false);
        showEmptyState(false);

        RetrofitClient.getApiService().discoverMoviesByGenre(genreId, "en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showError(true);
            }
        });
    }

    private void handleResponse(Response<MovieResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            exploreAdapter.setMovies(response.body().getResults());
            showLoading(false);
            if (exploreAdapter.getItemCount() == 0) {
                showEmptyState(true);
            }
        } else {
            showLoading(false);
            showError(true);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvExplore.setVisibility(isLoading ? View.GONE : (exploreAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE));
    }

    private void showEmptyState(boolean isShow) {
        layoutEmptyState.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (isShow) {
            rvExplore.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    private void showError(boolean isError) {
        errorLayout.setVisibility(isError ? View.VISIBLE : View.GONE);
        if (isError) {
            rvExplore.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}
