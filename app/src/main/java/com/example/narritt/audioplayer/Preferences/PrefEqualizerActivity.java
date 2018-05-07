package com.example.narritt.audioplayer.Preferences;

import android.media.audiofx.Equalizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.narritt.audioplayer.R;
import com.example.narritt.audioplayer.misc.PlayerCurrentState;

public class PrefEqualizerActivity extends AppCompatActivity {
    private static final String TAG = "MyAudioPlayer";
    CheckBox chbEqual;
    SeekBar[] sb = new SeekBar[5];
    TextView[] tw = new TextView[5];
    Equalizer equalizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_equalizer);
        findViews();

        equalizer = PlayerCurrentState.get_instance().getEqualizer();
        chbEqual.setChecked(equalizer.getEnabled());
        if ( chbEqual.isChecked() )
            for(SeekBar seekBar : sb)
                seekBar.setEnabled(true);
        else
            for(SeekBar seekBar : sb)
                seekBar.setEnabled(false);
        chbEqual.setOnCheckedChangeListener(checkBoxChangeListener);

        final short numberFrequencyBands    = equalizer.getNumberOfBands();
        final short numberOfPresets         = equalizer.getNumberOfPresets();
        final short lowerEqualizerBandLvl   = equalizer.getBandLevelRange()[0];
        final short upperEqualizerBandLvl   = equalizer.getBandLevelRange()[1];

        Log.i(TAG, "LOWER: " + lowerEqualizerBandLvl + "; UPPER: " + upperEqualizerBandLvl);

        //SeekBars
        for (short i = 0; i < sb.length; i++) {
            final short bandIndex = i;
            sb[i].setMax(upperEqualizerBandLvl - lowerEqualizerBandLvl);
            sb[i].setProgress(equalizer.getBandLevel(bandIndex) + 1500);    //wtf lowerEqualizerBandLvl is not working
            Log.i(TAG, "Band level of " + bandIndex + " band: " + equalizer.getBandLevel(bandIndex));
            sb[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    equalizer.setBandLevel(bandIndex, (short) (progress + lowerEqualizerBandLvl));
                    Log.i(TAG, "Band level of " + bandIndex + " band changed: " +  (progress + lowerEqualizerBandLvl));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        //TextViews frequency change
        for (short i = 0; i < 5; i++) {
            int freq = equalizer.getCenterFreq(i) / 1000;
            if ((freq % 100) == 0)
                tw[i].setText((float)freq / 1000 + " kHz");
            else
                tw[i].setText(freq + " Hz");
        }
    }

    private void findViews(){
        chbEqual = findViewById(R.id.chb_pref_equalizer);
        sb[0] = findViewById(R.id.seekBar1);
        sb[1] = findViewById(R.id.seekBar2);
        sb[2] = findViewById(R.id.seekBar3);
        sb[3] = findViewById(R.id.seekBar4);
        sb[4] = findViewById(R.id.seekBar5);
        tw[0] = findViewById(R.id.pref_equalizer_1);
        tw[1] = findViewById(R.id.pref_equalizer_2);
        tw[2] = findViewById(R.id.pref_equalizer_3);
        tw[3] = findViewById(R.id.pref_equalizer_4);
        tw[4] = findViewById(R.id.pref_equalizer_5);
    }

    public void btnBackToPlayerClick(View view){
        finish();
    }

    //LISTENERS
    OnCheckedChangeListener checkBoxChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if ( isChecked ) {
                equalizer.setEnabled(true);
                for(SeekBar seekBar : sb)
                    seekBar.setEnabled(true);
            }
            else {
                equalizer.setEnabled(false);
                for(SeekBar seekBar : sb)
                    seekBar.setEnabled(false);
            }
        }
    };

}
