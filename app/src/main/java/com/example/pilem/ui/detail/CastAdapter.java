package com.example.pilem.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.model.Cast;
import java.util.ArrayList;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final List<Cast> castList = new ArrayList<>();

    public void setCastList(List<Cast> list) {
        castList.clear();
        if (list != null) {
            castList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        holder.bind(castList.get(position));
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProfile;
        private final TextView tvName;
        private final TextView tvCharacter;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_cast_profile);
            tvName = itemView.findViewById(R.id.tv_cast_name);
            tvCharacter = itemView.findViewById(R.id.tv_cast_character);
        }

        public void bind(Cast cast) {
            tvName.setText(cast.getName());
            tvCharacter.setText(cast.getCharacter());
            String imageUrl = "https://image.tmdb.org/t/p/w185" + cast.getProfilePath();
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivProfile);
        }
    }
}
