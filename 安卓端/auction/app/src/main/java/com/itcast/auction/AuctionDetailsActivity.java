package com.itcast.auction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.loopj.android.image.SmartImageView;
import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AuctionDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> key = new ArrayList<String>();
    List<String> value = new ArrayList<String>();
    String commodity_id;
    String commodity_name;
    String margin;
    String start_price;
    SmartImageView mImageView;
    WebView mWebView;
    TextView title;
    //条数
    int num = 0;
    TextView R_t_3;
    //返回的数据
    TextView NowPrice;
    Button bt_commit;

    //拍卖记录
    TextView R_t_1_1;
    TextView R_t_2_2;
    TextView R_t_3_3;
    TextView R_t_1_1_1;
    TextView R_t_2_2_2;
    TextView R_t_3_3_3;
    TextView R_t_1_1_1_1;
    TextView R_t_2_2_2_2;
    TextView R_t_3_3_3_3;
    //倒计时
    TextView LT;
    //起拍价，当前价
    TextView Text1;
    Boolean StopThread = false;
    Boolean StopThreadWait = false;
    Boolean WaitFlag = false;
    Boolean StartFlag = false;
    String user_id;
    //开始的倒计时，用来判断是否开始拍卖
    int ST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);
        String flag = getIntent().getStringExtra("flag");
        if (flag.equals("1")) {
            key = getIntent().getStringArrayListExtra("key");
            //[commodity_id, commodity_name, is_cd, margin, start_price]
            value = getIntent().getStringArrayListExtra("value");
            commodity_id = value.get(0);
            commodity_name = value.get(1);
            margin = value.get(3);
            start_price = value.get(4);
        } else if (flag.equals("2")) {
            commodity_id = getIntent().getStringExtra("commodity_id");
            commodity_name = getIntent().getStringExtra("commodity_name");
            margin = getIntent().getStringExtra("margin");
            start_price = getIntent().getStringExtra("price");
        }
        mImageView = (SmartImageView) findViewById(R.id.AuctionDetailsImageView);
        mImageView.setImageUrl(Tools.getServerUrl() + "static/" + commodity_id + "/img.png", R.drawable.ic_bg);
        mWebView = (WebView) findViewById(R.id.Web_display);
        mWebView.loadUrl(Tools.getServerUrl() + "static/" + commodity_id + "/Description.html");
        title = (TextView) findViewById(R.id.R_title);
        title.setText(commodity_name);
        NowPrice = (TextView) findViewById(R.id.R_price);
        bt_commit = (Button) findViewById(R.id.R_commit);
        bt_commit.setText("立即参拍\n(保证金 ￥" + margin + ")");
        bt_commit.setOnClickListener(this);
        R_t_3 = (TextView) findViewById(R.id.R_t_3);
        R_t_1_1 = (TextView) findViewById(R.id.R_t_1_1);
        R_t_2_2 = (TextView) findViewById(R.id.R_t_2_2);
        R_t_3_3 = (TextView) findViewById(R.id.R_t_3_3);
        R_t_1_1_1 = (TextView) findViewById(R.id.R_t_1_1_1);
        R_t_2_2_2 = (TextView) findViewById(R.id.R_t_2_2_2);
        R_t_3_3_3 = (TextView) findViewById(R.id.R_t_3_3_3);
        R_t_1_1_1_1 = (TextView) findViewById(R.id.R_t_1_1_1_1);
        R_t_2_2_2_2 = (TextView) findViewById(R.id.R_t_2_2_2_2);
        R_t_3_3_3_3 = (TextView) findViewById(R.id.R_t_3_3_3_3);
        LT = (TextView) findViewById(R.id.AD_t);
        Text1 = (TextView) findViewById(R.id.AD_t1);
        putData();
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        // 通过游标的方法可迭代查询结果
        if (c.moveToFirst()) {
            user_id = c.getString(c.getColumnIndex("user_id"));
        }


    }

    //销毁时
    @Override
    protected void onDestroy() {
        Intent send = new Intent();
        setResult(123, send);
        System.out.println("-----------onDestroy------");
        StopThread = true;
        StopThreadWait = true;
        super.onDestroy();
    }


    //新的线程每隔一秒发送一次消息
    public class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!StopThread) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //新的线程每隔一秒发送一次消息
    public class MyThreadWait extends Thread {
        @Override
        public void run() {
            super.run();
            while (!StopThreadWait) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    //在主线程里面处理消息并更新UI界面
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    UpdatePrice();
                    break;
                case 1:
                    putData();
                    break;
            }
            return false;
        }
    });

    //当拍卖结束后检查订单
    private void CheckOrder(JSONObject response) {


        try {
            if (response.getJSONObject("0").getString("user_id").equals(user_id)) {
                showAlterDialog(1);
            } else {
                showAlterDialog(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //倒计时
    private void CountDown(int s, Boolean flag) {
        int h = s / 60 / 60;
        int m = s / 60 - h * 60;
        s = s - h * 60 * 60 - m * 60;
        if (flag) {
            if (h == 0) {
                if (m == 0)
                    LT.setText("倒计时:" + s + "秒");
                else
                    LT.setText("倒计时:" + m + "分" + s + "秒");
            } else {
                LT.setText("倒计时:" + h + "时" + m + "分" + s + "秒");
            }
        } else {
            if (h == 0) {
                if (m == 0)
                    LT.setText(s + "秒" + "后开始 ");
                else
                    LT.setText(m + "分" + s + "秒" + "后开始 ");
            } else {
                LT.setText(h + "时" + m + "分" + s + "秒" + "后开始 ");
            }
        }
    }

    private void UpdatePrice() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commodity_id", commodity_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(this, "http://10.0.2.2:5000/auction/QueryCommodityOrder/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    int s = response.getInt("time");
                    if (s != 0) {
                        CountDown(s, true);
                        if (s == 1) {
                            StopThread = true;
                        }
                    } else {
                        StopThread = true;
                        CheckOrder(response);
                    }

                    if (response.length() > 2) {
                        if (!NowPrice.getText().toString().equals(response.getJSONObject("0").getString("price"))) {
                            R_t_3.setText(response.length() - 2 + "条");
                            now_price(response.length(), response);
                            if (response.length() > 1)
                                record(response);
                            num = response.length() - 1;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    //拍卖记录
    private void record(JSONObject resData) {
        try {
            if (resData.length() > 2) {
                R_t_1_1.setText(resData.getJSONObject("0").getString("user_name"));
                R_t_2_2.setText("￥" + resData.getJSONObject("0").getString("price"));
                R_t_3_3.setText(resData.getJSONObject("0").getString("time"));
            }
            if (resData.length() > 3) {
                R_t_1_1_1.setText(resData.getJSONObject("1").getString("user_name"));
                R_t_2_2_2.setText("￥" + resData.getJSONObject("1").getString("price"));
                R_t_3_3_3.setText(resData.getJSONObject("1").getString("time"));
            }
            if (resData.length() > 4) {
                R_t_1_1_1_1.setText(resData.getJSONObject("2").getString("user_name"));
                R_t_2_2_2_2.setText("￥" + resData.getJSONObject("2").getString("price"));
                R_t_3_3_3_3.setText(resData.getJSONObject("2").getString("time"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //当前价格
    private void now_price(int num, JSONObject resData) {
        if (num > 0) {
            try {
                NowPrice.setText(Integer.toString(resData.getJSONObject(Integer.toString(0)).getInt("price")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            NowPrice.setText(start_price);
            System.out.println(key);

        }
    }

    private void putData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commodity_id", commodity_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(this, "http://10.0.2.2:5000/auction/QueryCommodityOrder/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    ST = response.getInt("start_time");
                    if (ST == 0) {
                        StopThreadWait = true;
                        int s = response.getInt("time");
                        System.out.println(s);
                        if (s != 0) {
                            CountDown(s, true);
                        } else {
                            StopThread = true;
                            CheckOrder(response);
                        }

                        //防止多开线程
                        if (!StartFlag) {
                            new MyThread().start();
                            StartFlag = true;
                            Text1.setText("当前价");
                            Text1.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.colorAccent));
                            NowPrice.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.colorAccent));
                            LT.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.color2));
                            LT.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        }
                    } else {
                        CountDown(response.getInt("start_time"), false);
                        //防止多开线程
                        if (!WaitFlag) {
                            Text1.setText("起拍价");
                            Text1.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.green));
                            NowPrice.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.green));
                            LT.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            LT.setTextColor(ContextCompat.getColor(AuctionDetailsActivity.this, R.color.green));
                            new MyThreadWait().start();
                            WaitFlag = true;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                R_t_3.setText(response.length() - 2 + "条");
                num = response.length() - 2;
                now_price(response.length() - 2, response);
                if (response.length() > 2)
                    record(response);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
            case R.id.R_commit:
                if (ST == 0) {
                    Intent intent = new Intent(this, CommodityPayActivity.class);
                    intent.putExtra("num", num);
                    intent.putExtra("bzj", margin);
                    intent.putExtra("commodity_id", commodity_id);
                    intent.putExtra("NowPrice", NowPrice.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "拍卖未开始！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //参考：https://blog.csdn.net/sakurakider/article/details/80735400
//普通的提示对话框
    private void showAlterDialog(int flag) {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(AuctionDetailsActivity.this);
        alterDiaglog.setTitle("拍卖结束！");//文字
        if (flag == 1) {
            alterDiaglog.setMessage("恭喜您赢得此次拍卖！");//提示消息
            alterDiaglog.setPositiveButton("支付余款", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(AuctionDetailsActivity.this, UserOrderActivity.class);
                    startActivity(intent);
                    AuctionDetailsActivity.this.finish();
                }
            });
            alterDiaglog.setNegativeButton("继续其他拍卖", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AuctionDetailsActivity.this, "此订单在我的订单里可继续支付尾款哦！", Toast.LENGTH_SHORT).show();
                    AuctionDetailsActivity.this.finish();
                }
            });

        } else {
            alterDiaglog.setMessage("很遗憾，您错失了此次拍卖！保证金将退还");//提示消息
            alterDiaglog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AuctionDetailsActivity.this, "保证金退还中", Toast.LENGTH_SHORT).show();
                    AuctionDetailsActivity.this.finish();
                }
            });
        }
        //dialog弹出后会点击屏幕或物理返回键，dialog不消失,默认是true
        alterDiaglog.setCancelable(false);
        //显示
        alterDiaglog.show();
    }


    public void Click(View view) {
        switch (view.getId()) {
            case R.id.R_record:
                Intent intent = new Intent(this, AuctionRecordActivity.class);
                intent.putExtra("commodity_id", commodity_id);
                startActivity(intent);
                break;

        }
    }
}
