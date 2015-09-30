package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity{
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        listView = (ListView) findViewById(R.id.lv_suggestions);
//        TODO: this list need to be populated maybe by some service
        ArrayList<MyPlace> list = new ArrayList<MyPlace>();

        loadTestPlaces(list);
        myAdapter adapter = new myAdapter(this,list);
        listView.setAdapter(adapter);
    }

//    TODO: remove this function after testing
    private void loadTestPlaces(ArrayList<MyPlace> list){
        String name = "Place ",area = "Area ",type = "Type ";
        for (int i = 0; i < 10 ; i++){
            MyPlace mp = new MyPlace(name+i, type+i, area+i ,null);
            list.add(mp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     public void logOut(View view) {
        ParseUser.logOut();
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
        this.finish();
    }

    public void mapActivity(View view) {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }

    public void runSuggestions(View view) {
        Intent i = new Intent(this, SuggestionActivity.class);
        startActivity(i);
    }


    class myAdapter extends BaseAdapter {
        ArrayList<MyPlace> list;
        Context ctx;
        myAdapter(Context context,ArrayList<MyPlace> list) {
            ctx=context;
            this.list=list;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_suggested,parent,false);
            TextView name=(TextView) convertView.findViewById(R.id.tv_name);
            TextView type=(TextView) convertView.findViewById(R.id.tv_type);
            TextView distance=(TextView) convertView.findViewById(R.id.tv_distance);
            TextView area=(TextView) convertView.findViewById(R.id.tv_area);
            MyPlace temp= list.get(position);

            if(temp.getName()==null) name.setText("Place");
            else   name.setText("Name: " + temp.getName());

            if(temp.getType()==null) type.setText("Unknown");
            else   type.setText("Type:" + temp.getType());

//           TODO: calculate distance from current location using latlng in MyPlace object
              distance.setText("Distance: "+ (position+1) + "km away");

            if(temp.getArea()==null) area.setText("Area");
            else   area.setText(temp.getArea());



            return convertView;
        }
    }
}
