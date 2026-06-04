package com.example.pilem.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.model.Movie;
import com.example.pilem.ui.detail.DetailActivity;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies = new ArrayList<>();

    public void setMovies(List<Movie> movieList) {
        movies.clear();
        if (movieList != null) {
            movies.addAll(movieList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPoster;
        private final TextView tvTitle;
        private final TextView tvReleaseDate;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_item_poster);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvReleaseDate = itemView.findViewById(R.id.tv_item_release_date);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie movie = movies.get(position);
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_ID, movie.getId());
                    intent.putExtra(DetailActivity.EXTRA_TITLE, movie.getTitle());
                    intent.putExtra(DetailActivity.EXTRA_POSTER, movie.getPosterPath());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvReleaseDate.setText(movie.getReleaseDate());
            String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPoster);
        }
    }
}
