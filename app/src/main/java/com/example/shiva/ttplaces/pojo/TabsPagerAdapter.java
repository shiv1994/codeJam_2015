package com.example.shiva.ttplaces.pojo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	ArrayList<TourItem> touri = new ArrayList<>();
	public TabsPagerAdapter(FragmentManager fm,ArrayList<TourItem> ti) {
		super(fm);
		touri=ti;
	}

	@Override
	public Fragment getItem(int index) {

		/*
		 return new touri.type ()
		where touri.type = HistoryFragement/ImageFragement.... etc
		*/

		TourItem item= touri.get(index);

		if(item.type==0)
			return new AudioFragment();

		else if(item.type==1)
			return new ImageFragment();

		else if(item.type==2)
			return new HistoryFragment();

		else if(item.type==3)
			return new VideoFragment();

		/*switch (index) {
			case 0:
				// History fragment activity
				return new HistoryFragment();
			case 1:
				// Video fragment activity
				return new VideoFragment();
			case 2:
				// Image fragment activity
				return new ImageFragment();
			case 3:
				// Audio fragment activity
				return new AudioFragment();
			case 4:
				// Image fragment activity
				return new ImageFragment();
		}*/
		return null;

	}
	@Override
	public int getCount(){
		// get item count - equal to number of tabs
		return touri.size();
	}

}
