package com.example.narritt.audioplayer;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

public class PrefActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
    }

    public void btnBackToPlayerClick(View view){
        finish();
    }
}
