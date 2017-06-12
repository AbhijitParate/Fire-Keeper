package com.github.abhijitpparate.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.abhijitpparate.checklistview.CheckListView;

public class MainActivity extends AppCompatActivity {

    CheckListView checkListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        checkListView = (CheckListView) findViewById(R.id.checkListView);


    }
}
