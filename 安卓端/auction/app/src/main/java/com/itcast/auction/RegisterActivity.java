package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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
import java.util.UUID;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText user_name;
    EditText password;
    EditText name;
    EditText phone;
    EditText address;
    Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }

    private void initView() {
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (checkInput()) {
            putData();


        }


    }

    private boolean checkInput() {
        if (!(!user_name.getText().toString().trim().isEmpty() && user_name.getText().toString().trim().length() >= 4 && user_name.getText().toString().trim().length() <= 14)) {
            Toast.makeText(this, "请输入账号！且保持在4到16个字符内。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(!password.getText().toString().trim().isEmpty() && password.getText().toString().trim().length() >= 4 && password.getText().toString().trim().length() <= 14)) {
            Toast.makeText(this, "请输入密码！且保持在4到16个字符内。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.getText().toString().trim().isEmpty() || phone.getText().toString().trim().length() != 11) {
            Toast.makeText(this, "请输入手机！且为11位。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入地址！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private String getUserID() {
        String UserID;
        UserID = UUID.randomUUID().toString();
        return UserID;
    }

    private void putData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", getUserID());
            jsonObject.put("user_name", user_name.getText().toString().trim());
            jsonObject.put("pwd", psw2md5(password.getText().toString().trim()));
            jsonObject.put("admin", 0);
            jsonObject.put("name", name.getText().toString().trim());
            jsonObject.put("phone", phone.getText().toString().trim());
            jsonObject.put("address", address.getText().toString().trim());

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
        client.post(this, "http://10.0.2.2:5000/auction/Register/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                try {
                    String flag = response.get("flag").toString();
                    String errmsg = response.get("errmsg").toString();
                    errMsg(flag, errmsg);

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

    private void errMsg(String flag, String errmsg) {
        if (flag.equals("1")) {
            Toast.makeText(this, errmsg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);    //第二个参数即为执行完跳转后的Activity
            startActivity(intent);
            RegisterActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
        } else {
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
