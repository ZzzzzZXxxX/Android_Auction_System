package com.itcast.auction;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//SQLite的特点是微型，轻量，占用资源低 。
//Android 提供的SQLiteOpenHelper.java 是一个抽象类，用来帮助我们实现数据库的操纵。
//此时需要在DatabaseHelper类中实现三个方法：构造函数，onCreate，onUpgrade。


public class DatabaseHelper extends SQLiteOpenHelper {
    //带全部参数的构造函数，此构造函数必不可少,name为数据库名称
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建数据库sql语句 并 执行,相当于初始化数据库
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table user(user_id varchar(40) primary key, user_name varchar(15) not null ,admin Integer)";
        db.execSQL(sql);
    }

    //这里应当实现数据库升级等操作

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
