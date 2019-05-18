package com.example.wandada.eddystonescanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//自定义适配器  view和数据的桥梁
public class CustomBeaconAdapter extends BaseAdapter {
    ArrayList<BeaconModel> list;
    Context context;

    public CustomBeaconAdapter(ArrayList<BeaconModel> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    //在此适配器中所代表的数据集中的条目数
    public int getCount() {
        return list.size();
    }

    @Override
    //获取数据集中与指定索引对应的数据项
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    //取在列表中与指定索引对应的行id
    public long getItemId(int position) {
        return position;
    }

    @Override
    //获取一个视图，显示数据集中指定位置的数据。
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater是用来加载布局的，用LayoutInflater的inflate方法就可以将你的item布局绘制出来。
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_beacon, null);

        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvDist = (TextView) view.findViewById(R.id.tv_dist);
        TextView tvns = (TextView) view.findViewById(R.id.tv_ns);
        TextView tvins = (TextView) view.findViewById(R.id.tv_ins);
        TextView tvtlm = (TextView) view.findViewById(R.id.tv_tlm);


        tvName.setText(list.get(position).getBeaconName());
        tvDist.setText(list.get(position).getDistance());
        tvns.setText(list.get(position).getNameSpace());
        tvins.setText(list.get(position).getInsTance());
        tvtlm.setText(list.get(position).getTLM());

        return view;
    }
}