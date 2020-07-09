package com.itcast.auction.ui.Notice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class NoticeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<JSONObject> jsonObject = new MutableLiveData<JSONObject>();

    public NoticeViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
        GetNotice();
    }

    public MutableLiveData<JSONObject> getJSONObject(){
        return jsonObject;
    }

    private void GetNotice() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/GetNotice/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonObject.setValue(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}

