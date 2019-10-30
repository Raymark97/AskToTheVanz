package it.antopregnant.asktothevanz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    int PERMISSION;
    boolean authorized=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
            Intent intent = getIntent();
            startActivity(intent);
            finish();
        }else{
            authorized=true;
        }
        if(authorized) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Sounds");
            if (file.exists()) {
                File files[] = file.listFiles();
                for (File fi : files) {
                    fi.delete();
                }
            } else {
                file.mkdirs();
            }
        }
        RecyclerView rw = findViewById(R.id.rw);
        rw.setLayoutManager(new GridLayoutManager(this, 3));
        rw.setAdapter(new SoundBoardAdapter(getApplicationContext(), this));
    }
}
