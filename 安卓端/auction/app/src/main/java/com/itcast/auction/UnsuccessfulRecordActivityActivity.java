package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UnsuccessfulRecordActivityActivity extends AppCompatActivity {
    ListView mListView;
    JSONObject ResData;
    int num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsuccessful_record_activity);
        mListView=(ListView)findViewById(R.id.lv_unsuccessful);
        GetSuccessOrder();
    }
    protected void GetSuccessOrder() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/GetUnsuccessfulOrder/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ResData=response;
                num=response.length();
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new UnsuccessfulRecordActivityActivity.MyBaseAdapter());



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
                        inflate(R.layout.record_item, parent, false);
                holder = new ViewHolder();
                holder.Title = (TextView) convertView.findViewById(R.id.tv_it_title_record);
                holder.siv = (SmartImageView) convertView.findViewById(R.id.siv_icon_record);
                holder.time=(TextView)convertView.findViewById(R.id.it_time_record);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(ResData.getJSONObject(Integer.toString(position)).getString("commodity_name"));
                holder.siv.setImageUrl(Tools.getServerUrl() + "static/" + ResData.getJSONObject(Integer.toString(position)).getString("commodity_id") + "/img.png", R.drawable.ic_bg);
                holder.time.setText(ResData.getJSONObject(Integer.toString(position)).getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView Title;
            SmartImageView siv;  //SmartImageView
            TextView time;
        }


    }
}
