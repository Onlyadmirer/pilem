package com.example.pilem.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.model.Movie;
import com.example.pilem.data.model.MovieResponse;
import com.example.pilem.data.remote.RetrofitClient;
import com.example.pilem.ui.detail.DetailActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MovieAdapter popularAdapter, topRatedAdapter, upcomingAdapter;
    private RecyclerView rvPopular, rvTopRated, rvUpcoming;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;
    private ImageView ivSearchHome;
    private TextView tvSeeAllPopular, tvSeeAllTopRated, tvSeeAllUpcoming;

    // Hero Section Views
    private CardView cvHero;
    private ImageView ivHeroBackdrop;
    private TextView tvHeroTitle, tvHeroRating;
    private Button btnHeroDetail;

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
        setupClickListeners();

        loadAllMovies();
    }

    private void initViews(View view) {
        ivSearchHome = view.findViewById(R.id.iv_search_home);
        nestedScrollView = view.findViewById(R.id.scroll_view_home);
        rvPopular = view.findViewById(R.id.rv_popular);
        rvTopRated = view.findViewById(R.id.rv_top_rated);
        rvUpcoming = view.findViewById(R.id.rv_upcoming);
        progressBar = view.findViewById(R.id.pb_home);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        tvSeeAllPopular = view.findViewById(R.id.tv_see_all_popular);
        tvSeeAllTopRated = view.findViewById(R.id.tv_see_all_top_rated);
        tvSeeAllUpcoming = view.findViewById(R.id.tv_see_all_upcoming);

        // Hero Section
        cvHero = view.findViewById(R.id.cv_hero);
        ivHeroBackdrop = view.findViewById(R.id.iv_hero_backdrop);
        tvHeroTitle = view.findViewById(R.id.tv_hero_title);
        tvHeroRating = view.findViewById(R.id.tv_hero_rating);
        btnHeroDetail = view.findViewById(R.id.btn_hero_detail);
        
        cvHero.setVisibility(View.GONE);
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

    private void setupClickListeners() {
        ivSearchHome.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.navigation_explore)
        );

        btnRefresh.setOnClickListener(v -> loadAllMovies());

        tvSeeAllPopular.setOnClickListener(v -> navigateToSeeAll("popular"));
        tvSeeAllTopRated.setOnClickListener(v -> navigateToSeeAll("top_rated"));
        tvSeeAllUpcoming.setOnClickListener(v -> navigateToSeeAll("upcoming"));
    }

    private void navigateToSeeAll(String category) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        Navigation.findNavController(requireView()).navigate(R.id.action_navigation_home_to_seeAllFragment, bundle);
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
                    List<Movie> movies = response.body().getResults();
                    if (movies != null && !movies.isEmpty()) {
                        setupHeroSection(movies.get(0));
                        List<Movie> remainingMovies = new ArrayList<>(movies);
                        remainingMovies.remove(0);
                        // Limit to 5 items
                        List<Movie> limitedList = remainingMovies.subList(0, Math.min(remainingMovies.size(), 5));
                        popularAdapter.setMovies(limitedList);
                    }
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

    private void setupHeroSection(Movie movie) {
        cvHero.setVisibility(View.VISIBLE);
        tvHeroTitle.setText(movie.getTitle());
        tvHeroRating.setText(String.format(Locale.US, "⭐ %.1f", movie.getVoteAverage()));

        String imageUrl = movie.getBackdropPath() != null ? movie.getBackdropPath() : movie.getPosterPath();
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780" + imageUrl)
                .placeholder(android.R.color.darker_gray)
                .into(ivHeroBackdrop);

        View.OnClickListener detailClickListener = v -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ID, movie.getId());
            intent.putExtra(DetailActivity.EXTRA_TITLE, movie.getTitle());
            intent.putExtra(DetailActivity.EXTRA_POSTER, movie.getPosterPath());
            startActivity(intent);
        };

        cvHero.setOnClickListener(detailClickListener);
        btnHeroDetail.setOnClickListener(detailClickListener);
    }

    private void loadTopRated() {
        RetrofitClient.getApiService().getTopRatedMovies("en-US", 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    if (movies != null) {
                        List<Movie> limitedList = movies.subList(0, Math.min(movies.size(), 5));
                        topRatedAdapter.setMovies(limitedList);
                    }
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
                    List<Movie> movies = response.body().getResults();
                    if (movies != null) {
                        List<Movie> limitedList = movies.subList(0, Math.min(movies.size(), 5));
                        upcomingAdapter.setMovies(limitedList);
                    }
                    checkLoadingComplete();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {}
        });
    }

    private void checkLoadingComplete() {
        if (popularAdapter.getItemCount() > 0 || cvHero.getVisibility() == View.VISIBLE) {
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
}
