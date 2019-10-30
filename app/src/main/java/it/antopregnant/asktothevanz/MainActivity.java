package it.antopregnant.asktothevanz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rw = findViewById(R.id.rw);
        rw.setLayoutManager(new GridLayoutManager(this, 3));
        rw.setAdapter(new SoundBoardAdapter(getApplicationContext()));
    }
}
