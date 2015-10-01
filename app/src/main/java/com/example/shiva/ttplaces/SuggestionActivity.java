package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;


public class SuggestionActivity extends NavDrawer  {

    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName="userAnswers";
    private static final String ANSWER1="ansKey1";
    private static final String ANSWER2="ansKey2";
    private static final String ANSWER3="ansKey3";

    String ans1="",ans2="",ans3="";

    Spinner answer1,answer2,answer3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        answer1 = (Spinner) findViewById(R.id.spinner1);
        answer2 = (Spinner) findViewById(R.id.spinner2);
        answer3 = (Spinner) findViewById(R.id.spinner3);

        //creates 3 arrays to hold answer values for each of the three spinners
        List<String> decision1 = new ArrayList<>();
        decision1.add("Yes");
        decision1.add("A little");
        decision1.add("No");

        List<String> decision2 = new ArrayList<>();
        decision2.add("Beach");
        decision2.add("Museum");
        decision2.add("Waterfall/River");
        decision2.add("Historical Landmarks");
        decision2.add("N/A");

        List<String> decision3 = new ArrayList<>();
        decision3.add("Quiet");
        decision3.add("Social");
        decision3.add("Learning");
        decision3.add("N/A");


        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision1);
        dataAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer1.setAdapter(dataAdapter1);


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision2);
        dataAdapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer2.setAdapter(dataAdapter2);

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,decision3);
        dataAdapter3.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        answer3.setAdapter(dataAdapter3);


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
        ans1 = answer1.getSelectedItem().toString();
        ans2 = answer2.getSelectedItem().toString();
        ans3 = answer3.getSelectedItem().toString();

        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ANSWER1, ans1);
        editor.putString(ANSWER2, ans2);
        editor.putString(ANSWER3, ans3);
        editor.apply();

        runMainActivity();
    }
}

