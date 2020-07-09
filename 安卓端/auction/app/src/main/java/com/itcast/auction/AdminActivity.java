package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;


public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    //未拍卖
    static TextView tv_1;
    //正在拍卖
    static TextView tv_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        tv_1 = (TextView) findViewById(R.id.textView4);
        tv_2 = (TextView) findViewById(R.id.textView2);
        getdata();
    }


    public void Click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.admin_1:
                System.out.println("123");
                break;
            case R.id.admin_2:
                System.out.println("456");
                break;
            case R.id.wd_2:
                intent = new Intent(AdminActivity.this, CreateAuctionActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
            case  R.id.wd_3:
                intent = new Intent(AdminActivity.this, UserManagementActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
            case  R.id.wd_4:
                intent = new Intent(AdminActivity.this, FinancialManagementActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                break;
        }
    }

    protected static synchronized void getdata() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/Auction_getCount/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int count1 = response.getInt("count1");
                    tv_1.setText(Integer.toString(count1));
                    int count2 = response.getInt("count2");
                    tv_2.setText(Integer.toString(count2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quit:
                DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                String sql = "delete from user ";
                db.execSQL(sql);
                db.close();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
