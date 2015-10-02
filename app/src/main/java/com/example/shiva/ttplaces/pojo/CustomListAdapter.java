package com.example.shiva.ttplaces.pojo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shiva.ttplaces.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] m_item;
    private final Integer[] m_img;

    public CustomListAdapter(Activity context, String[] m_item, Integer[] m_img) {
        super(context, R.layout.drawer_list_item, m_item);
        this.context=context;
        this.m_item=m_item;
        this.m_img=m_img;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.drawer_list_item, null,true);

        TextView MenuItem = (TextView) rowView.findViewById(R.id.MenuItem);
        ImageView MenuIcon = (ImageView) rowView.findViewById(R.id.MenuIcon);

        MenuItem.setText(m_item[position]);
        MenuIcon.setImageResource(m_img[position]);
        return rowView;
    }
}