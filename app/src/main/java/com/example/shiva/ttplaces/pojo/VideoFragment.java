/*
This fragment is used to load and display Video contents for the interactive tour Activity
 */
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

//Video Fragment utilizes the onClickListener to get selection from user
public class VideoFragment extends Fragment implements View.OnClickListener {

    String url = null; //url to store video link
    Bundle bundle; //bundle containing url loaded by the tour activity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Get contents of the bundle
        bundle = getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

        //Instantiates the view and stores it in rootView
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        //Gets text view from layout and Sets attributes for clickable text to start video
        TextView txt=(TextView)rootView.findViewById(R.id.Vlink);
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setOnClickListener(this);
        return rootView;
    }

    @Override

    public void onClick(View v) {

        //OnclickListener to start the video, responds to the user clicking the video link
        if(v.getId()==R.id.Vlink){// If the item clicked is the video link
            if(url == null || url.trim().equals("")) { //If link is invalid sends response to notif
                Toast toast= Toast.makeText(getActivity(), "Sorry This Video Is Currently Unavailable", Toast.LENGTH_LONG);
                toast.show();
                System.out.println("Url is invalid");
            }
            else {//Starts the video in user browser
                Intent viewVideo = new Intent("android.intent.action.VIEW", Uri.parse(url));
                startActivity(viewVideo);
            }
        }
    }
}