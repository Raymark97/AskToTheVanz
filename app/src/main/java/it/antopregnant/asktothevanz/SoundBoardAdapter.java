package it.antopregnant.asktothevanz;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SoundBoardAdapter extends RecyclerView.Adapter<SoundBoardAdapter.ViewHolder> implements ActionMode.Callback {

    List<Integer> sounds = new ArrayList<>();
    List<Drawable> images = new ArrayList<>();
    private Context context;
    private MediaPlayer player;
    ActionMode actionMode;
    MainActivity main;
    String sharepath;
    int toShare;

    public SoundBoardAdapter(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
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
            if (player != null && player.isPlaying()) player.stop();
            player = MediaPlayer.create(context, sounds.get(position));
            System.out.println(sounds.get(position));
            player.start();
        });
        holder.btn.setOnLongClickListener(view -> {
            if (actionMode != null) {
                actionMode = null;
                return false;
            }
            actionMode = main.startActionMode(SoundBoardAdapter.this);
            view.setSelected(true);
            actionMode = null;
            sharepath = main.getResources().getResourceName(sounds.get(position));
            toShare = sounds.get(position);
            return true;
        });
    }

    public void share(){
        System.out.println(sharepath);
        String s[] = sharepath.split("/");
        InputStream inputStream;
        FileOutputStream outputStream;
        File f;
        try{
            f = new File(Environment.getExternalStorageDirectory() + "/Sounds", "sound_" + s[1] + ".mp3");
            inputStream = main.getResources().openRawResource(toShare);
            outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[2048];
            int length;
            while((length = inputStream.read(buffer))>0){
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        }catch (IOException e){}
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/Sounds/sound_" + s[1] + ".mp3"));
        share.setType("audio/mp3");
        main.startActivity(Intent.createChooser(share, "Share audio file"));
    }

    @Override
    public int getItemCount() {
        return sounds.size();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.songs_menu_share, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.share:
                /*Metodo di condivisione*/
                share();
                actionMode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = (ImageButton) itemView;
        }
    }
}
