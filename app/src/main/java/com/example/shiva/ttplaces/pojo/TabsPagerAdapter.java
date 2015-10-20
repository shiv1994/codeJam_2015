//This adapter is used to set up the tabs int the tour activity
// In tour activity, there are 4 fragments types: audio, text, images and videos

package com.example.shiva.ttplaces.pojo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	ArrayList<TourItem> tours = new ArrayList<>(); //List of all the tour items

	public TabsPagerAdapter(FragmentManager fm,ArrayList<TourItem> ti) {
		super(fm);
		tours=ti;
	}

	@Override
    //Gets the Content item at the the index provided.
    //This gets the relevant item and returns the correct item type to be created in the tour activity view
    public Fragment getItem(int index) {
		TourItem item= tours.get(index);//gets the tour item from list

        Bundle b;
        b = new Bundle();
        b.putString("url",item.getUrl());

		if(item.type==0) { //type 0 represents audio
            AudioFragment audio = new AudioFragment();
            audio.setArguments(b);
            return audio;
        }
		else if(item.type==1) { //type 1 represents text
            TextFragment text = new TextFragment();
            text.setArguments(b);
            return text ;
        }
        else if(item.type==2) { //type 2 represents image
            ImageFragment image =new ImageFragment();
            image.setArguments(b);
            return image;
        }
		else if(item.type==3) { //type 3 represents video
            VideoFragment video = new VideoFragment();
            video.setArguments(b);
            return video ;
        }

		return null;
	}
	@Override
	public int getCount(){ //the number of tabs indicate the number of content Items
		// number of tab is dynamic, in that depending on how many content items there are, a new fragment is created
		return tours.size();
	}

}
