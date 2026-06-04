package com.example.pilem.ui.home;

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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pilem.R;
import com.example.pilem.data.model.MovieResponse;
import com.example.pilem.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MovieAdapter popularAdapter, topRatedAdapter, upcomingAdapter, searchAdapter;
    private RecyclerView rvPopular, rvTopRated, rvUpcoming, rvSearch;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showSearchView(false);
                }
                return true;
            }
        });

        btnRefresh.setOnClickListener(v -> loadAllMovies());

        loadAllMovies();
    }

    private void initViews(View view) {
        searchView = view.findViewById(R.id.search_view);
        nestedScrollView = view.findViewById(R.id.scroll_view_home);
        rvPopular = view.findViewById(R.id.rv_popular);
        rvTopRated = view.findViewById(R.id.rv_top_rated);
        rvUpcoming = view.findViewById(R.id.rv_upcoming);
        // We reuse the hidden vertically scrolling RV for search if needed, but for now let's focus on horizontal rows
        progressBar = view.findViewById(R.id.pb_home);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRefresh = view.findViewById(R.id.btn_refresh);
    }

    private void setupRecyclerViews() {
        popularAdapter = new MovieAdapter();
        topRatedAdapter = new MovieAdapter();
        upcomingAdapter = new MovieAdapter();

        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTopRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvPopular.setAdapter(popularAdapter);
        rvTopRated.setAdapter(topRatedAdapter);
        rvUpcoming.setAdapter(upcomingAdapter);
    }

    private void loadAllMovies() {
        showLoading(true);
        showError(false);

        loadPopular();
        loadTopRated();
        loadUpcoming();
    }

    private void loadPopular() {
        RetrofitClient.getApiService().getPopularMovies("en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    popularAdapter.setMovies(response.body().getResults());
                    checkLoadingComplete();
                } else {
                    showError(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showError(true);
            }
        });
    }

    private void loadTopRated() {
        RetrofitClient.getApiService().getTopRatedMovies("en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topRatedAdapter.setMovies(response.body().getResults());
                    checkLoadingComplete();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadUpcoming() {
        RetrofitClient.getApiService().getUpcomingMovies("en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upcomingAdapter.setMovies(response.body().getResults());
                    checkLoadingComplete();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {}
        });
    }

    private void performSearch(String query) {
        showLoading(true);
        RetrofitClient.getApiService().searchMovies(query, "en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    // For Netflix style, maybe search results replace the popular row or show in a separate view.
                    // For now, let's just update the popular row as "Results" or similar logic.
                    // Ideally we'd have a search results fragment or view.
                    popularAdapter.setMovies(response.body().getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showLoading(false);
            }
        });
    }

    private void checkLoadingComplete() {
        // Simple check: if at least popular is loaded, we show the UI
        if (popularAdapter.getItemCount() > 0) {
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        nestedScrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(boolean isError) {
        errorLayout.setVisibility(isError ? View.VISIBLE : View.GONE);
        if (isError) {
            nestedScrollView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showSearchView(boolean isSearching) {
        // Logic to toggle between search results and categorised view
        if (!isSearching) {
            loadAllMovies();
        }
    }
}
