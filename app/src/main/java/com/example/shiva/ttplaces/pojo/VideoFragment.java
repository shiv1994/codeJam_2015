package com.example.shiva.ttplaces.pojo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shiva.ttplaces.R;

public class VideoFragment extends Fragment implements View.OnClickListener {

    String url = null;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        bundle = getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        TextView txt=(TextView)rootView.findViewById(R.id.Vlink);
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.Vlink){
            if(url == null || url.trim().equals("")) {
                Toast toast= Toast.makeText(getActivity(), "Sorry This Video Is Currently Unavailable", Toast.LENGTH_LONG);
                toast.show();
                System.out.println("Url is invalid");
            }
            else {
                Intent viewVideo = new Intent("android.intent.action.VIEW", Uri.parse(url));
                startActivity(viewVideo);
            }
        }
    }
}