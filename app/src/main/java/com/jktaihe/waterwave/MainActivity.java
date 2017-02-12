package com.jktaihe.waterwave;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jktaihe.wwview.WaterWaveView;

public class MainActivity extends AppCompatActivity {

    WaterWaveView waterWaveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWaveProgress();
    }

    public void initWaveProgress() {
        waterWaveProgress = (WaterWaveView) findViewById(R.id.wwv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waterWaveProgress.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        waterWaveProgress.stop();
    }
}
