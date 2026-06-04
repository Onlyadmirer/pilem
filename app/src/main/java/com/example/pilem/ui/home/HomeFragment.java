package com.example.pilem.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMovies = view.findViewById(R.id.rv_movies);
        progressBar = view.findViewById(R.id.pb_home);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRefresh = view.findViewById(R.id.btn_refresh);

        adapter = new MovieAdapter();
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMovies.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> loadMovies());

        loadMovies();
    }

    private void loadMovies() {
        showLoading(true);
        showError(false);

        RetrofitClient.getApiService().getPopularMovies("en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setMovies(response.body().getResults());
                    showError(false);
                } else {
                    showError(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showError(true);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvMovies.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(boolean isError) {
        errorLayout.setVisibility(isError ? View.VISIBLE : View.GONE);
        if (isError) {
            rvMovies.setVisibility(View.GONE);
        }
    }
}
