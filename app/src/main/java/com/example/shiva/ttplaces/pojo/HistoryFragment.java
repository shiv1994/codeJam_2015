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

public class HistoryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		TextView txt=(TextView)rootView.findViewById(R.id.histContent);
		txt.setText(TourActivity.txts);
		txt.setTextColor(Color.parseColor("#000000"));
		
		return rootView;
	}
}
