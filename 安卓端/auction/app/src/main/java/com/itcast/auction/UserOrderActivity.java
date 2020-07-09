package com.itcast.auction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class UserOrderActivity extends AppCompatActivity {
    ListView mListView;
    String user_id;
    int num;
    JSONObject res_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);
        mListView = (ListView) findViewById(R.id.lv_order);
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        // 通过游标的方法可迭代查询结果
        if (c.moveToFirst()) {
            user_id = c.getString(c.getColumnIndex("user_id"));
            GetOrder();
        }
    }

    private void GetOrder() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);


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
        client.post(this, "http://10.0.2.2:5000/auction/QueryUSerOrder/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                num = response.length();
                res_data = response;
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new MyBaseAdapter());
                //点击Item
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            if (res_data.getJSONObject(Integer.toString(i)).getString("pay").equals("1")) {
                                msg();

                            } else {

                                Dialog(i);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }

    private void ZHIFU(int i) {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("commodity_id", res_data.getJSONObject(Integer.toString(i)).getString("commodity_id"));


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
        client.post(this, "http://10.0.2.2:5000/auction/ZHIFU/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    String args=response.getString("flag");
                    ZHIFUmsg(args);
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

    public void Dialog(final int i) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(UserOrderActivity.this);
//        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("支付");
        try {
            normalDialog.setMessage("剩余需支付："+(Double.valueOf(res_data.getJSONObject(Integer.toString(i)).getString("price"))-Double.valueOf(res_data.getJSONObject(Integer.toString(i)).getString("margin"))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    //这边可以是支付接口，这边直接写数据库
                        ZHIFU(i);

                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }
//刷新界面
    public static void restartActivity(Activity activity){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("延迟1.5s");
                //延迟特定时间后执行该语句（public void run()的花括号里的语句）
            } }, 1500);        //这里的数字1500意思是延迟1500毫秒

        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        activity.finish();
    }

    private void msg() {
        Toast.makeText(this, "已支付，拍卖订单成功！", Toast.LENGTH_SHORT).show();
    }
    private void ZHIFUmsg(String args) {
        Toast.makeText(this, args, Toast.LENGTH_SHORT).show();
        restartActivity(UserOrderActivity.this);
    }

    class MyBaseAdapter extends BaseAdapter {
        //得到item的总数
        @Override
        public int getCount() {
            //返回ListView Item条目的总数
            return num;
        }

        //得到Item代表的对象
        @Override
        public Object getItem(int position) {
            //返回ListView Item条目代表的对象
            return null;
        }

        //得到Item的id
        @Override
        public long getItemId(int position) {
            //返回ListView Item的id
            return position;
        }

        //得到Item的View视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.order_item, parent, false);
                holder = new ViewHolder();
                holder.Title = (TextView) convertView.findViewById(R.id.tv_it_title_order);
                holder.siv = (SmartImageView) convertView.findViewById(R.id.siv_icon_order);
                holder.Price = (TextView) convertView.findViewById(R.id.tv_it_price_order);
                holder.it_img = (ImageView) convertView.findViewById(R.id.it_img_order);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(res_data.getJSONObject(Integer.toString(position)).getString("commodity_name"));
                holder.siv.setImageUrl(Tools.getServerUrl() + "static/" + res_data.getJSONObject(Integer.toString(position)).getString("commodity_id") + "/img.png", R.drawable.ic_bg);
                holder.Price.setText(res_data.getJSONObject(Integer.toString(position)).getString("price"));
                if (res_data.getJSONObject(Integer.toString(position)).getString("pay").equals("1")) {
                    holder.it_img.setBackgroundResource(R.drawable.ic_paid);
                } else {
                    holder.it_img.setBackgroundResource(R.drawable.ic_unpaid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView Title;
            TextView Price;
            SmartImageView siv;  //SmartImageView
            ImageView it_img;
        }


    }

}
