package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.shiva.ttplaces.pojo.MyPlace;
import com.example.shiva.ttplaces.pojo.NavDrawer;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import java.util.ArrayList;

public class HomeActivity extends NavDrawer {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        listView = (ListView) findViewById(R.id.lv_suggestions);
//        TODO: this list need to be populated maybe by some service
        ArrayList<MyPlace> list = new ArrayList<>();

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

     public void logOut(View view) {
        ParseUser.logOut();
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
        this.finish();
    }

    public void mapsActivity(View view) {
        Intent i = new Intent(this, MapsActivity.class);
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
            if(convertView == null)
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
              distance.setText("Distance: "+ (position+1) + "km");

            if(temp.getArea()==null) area.setText("Area");
            else   area.setText(temp.getArea());



            return convertView;
        }
    }
}
