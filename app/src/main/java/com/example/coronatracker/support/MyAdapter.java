package com.example.coronatracker.support;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coronatracker.R;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private final ArrayList mData;

    public MyAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.menu.my_adapter, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        String newDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yy");
        String oldDate = item.getKey().replace("/","-");
        try {
            Date date = dateFormat.parse(oldDate);
            Format formatter = new SimpleDateFormat("dd-MM-yyyy");
            newDate = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((TextView) result.findViewById(android.R.id.text1)).setText("On Date: "+newDate+"- Cases: "+item.getValue());

        return result;
    }
}
