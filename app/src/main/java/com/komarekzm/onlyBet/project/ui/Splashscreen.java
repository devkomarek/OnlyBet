package com.komarekzm.onlyBet.project.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.ui.activities.Main2Activity;

public class Splashscreen extends Activity {
    Thread splashTread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        startAnimations();
    }

    private void startAnimations() {
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Splashscreen.this,
                            Main2Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {

                } finally {
                    finish();
                }
            }
        };
        splashTread.start();

    }

}
