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
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.example.shiva.ttplaces.pojo.TabsPagerAdapter;
import com.example.shiva.ttplaces.pojo.TourItem;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TourActivity extends FragmentActivity implements ActionBar.TabListener {
    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName = "userAnswers";
    private ViewPager viewPager = null;
    private TabsPagerAdapter mAdapter = null;
    private ActionBar actionBar;
    private ArrayList<TourItem> ti = new ArrayList<>();
    private int beaconId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        //We attempt to obtain the beacon id upon running the activity so we know which items in the database to reference.
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
    }

    public void showProgressDialog(String message) { //loads content to put in tour activity
        progressDialog = new ProgressDialog(TourActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

	public void dismissProgressDialog(){  //when the content is finished loaded the dialog is dismissed
		progressDialog.dismiss();
	}
	void getItemContent(){ //gets the content and loads it
		(new LoadContentData()).execute();
	}

    public void updateUI() { //updates the ui with the necessary content fo te tabs and icons
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

    //This async task allows us to retrieve data from the Parse system on a background thread.
	class LoadContentData extends AsyncTask<Void, Void, ArrayList<TourItem> > {

        //Loading the content from the parse database with the idof the beacon.
		protected ArrayList<TourItem> doInBackground(Void ... params){

            List<ParseObject> objects = null;
            ArrayList<TourItem> tours = new ArrayList<>();
            ParseQuery<ParseObject> findAllBeaconContent = ParseQuery.getQuery("BeaconContent");

			findAllBeaconContent.whereEqualTo("ID", beaconId);
			try {
				objects=findAllBeaconContent.find();
			}
			catch(Exception e){
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

        //We would like ot show a loading screen while the content is being downloaded to the device.
        protected void onPreExecute() {
            showProgressDialog("Loading Content");
        }

        //After the content has been loaded, we can now dismiss the dialog and update the UI with the items obtained for that specific beacon.
        protected void onPostExecute(ArrayList<TourItem> tourItems) {
            for (TourItem t : tourItems) {
                ti.add(t);
            }
            dismissProgressDialog();
            updateUI();
        }
    }

    public void onBackPressed() { //when you leave the tour activity you go back to the home page
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        this.finish();
    }
}

