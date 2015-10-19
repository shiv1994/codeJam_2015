package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.shiva.ttplaces.pojo.NavDrawer;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.*;


public class SuggestionActivity extends NavDrawer {

    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName="userAnswers";
    private static final String ANSWER1="ansKey1";

    private static final String ANSWER2="ansKey2";
    private static final String ANSWER3="ansKey3";
    private static final String ANSWER4="ansKey4";
    private static final String ANSWER5="ansKey5";
    private static final String sharedPrefExistKey ="sharedPrefExistKey";

    String ansCountry="";
    int ansEducational=-1, ansRecreational=-1, ansReligious=-1, ansRemote=-1;

    Spinner answer1, answer2, answer3, answer4, answer5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        answer1 = (Spinner) findViewById(R.id.answer1);
        answer2 = (Spinner) findViewById(R.id.answer2);
        answer3 = (Spinner) findViewById(R.id.answer3);
        answer4 = (Spinner) findViewById(R.id.answer4);
        answer5 = (Spinner) findViewById(R.id.answer5);


        //creates 5 arrays to hold answer values for each of the 4 spinners
        List<String> decision1 = new ArrayList<>();
        decision1.add("Antigua and Barbuda");
        decision1.add("Argentina");
        decision1.add("Australia");
        decision1.add("Bahamas");
        decision1.add("Barbados");
        decision1.add("Belize");
        decision1.add("Canada");
        decision1.add("Cuba");
        decision1.add("Dominica");
        decision1.add("Dominican Republic");
        decision1.add("France");
        decision1.add("Germany");
        decision1.add("Grenada");
        decision1.add("Guyana");
        decision1.add("Haiti");
        decision1.add("India");
        decision1.add("Italy");
        decision1.add("Jamaica");
        decision1.add("Japan");
        decision1.add("Maldives");
        decision1.add("Mexico");
        decision1.add("Netherlands");
        decision1.add("New Zealand");
        decision1.add("Panama");
        decision1.add("Peru");
        decision1.add("Portugal");
        decision1.add("Russia");
        decision1.add("St. Kitts and Nevis");
        decision1.add("St. Lucia");
        decision1.add("St. Vincent and The Grenadines");
        decision1.add("Spain");
        decision1.add("Suriname");
        decision1.add("Switzerland");
        decision1.add("Trinidad and Tobago");
        decision1.add("Turkey");
        decision1.add("United Arab Emirates");
        decision1.add("United Kingdom");
        decision1.add("United States of America");
        decision1.add("Venezuela");
        decision1.add("Vietnam");
        decision1.add("Not Listed");

        List<String> decision2 = new ArrayList<>(); //educational environment
        decision2.add("N/A");
        decision2.add("Not at all");
        decision2.add("A little");
        decision2.add("Somewhat");
        decision2.add("A lot");
        decision2.add("Very much");


        List<String> decision3 = new ArrayList<>(); //recreational environment
        decision3.add("N/A");
        decision3.add("Not at all");
        decision3.add("A little");
        decision3.add("Somewhat");
        decision3.add("A lot");
        decision3.add("Very much");


        List<String> decision4 = new ArrayList<>(); //religious environment
        decision4.add("N/A");
        decision4.add("Not at all");
        decision4.add("A little");
        decision4.add("Somewhat");
        decision4.add("A lot");
        decision4.add("Very much");


        List<String> decision5 = new ArrayList<>(); //remote environment
        decision5.add("N/A");
        decision5.add("Not at all");
        decision5.add("A little");
        decision5.add("Somewhat");
        decision5.add("A lot");
        decision5.add("Very much");


        //spinners for each of the questions

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision1);
        dataAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer1.setAdapter(dataAdapter1);


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision2);
        dataAdapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer2.setAdapter(dataAdapter2);


        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision3);
        dataAdapter3.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer3.setAdapter(dataAdapter3);

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision4);
        dataAdapter4.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer4.setAdapter(dataAdapter4);

        ArrayAdapter<String> dataAdapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision5);
        dataAdapter5.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer5.setAdapter(dataAdapter5);

    }

    public void runMainActivity(){
        Intent i = new Intent(this , HomeActivity.class);
        startActivity(i);
        this.finish();
    }

    public void notNow(View view){
        runMainActivity();
    }

    public void finish(View view) {

        //stores the choices the user selected
        ansCountry = answer1.getSelectedItem().toString();
        //stores the rating (1-5) for each environment type
        ansEducational = answer2.getSelectedItemPosition();
        ansRecreational = Integer.parseInt(Integer.toString(answer3.getSelectedItemPosition()));
        ansReligious = Integer.parseInt(Integer.toString(answer4.getSelectedItemPosition()));
        ansRemote = Integer.parseInt(Integer.toString(answer5.getSelectedItemPosition()));

        updateUserCountry(ansCountry);

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //stores the user's answers in the shared preferences file userAnswers
        editor.putString(ANSWER1, ansCountry);

        if(ansEducational==0)
            editor.putInt(ANSWER2, -1);

        editor.putInt(ANSWER2, ansEducational);

        if(ansRecreational == 0)
            editor.putInt(ANSWER3, -1);

        editor.putInt(ANSWER3, ansRecreational);

        if(ansReligious == 0)
            editor.putInt(ANSWER4, -1);

        editor.putInt(ANSWER4, ansReligious);

        if(ansRemote == 0)
            editor.putInt(ANSWER5, -1);

        editor.putInt(ANSWER5, ansRemote);

        //sets to true when user clicks finish
        editor.putBoolean(sharedPrefExistKey, true);
        editor.apply();

        runMainActivity();

    }

    private void updateUserCountry(String country){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.put("country",country);
            currentUser.saveInBackground();
        }
    }
}

