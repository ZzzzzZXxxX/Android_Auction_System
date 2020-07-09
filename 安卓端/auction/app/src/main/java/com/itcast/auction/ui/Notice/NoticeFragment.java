package com.itcast.auction.ui.Notice;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.itcast.auction.ContentActivity;
import com.itcast.auction.R;

import org.json.JSONException;
import org.json.JSONObject;


public class NoticeFragment extends Fragment {

    private NoticeViewModel mViewModel;
    private JSONObject jsonObj;
    private int jsonObjNum;
    ListView mListView;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root =inflater.inflate(R.layout.notice_fragment, container, false);
        mListView = (ListView) root.findViewById(R.id.lv_notice);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NoticeViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getJSONObject().observe(getViewLifecycleOwner(), new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                jsonObjNum = jsonObject.length();
                jsonObj = jsonObject;
                System.out.println(jsonObj);

                //创建Adapter的实例
                //设置Adapter
                mListView.setAdapter(new NoticeFragment.MyBaseAdapter());
                //点击Item
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), ContentActivity.class);
                        try {
                            intent.putExtra("content",jsonObj.getJSONObject(Integer.toString(i)).getString("content"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    class MyBaseAdapter extends BaseAdapter {

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
            NoticeFragment.MyBaseAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.notice_item, parent, false);
                holder = new NoticeFragment.MyBaseAdapter.ViewHolder();
                holder.Title = (TextView) convertView.findViewById
                        (R.id.tv_it_title_notice);
                holder.time = (TextView)convertView.findViewById(R.id.it_time_notice);

                convertView.setTag(holder);
            } else {
                holder = (NoticeFragment.MyBaseAdapter.ViewHolder) convertView.getTag();
            }
            try {
                holder.Title.setText(jsonObj.getJSONObject(Integer.toString(position)).getString("title"));
                holder.time.setText(jsonObj.getJSONObject(Integer.toString(position)).getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        class ViewHolder {
            TextView Title;
            TextView time;

        }


    }

}
