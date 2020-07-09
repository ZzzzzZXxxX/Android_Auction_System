package com.itcast.auction.ui.Home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<JSONObject> jsonObject = new MutableLiveData<JSONObject>();

    public HomeViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
        getdata("1");
    }

    public MutableLiveData<JSONObject> getJSONObject(){
        return jsonObject;
    }

    protected void getdata(String args) {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/getAuction/" + args, new JsonHttpResponseHandler() {
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

    protected void Sgetdata(String args) {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient

        client.post("http://10.0.2.2:5000/auction/SgetAuction/" + args, new JsonHttpResponseHandler() {
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
