package com.example.shailu.locationfetching.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shailu.locationfetching.R;

import java.util.ArrayList;

import static com.example.shailu.locationfetching.Activity.ImageAdapter.CATEGORYTYPE1;


/**
 * Created by shailu on 5/5/16.
 */
public class IndexActivity extends AppCompatActivity {


    private static final String TAG = IndexActivity.class.getSimpleName();
    public static final ArrayList<Integer> CATEGORYTYPE = new ArrayList<>();

    private Button applyFilter;
    public static AppBarLayout appBarLayout;
    public static ImageButton backButtonEvent;
    public  TextView resetButton;

    private GridView listView;
    ImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        backButtonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SparseBooleanArray checked = listView.getCheckedItemPositions();
//                for (int i = 0; i < checked.size(); i++) {
//
//                    int position = checked.keyAt(i);
//                    if (checked.valueAt(i))
//                        CATEGORYTYPE.add(position);
//                }

                if (CATEGORYTYPE1.size() > 0) {
                    Intent inti = new Intent(IndexActivity.this, LocationService.class);
                    IndexActivity.this.startService(inti);
                    finish();
                } else
                    Toast.makeText(IndexActivity.this, "Please select a filter first", Toast.LENGTH_SHORT).show();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void initViews() {

        listView = (GridView) findViewById(R.id.list);
        String[] category = getResources().getStringArray(R.array.subcategorytype);
        adapter = new ImageAdapter(this, category);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
//        for (int i = 0; i < CATEGORYTYPE.size(); i++) {
//            int index = 0;
//            for (int j = 0; j < category.length; j++) {
//                if (CATEGORYTYPE.get(i).equals(category[j])) {
//                    index = j;
//                    break;
//                }
//            }
//            listView.setItemChecked(index, true);
//        }


        applyFilter = (Button) findViewById(R.id.apply_event_filter_btn);

        backButtonEvent = (ImageButton) findViewById(R.id.backButtonEvent);
        resetButton = (TextView) findViewById(R.id.action_reset);


    }
}

