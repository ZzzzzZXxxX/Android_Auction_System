package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommodityPayActivity extends AppCompatActivity implements View.OnClickListener{
    int num;
    String bzj;
    TextView BZJ_price;
    Button BZJ_commit;
    Button Act_commit;
    RelativeLayout BZJ;
    RelativeLayout R_Act;
    String user_id;
    String user_name;
    String commodity_id;
    EditText Act_price;
    double NowPrice;

    //定义格式
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_pay);
        num = getIntent().getIntExtra("num",0);
        bzj=getIntent().getStringExtra("bzj");
        commodity_id=getIntent().getStringExtra("commodity_id");
        NowPrice=Double.valueOf(getIntent().getStringExtra("NowPrice"));
        BZJ_price=(TextView)findViewById(R.id.BZJ_price);
        BZJ_price.setText(bzj);
        BZJ_commit=(Button)findViewById(R.id.BZJ_commit);
        BZJ_commit.setOnClickListener(this);
        BZJ=(RelativeLayout)findViewById(R.id.R_BZJ);
        R_Act=(RelativeLayout)findViewById(R.id.R_Act);
        Act_price=(EditText)findViewById(R.id.et_Act_price);
        Act_commit=(Button)findViewById(R.id.Act_commit);
        Act_commit.setOnClickListener(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        // 通过游标的方法可迭代查询结果
        if(c.moveToFirst()) {
            user_id = c.getString(c.getColumnIndex("user_id"));
            user_name=c.getString(c.getColumnIndex("user_name"));
        }
        //“Q”查询，“I”插入
        QueryBZJ("Q");
    }


//查询保证金
    private void QueryBZJ(final String flag) {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commodity_id", commodity_id);
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
        client.post(this, "http://10.0.2.2:5000/auction/QueryBZJ/"+flag, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
               if(flag.equals("Q")){
                   try {
                       if(response.getString("flag").equals("1")){
                           //设置不可见
                           BZJ.setVisibility(View.INVISIBLE);
                           //可见
                           R_Act.setVisibility(View.VISIBLE);
                       }else if(response.getString("flag").equals("0")){
                           //设置可见
                           BZJ.setVisibility(View.VISIBLE);
                           //不可见
                           R_Act.setVisibility(View.INVISIBLE);

                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               else if(flag.equals("I")){
                   try {
                       errmsg(response.getString("errmsg"));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }
//插入拍卖记录
    private void InsertAct() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("commodity_id", commodity_id);
            jsonObject.put("id", num+1);
            jsonObject.put("user_name", user_name);
            jsonObject.put("price", Act_price.getText().toString());
            jsonObject.put("time", dateformat.format(new Date()));
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
        client.post(this, "http://10.0.2.2:5000/auction/InsertAct/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }
    private void errmsg(String msg){
        if(msg.equals("1")){
            Toast.makeText(this, "支付成功！", Toast.LENGTH_SHORT).show();
            //设置不可见
            BZJ.setVisibility(View.INVISIBLE);
            //可见
            R_Act.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(this, "支付失败，请重新提交！", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BZJ_commit:
                //这边可以是支付接口，略！



                QueryBZJ("I");
                


                break;

            case R.id.Act_commit:
                if(NowPrice<Double.valueOf(Act_price.getText().toString())){
                    InsertAct();
                    this.finish();
                }else {
                    Toast.makeText(this, "参拍金额不高于目前价格，请重新输入！", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
