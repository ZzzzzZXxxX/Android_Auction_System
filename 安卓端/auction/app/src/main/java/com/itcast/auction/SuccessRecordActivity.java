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

import cz.msebera.android.httpclient.Header;

public class SuccessRecordActivity extends AppCompatActivity {
    ListView mListView;
    JSONObject ResData;
    int num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_record);
        mListView = (ListView) findViewById(R.id.lv_success);
        GetSuccessOrder();
    }
    protected void GetSuccessOrder() {
            AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/GetSuccessOrder/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ResData=response;
                num=response.length();
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new SuccessRecordActivity.MyBaseAdapter());



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
                        inflate(R.layout.order_item, parent, false);
                holder = new ViewHolder();
                holder.Title = (TextView) convertView.findViewById(R.id.tv_it_title_order);
                holder.siv = (SmartImageView) convertView.findViewById(R.id.siv_icon_order);
                holder.client = (TextView) convertView.findViewById(R.id.tv_it_price_order);
                holder.it_img = (ImageView) convertView.findViewById(R.id.it_img_order);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(ResData.getJSONObject(Integer.toString(position)).getString("commodity_name"));
                holder.siv.setImageUrl(Tools.getServerUrl() + "static/" + ResData.getJSONObject(Integer.toString(position)).getString("commodity_id") + "/img.png", R.drawable.ic_bg);
                holder.client.setText("客户："+ResData.getJSONObject(Integer.toString(position)).getString("user_name")+",以"+ResData.getJSONObject(Integer.toString(position)).getString("price")+"拍下");
                if (ResData.getJSONObject(Integer.toString(position)).getString("pay").equals("1")) {
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
            TextView client;
            SmartImageView siv;  //SmartImageView
            ImageView it_img;
        }


    }

}
