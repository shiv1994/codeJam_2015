package com.example.shiva.ttplaces.pojo;

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

		if(item.type==0)
			return new AudioFragment();

		else if(item.type==1)
			return new ImageFragment();

		else if(item.type==2)
			return new HistoryFragment();

		else if(item.type==3)
			return new VideoFragment();
		return null;

	}
	@Override
	public int getCount(){
		// get item count : equal to number of tabs
		return tours.size();
	}

}
