package com.example.shiva.ttplaces;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;


public class SuggestionActivity extends AppCompatActivity implements OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        Spinner answer1 = (Spinner) findViewById(R.id.spinner1);
        Spinner answer2 = (Spinner) findViewById(R.id.spinner2);
        Spinner answer3 = (Spinner) findViewById(R.id.spinner3);

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


    public void onClick(View v) {

    }

}

