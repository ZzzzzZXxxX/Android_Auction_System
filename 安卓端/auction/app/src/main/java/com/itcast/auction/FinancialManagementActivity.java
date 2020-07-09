package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FinancialManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_management);
    }
    public void Click(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.success:
                intent = new Intent(FinancialManagementActivity.this, SuccessRecordActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
            case R.id.unsuccessful:
                intent = new Intent(FinancialManagementActivity.this, UnsuccessfulRecordActivityActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
            case R.id.statistics:
                intent = new Intent(FinancialManagementActivity.this, StatisticsActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
        }
    }
}
