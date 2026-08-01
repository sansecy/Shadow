package com.tencent.shadow.sample.plugin.app.lib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_plugin2 = findViewById(R.id.tv_plugin2);
        tv_plugin2.setText("tv_plugin8");
    }
}