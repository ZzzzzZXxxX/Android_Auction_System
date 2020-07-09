package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

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

import com.itcast.auction.ui.Home.HomeFragment;
import com.loopj.android.image.SmartImageView;
import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AuctionRecordActivity extends AppCompatActivity {
    ListView mListView;
    String commodity_id;
    int num;
    JSONObject res_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_record);
        mListView = (ListView)findViewById(R.id.lv_record);
        commodity_id=getIntent().getStringExtra("commodity_id");
        GetRecord();
    }

    private void GetRecord() {
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
                num=response.length()-2;
                res_data=response;
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new MyBaseAdapter());




            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


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
                        inflate(R.layout.auction_record_item, parent, false);
                holder = new ViewHolder();
                holder.it_user = (TextView) convertView.findViewById
                        (R.id.it_user);
                holder.it_time = (TextView) convertView.findViewById
                        (R.id.it_time);
                holder.it_price = (TextView) convertView.findViewById
                        (R.id.it_price);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.it_user.setText(res_data.getJSONObject(Integer.toString(position)).getString("user_name"));
                holder.it_time.setText(res_data.getJSONObject(Integer.toString(position)).getString("time"));
                holder.it_price.setText("￥"+res_data.getJSONObject(Integer.toString(position)).getString("price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView it_user;
            TextView it_time;
            TextView it_price;
        }


    }

}
