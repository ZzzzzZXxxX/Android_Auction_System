package com.itcast.auction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import static android.os.SystemClock.sleep;

public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DatabaseHelper databaseHelper = new DatabaseHelper(this, "auction", null, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final SQLiteDatabase db = databaseHelper.getWritableDatabase();
//                String sql = "delete from user ";
//                db.execSQL(sql);
//                清空user表
                final Cursor c = db.rawQuery("select * from user", null);
                //判断是否已登录，登录的时候，会把用户信息写入sqlite，这里查询用户表是否为空

                //耗时任务，比如加载网络数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 这里可以睡几秒钟，如果要放广告的话
                        sleep(1000);
                        if (c.getCount() == 0) {
                            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);    //第二个参数即为执行完跳转后的Activity
                            startActivity(intent);
                            db.close();
                            LaunchActivity.this.finish();
                        } else {
                            Cursor cu=db.query("user", null, null, null, null, null, null);
                            cu.moveToFirst();
                            if(cu.getInt(cu.getColumnIndex("admin"))==0) {
                                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);    //第二个参数即为执行完跳转后的Activity
                                startActivity(intent);
                                db.close();
                                LaunchActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
                            }
                            else {
                                Intent intent = new Intent(LaunchActivity.this, AdminActivity.class);    //第二个参数即为执行完跳转后的Activity
                                startActivity(intent);
                                db.close();
                                LaunchActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
                            }
                        }

                    }
                });
            }
        }).start();
    }


}
