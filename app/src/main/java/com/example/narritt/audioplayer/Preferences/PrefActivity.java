package com.example.narritt.audioplayer.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.narritt.audioplayer.PlayerActivity;
import com.example.narritt.audioplayer.PlaylistsActivity;
import com.example.narritt.audioplayer.R;

import java.util.ArrayList;

public class PrefActivity extends Activity {
    ListView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        menu = findViewById(R.id.prefmenu);
        menu.setOnItemClickListener(listener);

        ArrayList<String> menuItems = new ArrayList<>();
        menuItems.add(getString(R.string.pref_equalizer));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems);
        menu.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    Intent intent = new Intent(PrefActivity.this, PrefEqualizerActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public void btnBackToPlayerClick(View view){
        finish();
    }
}
