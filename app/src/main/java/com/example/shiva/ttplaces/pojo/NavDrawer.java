package com.example.shiva.ttplaces.pojo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shiva.ttplaces.HomeActivity;
import com.example.shiva.ttplaces.LoginRegisterActivity;
import com.example.shiva.ttplaces.MapsActivity;
import com.example.shiva.ttplaces.R;
import com.example.shiva.ttplaces.SuggestionActivity;
import com.parse.ParseUser;

public class NavDrawer extends AppCompatActivity {

    protected DrawerLayout baseLayout;
    protected FrameLayout activityContent;
    private ListView DrawerList;
    private ActionBarDrawerToggle DrawerToggle;
    private DrawerLayout drawerLayout;
    private String ActivityTitle;

    @Override
    public void setContentView(int layoutResID) {
        //Base layout of the activity i.e the navigation menu and the the template to hold the activity content
        baseLayout= (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_nav_drawer, null);

        //Activity content relevant to the specific activity that uses the navigation drawer
        activityContent= (FrameLayout) baseLayout.findViewById(R.id.activityContent);

        //Sets the content of activity layout to the template holder in base layout
        getLayoutInflater().inflate(layoutResID, activityContent, true);
        super.setContentView(baseLayout);

        //Gets the list of navigation items, the navigation drawer layout and the activity title
        DrawerList = (ListView)findViewById(R.id.navList);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

       assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i= new Intent();
                if (position == 0){
                    i=new Intent(NavDrawer.this,HomeActivity.class);
                }
                if (position == 1) {
                   i=new Intent(NavDrawer.this,SuggestionActivity.class);
                }
                if (position == 2) {
                    i = new Intent(NavDrawer.this, MapsActivity.class);
                }
                if (position == 3) {
                    Toast.makeText(NavDrawer.this, "SEE YOU SOON :)", Toast.LENGTH_SHORT).show();
                    ParseUser.logOut();
                    i = new Intent(NavDrawer.this, LoginRegisterActivity.class);
                }
                startActivity(i);
                NavDrawer.this.finish();
            }
        });
    }

    private void addDrawerItems(){
        String[] menuList = { "HOME", "PREFERENCES", "VIEW MAP","LOG OUT"};
        Integer[] icons = {R.drawable.home2,R.drawable.settings2,R.drawable.view2,R.drawable.logout2};

        CustomListAdapter Adapter= new CustomListAdapter(this, menuList, icons);
        DrawerList.setAdapter(Adapter);
    }

    private void setupDrawer() {
       assert getSupportActionBar() != null;
        DrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(ActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        DrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(DrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (DrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        DrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,HomeActivity.class);
        startActivity(i);
        this.finish();
    }
}
