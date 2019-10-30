package it.antopregnant.asktothevanz;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SoundBoardAdapter extends RecyclerView.Adapter<SoundBoardAdapter.ViewHolder> {

    List<Integer> sounds = new ArrayList<>();
    List<Drawable> images = new ArrayList<>();
    private Context context;
    private MediaPlayer player;

    public SoundBoardAdapter(Context context) {
        this.context = context;

        try {
            List<Field> sounds = Arrays.asList(R.raw.class.getFields());

            for (Field f : sounds) {
                int id = f.getInt(sounds.indexOf(f));
                String sound = context.getResources().getResourceName(id);
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier(sound.split("/")[1], "drawable", context.getPackageName()));
                this.sounds.add(id);
                this.images.add(drawable);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public SoundBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageButton v = (ImageButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_button, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.btn.setImageDrawable(images.get(position));
        holder.btn.setOnClickListener(view -> {
            if(player != null && player.isPlaying()) player.stop();
            player = MediaPlayer.create(context, sounds.get(position));
            player.start();
        });
    }

    @Override
    public int getItemCount() {
        return sounds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = (ImageButton) itemView;
        }
    }
}
