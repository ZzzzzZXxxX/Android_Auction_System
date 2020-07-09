package com.itcast.auction.ui.Home;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.itcast.auction.AuctionDetailsActivity;
import com.itcast.auction.R;
import com.itcast.auction.Tools;
import com.loopj.android.image.SmartImageView;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private HomeViewModel mViewModel;
    private int jsonObjNum;
    private JSONObject jsonObj;
    LinearLayout loading;
    ListView mListView;
    CheckBox cd1;
    CheckBox cd2;
    boolean isCd1 = true;
    boolean isCd2 = false;
    SearchView mSearchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        loading = (LinearLayout) root.findViewById(R.id.loading);
        mListView = (ListView) root.findViewById(R.id.lv_news);
        cd1 = (CheckBox) root.findViewById(R.id.checkBox1);
        cd1.setOnCheckedChangeListener(this);
        cd2 = (CheckBox) root.findViewById(R.id.checkBox2);
        cd2.setOnCheckedChangeListener(this);
        mSearchView = (SearchView) root.findViewById(R.id.SV);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //输入完成后，点击回车或是完成键
            @Override
            public boolean onQueryTextSubmit(String query) {
                //query为搜索的参数
                if (isCd1) {
                    if (isCd2) {
                        //都有，待拍卖+拍卖中
                        mViewModel.Sgetdata("3+" + query);
                    } else {
                        //拍卖中
                        mViewModel.Sgetdata("1+" + query);
                    }
                } else {
                    if (isCd2) {
                        //待拍卖
                        mViewModel.Sgetdata("2+" + query);
                    } else {
                        //null
                        mViewModel.Sgetdata("0+" + query);
                    }
                }
                //加载资源动画可见
                loading.setVisibility(View.VISIBLE);

                return true;
            }

            //查询文本框有变化时事件
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //添加下面一句,防止数据两次加载
        mSearchView.setIconified(true);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getJSONObject().observe(getViewLifecycleOwner(), new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                jsonObjNum = jsonObject.length();
                jsonObj = jsonObject;
                //更新界面，使加载不可见
                loading.setVisibility(View.INVISIBLE);
                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new MyBaseAdapter());
                //点击Item
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            System.out.println(jsonObj.getJSONObject(Integer.toString(i)));
                            List<String> key = Arrays.asList("commodity_id", "commodity_name", "is_cd", "margin", "start_price","time");
                            List<String> value = new ArrayList<String>();
                            for (int k = 0; k < jsonObj.getJSONObject(Integer.toString(i)).length() - 3; k++) {
                                value.add(jsonObj.getJSONObject(Integer.toString(i)).getString(key.get(k)));
                            }
                            value.add(Integer.toString(jsonObj.getJSONObject(Integer.toString(i)).getInt("margin")));
                            value.add(Integer.toString(jsonObj.getJSONObject(Integer.toString(i)).getInt("start_price")));
                            System.out.println(value);
                            //从一个Activity的Fragment跳转到另外一个Activity
                            //https://www.jianshu.com/p/ab1cb7ddf91f
                            Intent intent = new Intent(getActivity(), AuctionDetailsActivity.class);
                            ArrayList<String> arrayListKey = new ArrayList<String>(key);
                            ArrayList<String> arrayListValue = new ArrayList<String>(value);
                            intent.putStringArrayListExtra("key", (ArrayList<String>) arrayListKey);
                            intent.putStringArrayListExtra("value", (ArrayList<String>) arrayListValue);
                            //标志，拍卖界面为1，记录界面2
                            intent.putExtra("flag","1");
                            startActivityForResult(intent, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }


    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            //点击checkBox1，拍卖中，如果被上勾isChecked为true，否则为false
            case R.id.checkBox1:
                isCd1 = isChecked;
                break;
            ////点击checkBox2，拍卖中，如果被上勾isChecked为true，否则为false
            case R.id.checkBox2:
                isCd2 = isChecked;
                break;

        }
        if (isCd1) {
            if (isCd2) {
                //都有，拍卖中and拍卖中
                mViewModel.getdata("3");
            } else {
                //拍卖中
                mViewModel.getdata("1");
            }
        } else {
            if (isCd2) {
                //待拍卖
                mViewModel.getdata("2");
            } else {
                //null
                mViewModel.getdata("0");
            }
        }
        //加载资源动画可见
        loading.setVisibility(View.VISIBLE);
    }


    //回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //BUG resultCode 得不到正确值，但是不影响功能

        System.out.println(requestCode + "," + resultCode + "====================");
        if (isCd1) {
            if (isCd2) {
                //都有，拍卖中and拍卖中
                mViewModel.getdata("3");
            } else {
                //拍卖中
                mViewModel.getdata("1");
            }
        } else {
            if (isCd2) {
                //待拍卖
                mViewModel.getdata("2");
            } else {
                //null
                mViewModel.getdata("0");
            }
        }
        //加载资源动画可见
        loading.setVisibility(View.VISIBLE);
    }

    class MyBaseAdapter extends BaseAdapter {
//        private int num = 0;

        //得到item的总数
        @Override
        public int getCount() {
            //返回ListView Item条目的总数
            return jsonObjNum;
        }

        //得到Item代表的对象
        @Override
        public JSONObject getItem(int position) {
            //返回ListView Item条目代表的对象
            try {
                return jsonObj.getJSONObject(Integer.toString(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //得到Item的id
        @Override
        public long getItemId(int position) {
            //返回ListView Item的id
            return position;
        }


        //得到Item的View视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.Title = (TextView) convertView.findViewById
                        (R.id.tv_it_title);
                holder.siv = (SmartImageView) convertView.findViewById(R.id.siv_icon);
                holder.Price = (TextView) convertView.findViewById(R.id.tv_it_price);
                holder.it_img = (ImageView) convertView.findViewById(R.id.it_img);
                holder.time = (TextView) convertView.findViewById(R.id.tv_it_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(jsonObj.getJSONObject(Integer.toString(position)).getString("commodity_name"));
                holder.siv.setImageUrl(Tools.getServerUrl() + "static/" + jsonObj.getJSONObject(Integer.toString(position)).getString("commodity_id") + "/img.png", R.drawable.ic_bg);
//                holder.siv.setImageUrl("http://10.0.2.2:5000/static/158290676652018/img.png", R.drawable.ic_bg);
                int s=jsonObj.getJSONObject(Integer.toString(position)).getInt("time");
                //发现有2秒延迟,加2秒
                Date date = new Date(new Date().getTime() + (s+2) * 1000);
                DateTime now = new DateTime();
                DateTime today_start = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0);
                DateTime today_end = today_start.plusDays(1);
                DateTime tomorrow_end=today_end.plusDays(1);
                System.out.println(date);
                if (jsonObj.getJSONObject(Integer.toString(position)).getString("is_cd").equals("1")) {
                    holder.it_img.setBackgroundResource(R.drawable.ic_act);
                    holder.Price.setText("当前价：¥" + jsonObj.getJSONObject(Integer.toString(position)).getInt("start_price"));
                    if(date.after(today_start.toDate())&&date.before(today_end.toDate())){
                        holder.time.setText("预计今天"+new DateTime(date).toString("HH:mm")+"结束");
                    }else if(date.after(today_end.toDate())&&date.before(tomorrow_end.toDate())){
                        holder.time.setText("预计明天"+new DateTime(date).toString("HH:mm")+"结束");
                    }else {
                        holder.time.setText("预计"+new DateTime(date).toString("yyyy-MM-dd HH:mm")+"结束");
                    }
                } else {
                    holder.it_img.setBackgroundResource(R.drawable.ic_wait);
                    holder.Price.setText("起拍价：¥" + jsonObj.getJSONObject(Integer.toString(position)).getInt("start_price"));
                    holder.Price.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                    if(date.after(today_start.toDate())&&date.before(today_end.toDate())){
                        holder.time.setText("预计今天"+new DateTime(date).toString("HH:mm")+"开始");
                    }else if(date.after(today_end.toDate())&&date.before(tomorrow_end.toDate())){
                        holder.time.setText("预计明天"+new DateTime(date).toString("HH:mm")+"开始");
                    }else {
                        holder.time.setText("预计"+new DateTime(date).toString("yyyy-MM-dd HH:mm")+"开始");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //发现会有两次循环
//            if (num < jsonObjNum - 1) {
//                num++;
//            } else {
//                num = 0;
//            }

            return convertView;
        }

        class ViewHolder {
            TextView Title;
            private TextView Price;
            SmartImageView siv;  //SmartImageView
            ImageView it_img;
            TextView time;
        }


    }

}
