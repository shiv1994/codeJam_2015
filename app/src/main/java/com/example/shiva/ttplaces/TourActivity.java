package com.example.shiva.ttplaces;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import com.example.shiva.ttplaces.pojo.TabsPagerAdapter;
import com.example.shiva.ttplaces.pojo.TourItem;

public class TourActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	public static ArrayList<TourItem> ti = new ArrayList<>();
	public static String txts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);

		// Initialization
        TourItem temp=new TourItem("History",2);
        ti.add(temp);
		txts="Mission Statement:\n\n" +
				"        To collect, preserve, document and display the fauna of Trinidad & Tobago\n\n\n" +
				"The Collections:\n\n" +
				"The collections consist of a wide range of specimens from all over Trinidad & Tobago and the wider Caribbean and South American region. There are specimens from all the major animal groups and some geological and archaeological objects as well.\n\n\n" +

                "The Beginnings:\n\n" +
                 "The collections held by the Zoology Museum date back to the 1920s. Before The University of the West Indies (UWI) existed the St. Augustine campus was home to the West Indies Agricultural College which in 1924 became the Imperial College of Tropical Agriculture (ICTA).\n" +
                "The early collection was basically a repository for researchers investigating animal species of agricultural importance and as a resource for teaching. Insects formed the bulk of the specimens and consisted of pest and beneficial species associated with the various crops under study – cocoa, maize, coffee, cotton, tobacco, citrus, pineapple and banana to name a few.\n" +
                "Many of these were collected by the Professor of Entomology and Commissioner of Agriculture Henry Arthur Ballou, a prolific author on many aspects of entomology throughout the Caribbean and beyond.\n";

        temp=new TourItem("Video",3);
        ti.add(temp);
        temp=new TourItem("Image",1);
        ti.add(temp);
        temp=new TourItem("Audio",0);
        ti.add(temp);

		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),ti);

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
		for (TourItem t : ti) {
			actionBar.addTab(actionBar.newTab().setText(t.getName())
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

}
