package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userId;
    private EditText psw;
    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();


    }

    private void initView() {
        userId = (EditText) findViewById(R.id.userId);
        psw = (EditText) findViewById(R.id.psw);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loginBtn:
                putData();
                break;
            case R.id.signupBtn:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
        }
    }

    private void putData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient对象
        JSONObject jsonObject = new JSONObject();//JSONObject 对象
        try {
            //添加json键值对
            jsonObject.put("user_name", userId.getText().toString().trim());
            jsonObject.put("pwd", psw2md5(psw.getText().toString().trim()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            //将封装好的json数据，通过new一个ByteArrayEntity的方式，在post的entity里设置UTF-8格式的byte类型的json数据，以保证中文的正确传输
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            //设置连接类型HTTP，application/json
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //post请求
        client.post(this, "http://10.0.2.2:5000/auction/Login/", entity, "application/json", new JsonHttpResponseHandler() {
            //成功
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    //解析响应
                    String flag = response.get("flag").toString();
                    String errmsg = response.get("errmsg").toString();
                    //通过解析的数据，进行下一步操作
                    if (flag.equals("1")) {
                        String user_id = response.get("user_id").toString();
                        String user_name = userId.getText().toString().trim();
                        String admin = response.get("admin").toString();
                        errMsg(flag, errmsg, user_id, user_name, Integer.parseInt(admin));
                    } else {
                        errMsg(flag, errmsg, null, null, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //失败
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void errMsg(String flag, String errmsg, String user_id, String user_name, Integer admin) {
        if (flag.equals("1") && admin != 2) {
            Toast.makeText(this, errmsg, Toast.LENGTH_SHORT).show();
            DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            //清空user表
            String sql = "delete from user";
            db.execSQL(sql);
            /**
             *  操作1：插入数据 = insert()
             */
            // a. 创建ContentValues对象
            ContentValues values = new ContentValues();

            // b. 向该对象中插入键值对
            values.put("user_id", user_id);
            values.put("user_name", user_name);
            values.put("admin", admin);
            //其中，key = 列名，value = 插入的值
            //注：ContentValues内部实现 = HashMap，区别在于：ContenValues Key只能是String类型，Value可存储基本类型数据 & String类型

            // c. 插入数据到数据库当中：insert()
            db.insert("user", null, values);
            // 参数1：要操作的表名称
            // 参数2：SQl不允许一个空列，若ContentValues是空，那么这一列被明确的指明为NULL值
            // 参数3：ContentValues对象
            // 注：也可采用SQL语句插入
            // String sql = "insert into user (id,name) values (1,'carson')";
            // db.execSQL(sql);
            db.close();
            if (admin == 0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                LoginActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
            } else {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                LoginActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
            }


        } else {
            if (admin==2){
                Toast.makeText(this, "登录失败，可能账户已停用！", Toast.LENGTH_SHORT).show();
            }
            else
            Toast.makeText(this, errmsg, Toast.LENGTH_SHORT).show();
        }
    }

    //密码MD5加密
    private String psw2md5(String psw) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(psw.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
