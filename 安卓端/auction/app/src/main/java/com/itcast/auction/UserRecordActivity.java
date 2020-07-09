package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class UserRecordActivity extends AppCompatActivity {
    String user_id;
    ListView mListView;

    int num = 0;
    JSONObject res_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_record);
        user_id = (String) getIntent().getStringExtra("user_id");
        mListView = (ListView) findViewById(R.id.UserRecordList);
        GetUserRecord();
    }

    protected void GetUserRecord() {
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
        client.post(this, "http://10.0.2.2:5000/auction/GetUserRecord/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                num = response.length();
                res_data = response;
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new UserRecordActivity.MyBaseAdapter());
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            String commodity_id = res_data.getJSONObject(Integer.toString(i)).getString("commodity_id");
                            String commodity_name = res_data.getJSONObject(Integer.toString(i)).getString("commodity_name");
                            int price = res_data.getJSONObject(Integer.toString(i)).getInt("price");
                            int margin = res_data.getJSONObject(Integer.toString(i)).getInt("margin");
                            Intent intent = new Intent(UserRecordActivity.this, AuctionDetailsActivity.class);
                            intent.putExtra("commodity_id", commodity_id);
                            intent.putExtra("commodity_name", commodity_name);
                            intent.putExtra("price", Integer.toString(price));
                            intent.putExtra("margin", Integer.toString(margin));
                            //标志，拍卖界面为1，记录界面2
                            intent.putExtra("flag", "2");
                            startActivity(intent);
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
            UserRecordActivity.MyBaseAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.list_item, parent, false);
                holder = new UserRecordActivity.MyBaseAdapter.ViewHolder();
                holder.Title = (TextView) convertView.findViewById
                        (R.id.tv_it_title);
                holder.siv = (SmartImageView) convertView.findViewById(R.id.siv_icon);
                holder.Price = (TextView) convertView.findViewById(R.id.tv_it_price);
                holder.it_img = (ImageView) convertView.findViewById(R.id.it_img);
                holder.time = (TextView) convertView.findViewById(R.id.tv_it_time);


                convertView.setTag(holder);
            } else {
                holder = (UserRecordActivity.MyBaseAdapter.ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(res_data.getJSONObject(Integer.toString(position)).getString("commodity_name"));
                holder.siv.setImageUrl(Tools.getServerUrl() + "static/" + res_data.getJSONObject(Integer.toString(position)).getString("commodity_id") + "/img.png", R.drawable.ic_bg);
//                holder.siv.setImageUrl("http://10.0.2.2:5000/static/158290676652018/img.png", R.drawable.ic_bg);
                String end_time = res_data.getJSONObject(Integer.toString(position)).getString("end_time");
                holder.time.setText("于" + end_time + "结束");
                holder.Price.setText("当前价：¥" + res_data.getJSONObject(Integer.toString(position)).getInt("price"));
                if (res_data.getJSONObject(Integer.toString(position)).getString("flag").equals("1")) {
                    holder.it_img.setBackgroundResource(R.drawable.ic_act);
                } else if (res_data.getJSONObject(Integer.toString(position)).getString("flag").equals("2")) {
                    holder.it_img.setBackgroundResource(R.drawable.ic_success1);
                } else if (res_data.getJSONObject(Integer.toString(position)).getString("flag").equals("3")) {
                    holder.it_img.setBackgroundResource(R.drawable.ic_unsuccessful1);
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
            TextView time;
        }


    }

}



