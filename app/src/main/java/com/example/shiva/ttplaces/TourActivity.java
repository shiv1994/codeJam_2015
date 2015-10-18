package com.example.shiva.ttplaces;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.example.shiva.ttplaces.pojo.TabsPagerAdapter;
import com.example.shiva.ttplaces.pojo.TourItem;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TourActivity extends FragmentActivity implements ActionBar.TabListener {
	public SharedPreferences sharedPreferences;
	public static final String sharedPreferenceName="userAnswers";
	public ViewPager viewPager=null;
	public TabsPagerAdapter mAdapter=null;
	public ActionBar actionBar;
	public ArrayList<TourItem> ti = new ArrayList<>();
	public int beaconId;
	ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);

		getBeaconID();
		Log.d("ASFTTTTXEETTEEEEEEE    ", "logggg");
//		for(TourItem t:ti){
//			Log.d("ASFTTTTXEETTEEEEEEE    ", t.getName());
//			Log.d("AFFFFTEERECCEEEEE    ", t.toString());
//		}
		ti.add(new TourItem("randItem",1,"url"));
		for(int i =0;i<ti.size();i++ ){
			Log.d("Dooo innn baccroundddd" ,ti.get(i).getName());
			Log.d("Dooo baccgroundddd    " , ti.get(i).toString());
		}
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),ti);

		pleaseWork();
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.touricon2);
        actionBar.setTitle("LEAVE TOUR");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        // Adding Tabs

		Log.d("adddddd tabssss    ", "");


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent i = new Intent(this , HomeActivity.class);
            startActivity(i);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

	public void getBeaconID(){
		sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
		beaconId = sharedPreferences.getInt("BeaconID",-1);
	}

	public void showProgressDialog(String message){
		progressDialog = new ProgressDialog(TourActivity.this);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	public void dismissProgressDialog(){
		progressDialog.dismiss();
	}
	void pleaseWork(){
		(new LoadContentData()).execute(Integer.toString(1));
	}

	public void updateUI(){
		for (TourItem t : ti) {
			actionBar.addTab(actionBar.newTab().setText(t.getName()).setTabListener(this));
		}
	}

	class LoadContentData extends AsyncTask<String, Void, ArrayList<TourItem> > {

		protected ArrayList<TourItem> doInBackground(String ... params){

			List<ParseObject> objects =null;
			ArrayList<TourItem> tours = new ArrayList<>();
			ParseQuery<ParseObject> findAllBeaconContent = ParseQuery.getQuery("BeaconContent");

			findAllBeaconContent.whereEqualTo("ID", Integer.parseInt(params[0]));
			Log.d("LoadContentDataTask",(params[0]));
			try {
				objects=findAllBeaconContent.find();
				Log.d("LoadContentDataTask", "Found " + objects.size() + " Items");
			}
			catch(Exception e){
				Log.e("LoadContentDataTask", "Error In receiving object occurred: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			if(objects!=null){
				ParseObject temp=null;
				for(ParseObject obj : objects){
					try {
						temp = findAllBeaconContent.get(obj.getObjectId());
					}
					catch(Exception e){
						e.printStackTrace();
					}
					if(temp!=null) {
						tours.add(new TourItem(temp.getString("Name"), temp.getInt("Type"), temp.getString("ContentLink")));
					}
				}
			}
			for(int i =0;i<tours.size();i++ ){
				Log.d("LoadContentDataTask" , "Tour Name: "+ tours.get(i).getName());
				Log.d("LoadContentDataTask", "Tour:" + tours.get(i).toString());
			}

			return tours;
		}

		protected void onPreExecute(){
			showProgressDialog("Loading Content");
		}

		protected void onPostExecute(ArrayList<TourItem> tourItems){
			Log.d("LoadContentDataTask", "From the DoInBackground We received: " + tourItems.size());

				for(TourItem t:tourItems){
					ti.add(t);
					Log.d("LoadContentDataTask", "Name: " + t.getName() + " ToString: " + t.toString());
				}
			dismissProgressDialog();
			updateUI();
			//up
		}
	}
}


