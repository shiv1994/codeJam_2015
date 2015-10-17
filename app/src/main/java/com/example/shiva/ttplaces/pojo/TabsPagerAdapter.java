package com.example.shiva.ttplaces.pojo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	ArrayList<TourItem> tours = new ArrayList<>();
	public TabsPagerAdapter(FragmentManager fm,ArrayList<TourItem> ti) {
		super(fm);
		tours=ti;
	}

	@Override
	public Fragment getItem(int index) {

		TourItem item= tours.get(index);

        Bundle b;
        b = new Bundle();
        b.putString("url",item.getUrl());

		if(item.type==0) {
            AudioFragment audio = new AudioFragment();
            audio.setArguments(b);
            return audio;
        }
		else if(item.type==1) {
            ImageFragment image =new ImageFragment();
            image.setArguments(b);
            return image;
        }
		else if(item.type==2) {
            TextFragment text = new TextFragment();
            text.setArguments(b);
            return text ;
        }
		else if(item.type==3) {
            VideoFragment video = new VideoFragment();
            video.setArguments(b);
            return video ;
        }

		return null;
	}
	@Override
	public int getCount(){
		// get item count : equal to number of tabs
		return tours.size();
	}

}
