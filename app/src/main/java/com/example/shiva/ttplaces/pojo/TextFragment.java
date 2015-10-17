package com.example.shiva.ttplaces.pojo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shiva.ttplaces.R;
import com.example.shiva.ttplaces.TourActivity;

public class TextFragment extends Fragment {

	String url="";
	Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        bundle=getArguments();
        url=bundle.getString("url");
		View rootView = inflater.inflate(R.layout.fragment_text, container, false);
		TextView txt=(TextView)rootView.findViewById(R.id.histContent);
		txt.setText(url);
		txt.setTextColor(Color.parseColor("#000000"));
		
		return rootView;
	}
}
