package com.itcast.auction.ui.User;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.itcast.auction.DatabaseHelper;
import com.itcast.auction.EditProfileActivity;
import com.itcast.auction.LoginActivity;
import com.itcast.auction.R;
import com.itcast.auction.UserOrderActivity;
import com.itcast.auction.UserRecordActivity;

public class UserFragment extends Fragment  {

    private UserViewModel mViewModel;

    TextView tv_Username;
    String user_id;



    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.user_fragment, container, false);
        View UserInfo = root.findViewById(R.id.UserInfo);
        tv_Username=root.findViewById(R.id.tv_Username);
        UserInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        View UserOrder = root.findViewById(R.id.UserOrder);
        UserOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserOrderActivity.class);
                startActivity(intent);
            }
        });
        View UserRecord = root.findViewById(R.id.UserRecord);
        UserRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserRecordActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), "auction", null, 1);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        // 通过游标的方法可迭代查询结果
        if(c.moveToFirst()) {
            tv_Username.setText(c.getString(c.getColumnIndex("user_name")));
            user_id=c.getString(c.getColumnIndex("user_id"));
        }
        Button commit_quit=root.findViewById(R.id.commit_quit);
        commit_quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sql = "delete from user ";
                db.execSQL(sql);
                Intent intent = new Intent(getActivity(), LoginActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                getActivity().finish();

            }


        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        // TODO: Use the ViewModel
    }

}
