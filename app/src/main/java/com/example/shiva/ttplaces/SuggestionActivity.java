package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.shiva.ttplaces.pojo.NavDrawer;
import java.util.*;


public class SuggestionActivity extends NavDrawer {

    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName="userAnswers";
    private static final String ANSWER1="ansKey1";

    private static final String ANSWER2="ansKey2";
    private static final String ANSWER3="ansKey3";
    private static final String ANSWER4="ansKey4";
    private static final String ANSWER5="ansKey5";

    String ans1="", ans2="", ans3="", ans4="", ans5="";

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
        decision2.add("1");
        decision2.add("2");
        decision2.add("3");
        decision2.add("4");
        decision2.add("5");

        List<String> decision3 = new ArrayList<>(); //recreational environment
        decision3.add("N/A");
        decision3.add("1");
        decision3.add("2");
        decision3.add("3");
        decision3.add("4");
        decision3.add("5");

        List<String> decision4 = new ArrayList<>(); //religious environment
        decision4.add("N/A");
        decision4.add("1");
        decision4.add("2");
        decision4.add("3");
        decision4.add("4");
        decision4.add("5");

        List<String> decision5 = new ArrayList<>(); //remote environment
        decision5.add("N/A");
        decision5.add("1");
        decision5.add("2");
        decision5.add("3");
        decision5.add("4");
        decision5.add("5");


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
        ans1 = answer1.getSelectedItem().toString();

        //stores the rating (1-5) for each environment type
        ans2 = answer2.getSelectedItem().toString();
        ans3 = answer3.getSelectedItem().toString();
        ans4 = answer4.getSelectedItem().toString();
        ans5 = answer5.getSelectedItem().toString();


        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //stores the user's answers in the shared preferences file userAnswers
        editor.putString(ANSWER1, ans1);
        editor.putString(ANSWER2, ans2);
        editor.putString(ANSWER3, ans3);
        editor.putString(ANSWER4, ans4);
        editor.putString(ANSWER5, ans5);

        editor.apply();

        runMainActivity();

    }

}

