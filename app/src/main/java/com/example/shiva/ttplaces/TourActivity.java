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
    public static final String sharedPreferenceName = "userAnswers";
    public ViewPager viewPager = null;
    public TabsPagerAdapter mAdapter = null;
    public ActionBar actionBar;
    public ArrayList<TourItem> ti = new ArrayList<>();
    public int beaconId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

		getBeaconID();

		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();

        getItemContent();

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
            onBackPressed();
       }
        return super.onOptionsItemSelected(item);
    }

    
	public void getBeaconID(){
		sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
		beaconId = sharedPreferences.getInt("BeaconID", 1);
		Log.i("BEACON ID", "" + beaconId);
	}

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(TourActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

	public void dismissProgressDialog(){
		progressDialog.dismiss();
	}
	void getItemContent(){
		(new LoadContentData()).execute();
	}

    void getItemContent() {
        (new LoadContentData()).execute(Integer.toString(1));// <------ FOR TESTING ONLY ------- remove after testing --->>
        //(new LoadContentData()).execute(Integer.toString(beaconId));
    }

    public void updateUI() {
        for (TourItem t : ti) {
            actionBar.addTab(actionBar.newTab().setText(t.getName()).setTabListener(this));
        }
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), ti);
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.touricon2);
        actionBar.setTitle("LEAVE TOUR");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

	class LoadContentData extends AsyncTask<Void, Void, ArrayList<TourItem> > {

		protected ArrayList<TourItem> doInBackground(Void ... params){

            List<ParseObject> objects = null;
            ArrayList<TourItem> tours = new ArrayList<>();
            ParseQuery<ParseObject> findAllBeaconContent = ParseQuery.getQuery("BeaconContent");

			findAllBeaconContent.whereEqualTo("ID", beaconId);
			//Log.d("LoadContentDataTask",(params[0]));
			try {
				objects=findAllBeaconContent.find();
				//Log.d("LoadContentDataTask", "Found " + objects.size() + " Items");
			}
			catch(Exception e){
				//Log.e("LoadContentDataTask", "Error In receiving object occurred: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

            if (objects != null) {
                ParseObject temp = null;
                for (ParseObject obj : objects) {
                    try {
                        temp = findAllBeaconContent.get(obj.getObjectId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (temp != null) {
                        tours.add(new TourItem(temp.getString("Name"), temp.getInt("Type"), temp.getString("ContentLink")));
                    }
                }
            }
            return tours;
        }

        protected void onPreExecute() {
            showProgressDialog("Loading Content");
        }

        protected void onPostExecute(ArrayList<TourItem> tourItems) {
            //Log.d("LoadContentDataTask", "From the DoInBackground We received: " + tourItems.size());

            for (TourItem t : tourItems) {
                ti.add(t);
                //Log.d("LoadContentDataTask", "Name: " + t.getName() + " ToString: " + t.toString());
            }
            dismissProgressDialog();
            updateUI();
        }
    }

    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        this.finish();
    }
}

