package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ContentActivity extends AppCompatActivity {
    TextView mTextView;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        content=(String)getIntent().getStringExtra("content");
        mTextView=(TextView)findViewById(R.id.tv_content);
        mTextView.setText(content);
        System.out.println(content);
    }
}
