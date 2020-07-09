package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{
    EditText ProFile_et1;
    EditText ProFile_et2;
    EditText ProFile_et3;
    Button ProFile_commit;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ProFile_et1=(EditText)findViewById(R.id.ProFile_et1);
        ProFile_et2=(EditText)findViewById(R.id.ProFile_et2);
        ProFile_et3=(EditText)findViewById(R.id.ProFile_et3);
        ProFile_commit=(Button)findViewById(R.id.ProFile_commit);
        ProFile_commit.setOnClickListener(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        // 通过游标的方法可迭代查询结果
        if(c.moveToFirst()) {
            user_id=c.getString(c.getColumnIndex("user_id"));
            GetData();
        }
    }

    private void GetData() {
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
        client.post(this, "http://10.0.2.2:5000/auction/QueryUserInfo/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    ProFile_et1.setHint("电话:"+response.getString("phone"));
                    ProFile_et2.setHint("姓名:"+response.getString("name"));
                    ProFile_et3.setHint("地址:"+response.getString("address"));
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

    private void PutData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("phone", ProFile_et1.getText().toString());
            jsonObject.put("name", ProFile_et2.getText().toString());
            jsonObject.put("address", ProFile_et3.getText().toString());


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
        client.post(this, "http://10.0.2.2:5000/auction/UpdateUserInfo/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    errmsg(response.getString("errmsg"));
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
    private void errmsg(String msg){

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ProFile_commit:
                if(ProFile_et1.getText().toString().isEmpty()||ProFile_et2.getText().toString().isEmpty()||ProFile_et2.getText().toString().isEmpty()){
                    Toast.makeText(this, "请补全信息！", Toast.LENGTH_SHORT).show();
                }else {
                    PutData();
                }
                break;
        }
    }
}
