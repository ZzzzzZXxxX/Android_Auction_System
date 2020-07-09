package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class UserManagementActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et1;
    EditText et2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        et1 = (EditText) findViewById(R.id.adm_et1);
        et2 = (EditText) findViewById(R.id.adm_et2);
    }

    private void PutData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", et1.getText().toString());
            jsonObject.put("pwd", psw2md5(et2.getText().toString()));

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
        client.post(this, "http://10.0.2.2:5000/auction/UserManagement/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("rs", response.toString());
                //response为
                try {
                    ErrMsg(response.getString("errmsg"));
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

    private void ErrMsg(String errmsg) {
        Toast.makeText(this, errmsg, Toast.LENGTH_SHORT).show();
    }

    protected void DiscontinueUser() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/DiscontinueUser/" + et1.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    ErrMsg(response.getString("errmsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    protected void RecoveryUser() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/RecoveryUser/" + et1.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    ErrMsg(response.getString("errmsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
            case R.id.adm_commit:
                if (et1.getText().length() == 0 || et2.getText().length() == 0) {
                    Toast.makeText(this, "请补全信息！", Toast.LENGTH_SHORT).show();
                } else
                    PutData();
                break;
            case R.id.adm_commit1:
                if (et1.getText().length() == 0) {
                    Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
                } else
                    DiscontinueUser();

                break;
            case R.id.adm_commit2:
                if (et1.getText().length() == 0) {
                    Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
                } else
                    RecoveryUser();
                break;
        }
    }
}
