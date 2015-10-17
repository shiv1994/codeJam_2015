package com.example.shiva.ttplaces.pojo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shiva.ttplaces.R;

public class AudioFragment extends Fragment {

	String url="";
	Bundle bundle;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		bundle=getArguments();
		url=bundle.getString("url");
		View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
		
		return rootView;
	}

}
