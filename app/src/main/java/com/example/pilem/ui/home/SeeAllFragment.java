package com.example.pilem.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pilem.R;
import com.example.pilem.data.model.MovieResponse;
import com.example.pilem.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllFragment extends Fragment {

    private String category;
    private RecyclerView rvSeeAll;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private ImageButton btnBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_see_all, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadMovies();

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void initViews(View view) {
        rvSeeAll = view.findViewById(R.id.rv_see_all);
        progressBar = view.findViewById(R.id.pb_see_all);
        tvTitle = view.findViewById(R.id.tv_title_see_all);
        btnBack = view.findViewById(R.id.btn_back_see_all);

        if (category != null) {
            switch (category) {
                case "popular":
                    tvTitle.setText("Popular Movies");
                    break;
                case "top_rated":
                    tvTitle.setText("Top Rated Movies");
                    break;
                case "upcoming":
                    tvTitle.setText("Upcoming Movies");
                    break;
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        rvSeeAll.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvSeeAll.setAdapter(adapter);
    }

    private void loadMovies() {
        if (category == null) return;

        progressBar.setVisibility(View.VISIBLE);
        Call<MovieResponse> call;

        switch (category) {
            case "popular":
                call = RetrofitClient.getApiService().getPopularMovies("en-US", 1);
                break;
            case "top_rated":
                call = RetrofitClient.getApiService().getTopRatedMovies("en-US", 1);
                break;
            case "upcoming":
                call = RetrofitClient.getApiService().getUpcomingMovies("en-US", 1);
                break;
            default:
                progressBar.setVisibility(View.GONE);
                return;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setMovies(response.body().getResults());
                } else {
                    Toast.makeText(getContext(), "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
