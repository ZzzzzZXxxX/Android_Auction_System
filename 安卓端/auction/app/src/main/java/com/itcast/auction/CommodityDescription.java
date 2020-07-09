package com.itcast.auction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.qzb.richeditor.RichEditor;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommodityDescription extends AppCompatActivity implements View.OnClickListener {
    Button bt_left;
    Button bt_right;
    RichEditor re;

    private static int CAMERA_REQUEST_CODE = 1;
    private static int IMAGE_RESULT_CODE = 0;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    //用于保存拍照图片的uri
    private Uri mCameraUri;

    private String Camerastr;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    ArrayList<String> img_src = new ArrayList<String>();
    int img_src_num = 0;
    String Htm;
    String Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_description);
        re = (RichEditor) findViewById(R.id.editor);
        re.setPlaceholder("添加图片和文字让你的商品更诱人");
        re.setPadding(20, 20, 20, 20);
        re.setTextBackgroundColor(Color.WHITE);
        bt_left = (Button) findViewById(R.id.bt_left);
        bt_left.setOnClickListener(this);
        bt_right = (Button) findViewById(R.id.bt_right);
        bt_right.setOnClickListener(this);
        Id = this.getIntent().getStringExtra("Id");
    }


    /**
     *
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_left:
                Dialog();
                break;
            case R.id.bt_right:
                //如果没有任何输入，re为null，使用gethtml可以使html_Img不出错
                Htm = getHtmlData(html_Img(getHtmlData(re.getHtml())));
                putData();
//                re.loadDataWithBaseURL(null,
//                        getHtmlData(html_ImgToBase64(re.getHtml())), "text/html", "utf-8", null);
                Intent send = new Intent();
                send.putExtra("flag", "true");
                setResult(11, send);
                this.finish();
                break;
        }
    }

    //
    public String html_Img(String html) {
        Document doc = Jsoup.parse(html, "utf-8");
        Elements imgs = doc.getElementsByTag("img");

        for (Element img : imgs) {
            String src = img.attr("src");
            try {
                Uri uri = Uri.parse(src);
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                Camerastr = bitmaptoString(bitmap);
                img_src.add(Camerastr);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //将有效的路径传入新的src的方法
            img.attr("src", "img" + ++img_src_num + ".png");

        }

        //返回html文档
        return doc.getElementsByTag("body").html();
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\" charset=\"utf-8\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto!important;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    private String bitmaptoString(Bitmap bm) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;

    }

    /*列表Dialog*/
    private void Dialog() {
        final String[] items = {"相册", "拍照"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(CommodityDescription.this);
//        listDialog.setTitle("我是一个列表Dialog");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openImage();
                        break;
                    case 1:
                        checkPermissionAndCamera();
                        break;
                }

            }
        });
        listDialog.show();
    }


    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            openCamera();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }
            mCameraUri = photoUri;

            if (photoUri != null) {

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void openImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_RESULT_CODE);

    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri));
                    re.insertImage(mCameraUri.toString(), "", 100);
                    Camerastr = bitmaptoString(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == IMAGE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    mCameraUri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri));
                    re.insertImage(mCameraUri.toString(), "", 100);
                    Camerastr = bitmaptoString(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void putData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            int num = 0;
            if (img_src_num > 0) {
                for (String i : img_src) {
                    num++;
                    jsonObject.put("img" + num, i);
                }
            }
            jsonObject.put("Id", Id);
            jsonObject.put("num", img_src_num);
            jsonObject.put("Htm", Htm);
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
        client.post(this, "http://10.0.2.2:5000/auction/Auction_CommodityDescription/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String flag = response.get("flag").toString();
                    errMsg(flag);

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
    private void errMsg(String flag) {

        Toast.makeText(this, flag, Toast.LENGTH_SHORT).show();

    }


}
