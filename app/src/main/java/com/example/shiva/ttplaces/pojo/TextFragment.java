package com.example.shiva.ttplaces.pojo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shiva.ttplaces.R;

public class TextFragment extends Fragment {

	String url=null,text;
	Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        bundle=getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

		View rootView = inflater.inflate(R.layout.fragment_text, container, false);
		TextView txt=(TextView)rootView.findViewById(R.id.Content);

        if(url == null || url.trim().equals("")) {
            text="Content Currently Unavailable";
            System.out.println("Url is invalid");
        }
        else {//Extract text information from url to text file and store in string text   <----------------------------
            text = url;
        }
		txt.setText(text);
		txt.setTextColor(Color.parseColor("#000000"));
		
		return rootView;
	}
}
