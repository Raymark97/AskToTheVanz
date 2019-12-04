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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    String sharePath;
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

    public void share() {
        String d[] = sharePath.split("/");
        String extPath = Environment.getExternalStorageDirectory() + "/Sounds/" + d[1] + ".mp3";
        File f = new File(extPath);
        try {
            InputStream is = main.getResources().openRawResource(toShare);
            OutputStream os = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, lengthRead);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(extPath);
        Uri uri = Uri.parse(extPath);
        System.out.println(uri);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/mp3");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        main.startActivity(Intent.createChooser(share, "Condividi file audio"));
    }

    public void share2() {
        String d[] = sharePath.split("/");
        File source = new File("android.resource://" + main.getPackageName() + "/raw/", d[1]);
        File dest = context.getExternalFilesDir(null);
        try {
            InputStream is = new FileInputStream(source);
            OutputStream os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, lengthRead);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sharePath);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(dest));
        main.startActivity(Intent.createChooser(share, "Condividi audio"));

    }

    public void share3() {
        String d[] = sharePath.split("/");
        String extPath = Environment.getExternalStorageDirectory() + "/Sounds/" + d[1] + ".mp3";
        File f = new File(extPath);
        Uri uri = Uri.parse(extPath);
        context.grantUriPermission("it.antopregnant.asktothevanz", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            InputStream is = main.getResources().openRawResource(toShare);
            OutputStream os = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, lengthRead);
                os.flush();
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        main.startActivity(Intent.createChooser(share, "Condividi audio"));

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
            sharePath = main.getResources().getResourceName(sounds.get(position));
            toShare = sounds.get(position);
            return true;
        });
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
        switch (menuItem.getItemId()) {
            case R.id.share:
                main.runOnUiThread(this::share3);
                System.out.println("Ci passa per davvero");
                break;
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
