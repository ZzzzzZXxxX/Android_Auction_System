package com.itcast.auction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vorlonsoft.android.http.AsyncHttpClient;
import com.vorlonsoft.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
//创建拍品界面
public class CreateAuctionActivity extends AppCompatActivity implements View.OnClickListener {
    private static int CAMERA_REQUEST_CODE = 1;
    private static int IMAGE_RESULT_CODE = 0;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    //用于保存拍照图片的uri
    private Uri mCameraUri;

    private String Camerastr = new String();

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private ImageView imageView;
    private TextView mTextView;
    //当前时间
    Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR);
    private int month = c.get(Calendar.MONTH);
    private int day = c.get(Calendar.DAY_OF_MONTH);
    private int hour = c.get(Calendar.HOUR_OF_DAY);
    private int minute = c.get(Calendar.MINUTE);
    private int second = c.get(Calendar.SECOND);
    //当前时间
    Date Date1;
    //设定时间
    Date Date2;
    //最终设定的时间
    Date date;
    //定义格式
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    EditText title;
    EditText start_price;
    EditText Margin;
    EditText length_time;
    //提交按钮
    Button commit;
    //商品信息实体类的对象
    CommodityInformation info = new CommodityInformation();
    //判断详情页面是否已编辑
    String CommodityDescription_flag;
    //详情页面图片的数量
    int img_src_num = 0;
    //详情页面 图片的list
    ArrayList<String> img_src = new ArrayList<String>();
    //详情页面的Html
    String Htm;
    //已编辑
    TextView CommodityDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_auction);
        initView();

    }

    private void initView() {
        info.setCommodity_id(KeyUtil.genUniqueKeyProduct());
        imageView = (ImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.tv_time);
        title = (EditText) findViewById(R.id.title);
        start_price = (EditText) findViewById(R.id.et_start_price);
        Margin = (EditText) findViewById(R.id.et_Margin);
        length_time = (EditText) findViewById(R.id.et_length);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        CommodityDescription = (TextView) findViewById(R.id.CommodityDescription_flag);
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%d-%02d-%02d %02d:%02d:%02d", year, month + 1, day, hour, minute + 5, second));
        mTextView.setText(sb);
        try {
            Date1 = dateformat.parse(sb.toString());
            //如果没有设置时间，则以目前时间+5分为开始时间
            date = Date1;

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    /*列表Dialog*/
    private void Dialog() {
        final String[] items = {"相册", "拍照"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(CreateAuctionActivity.this);
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

    private void date_Dialog() {


        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAuctionActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                time_Dialog(view);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void time_Dialog(final DatePicker v) {


        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAuctionActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                StringBuffer sb = new StringBuffer();

                sb.append(String.format("%d-%02d-%02d %02d:%02d:00", v.getYear(), v.getMonth() + 1, v.getDayOfMonth(), view.getHour(), view.getMinute()));


                try {
                    Date2 = dateformat.parse(sb.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date now = new Date();
                Date1 = new Date(now.getTime() + 5 * 60 * 1000); //现在时间加5分钟
                if (Date1.compareTo(Date2) == -1) {
                    mTextView.setText(sb);
                    date = Date2;
                } else {
                    date = Date1;
                    Toast.makeText(CreateAuctionActivity.this, "请选择距现在时间5分钟之后的时间！", Toast.LENGTH_SHORT).show();
                }


            }
        }, hour, minute, true);

        timePickerDialog.show();
    }


    public void Click(View view) {
        switch (view.getId()) {
            case R.id.choose_image:
                Dialog();
                break;
            case R.id.input_4:
                date_Dialog();
                break;
            case R.id.input_3:
                Intent intent = new Intent(CreateAuctionActivity.this, CommodityDescription.class);    //第二个参数即为执行完跳转后的Activity
                intent.putExtra("Id", info.getCommodity_id());
                //开启意图，并设置请求码是0，相当于设置一个监听或者中断
                //这个中断将在运行到setResult(结果码,意图实例);这样的代码返回来
                //请求码用于判定返回的意图传到哪里，
                startActivityForResult(intent, 666);

                break;


        }
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

        if (resultCode == RESULT_OK) {
            //铺满区域
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                    GridView.LayoutParams.MATCH_PARENT);
            if (requestCode == CAMERA_REQUEST_CODE) {

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(mCameraUri));
                    //img设置区域
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(bitmap);
                    Camerastr = bitmaptoString(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == IMAGE_RESULT_CODE) {

                try {
                    mCameraUri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri));
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(bitmap);
                    Camerastr = bitmaptoString(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        //判断请求码是什么，判断结果码是什么，来执行相应代码
        if (requestCode == 666) {
            switch (resultCode) {
                case 11:
                    CommodityDescription_flag = data.getStringExtra("flag");
                    if (CommodityDescription_flag != null && CommodityDescription_flag.equals("true")) {
                        CommodityDescription.setText("已编辑");
                    }
                    break;

                default:
                    break;

            }

        }
    }

    private String bitmaptoString(Bitmap bm) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;

    }

    private void putData() {
        AsyncHttpClient client = new AsyncHttpClient(); //AsyncHttpClient
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", info.getCommodity_id());
            jsonObject.put("img", Camerastr);
            jsonObject.put("title", title.getText().toString());
            jsonObject.put("start_price", start_price.getText().toString());
            jsonObject.put("Margin", Margin.getText().toString());
            Date end_time = new Date(date.getTime() + Integer.parseInt(length_time.getText().toString()) * 60 * 60 * 1000);
            jsonObject.put("end_time", dateformat.format(end_time));
            jsonObject.put("Date", dateformat.format(date));
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
        client.post(this, "http://10.0.2.2:5000/auction/Auction_info/", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String flag = response.get("flag").toString();
                    errMsg(flag);
                    AdminActivity.getdata();//更新AdminActivity的拍卖与待拍卖数量

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


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (checkInput()) {
                    putData();
                    this.finish();
                }
                break;
        }
    }

    private boolean checkInput() {
        if (Camerastr.isEmpty()) {
            Toast.makeText(this, "未加商品图!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入商品标题！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (start_price.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入拍卖价格！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Margin.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入保证金！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(CommodityDescription_flag != null && CommodityDescription_flag.equals("true"))) {
            Toast.makeText(this, "未编辑商品描述！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (length_time.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入拍卖时长！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
