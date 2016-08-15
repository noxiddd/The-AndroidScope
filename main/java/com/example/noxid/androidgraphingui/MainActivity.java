package com.example.noxid.androidgraphingui;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    Button toggle,pause,save;
    LineChart lineChart;
    ArrayList<Entry> entries=new ArrayList<>();
    boolean show_data_points=false;
    float MaxYValue=40;
    float MinYValue=-40;
    float x=0;
    float y=0;
    LineData data;
    LineDataSet dataSet;
    float prev_x=0;
    float prev_y=0;
    float start_time,now_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lineChart=(LineChart) findViewById(R.id.chart);

        ////////////////////////////////////////////////

        /////////////////////////////////////////////////////
         dataSet=new LineDataSet(entries,"Channel 1");
        //dataSet.setCubicIntensity(1f);

        data=new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.BLACK);
        /////////////////////////////////////////


        //lineChart.getaxis

        Legend legend=lineChart.getLegend();
        legend.setTextColor(Color.WHITE);//sets the graph axis numebers to white
        legend.setForm(Legend.LegendForm.CIRCLE);

        XAxis xAxis=lineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);//sets the graph number to white color
        xAxis.setAvoidFirstLastClipping(true);



        YAxis yAxis=lineChart.getAxisLeft();//set the maximum and minimum voltage for the scope at 40 and -40
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisMaxValue(MaxYValue);//max voltage
        yAxis.setAxisMinValue(MinYValue);


        lineChart.setData(data);

        dataSet.setDrawCircles(false);//removes large circular data points

        toggle=(Button)findViewById(R.id.button_toggle);//was to turn on and off points on graph-->not working
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"DATA POINTS",Toast.LENGTH_LONG).show();
                if(show_data_points==true)
                { dataSet.setDrawCircles(false);}
                else if(show_data_points==false)
                { dataSet.setDrawCircles(true);}

                dataSet.notifyDataSetChanged();

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });

        pause=(Button)findViewById(R.id.button_pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        save=(Button)findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineChart.saveToGallery("Graph",50);
                Toast.makeText(MainActivity.this, "Graph Saved To Gallery", Toast.LENGTH_LONG).show();
            }
        });
        //new Thread(new grapher()).start();//start the grapher thread to recieve from splash through intents
        //why dont i try to implement the onRecieve method from splash in this activity
          start_time=System.nanoTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.single_view_menu,menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId())//actions for the menu defined in mainactivity
        {
            case R.id.double_channel:
                Toast.makeText(MainActivity.this, "DOUBLE CHANNEL", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.separate_double_channel:
                Toast.makeText(MainActivity.this, "SEPARATE", Toast.LENGTH_SHORT).show();
                Intent separate_view = new Intent(MainActivity.this,TwoChannels.class);
                startActivity(separate_view);
                return true;
            case R.id.splash:
                Toast.makeText(MainActivity.this, "SPLASH", Toast.LENGTH_SHORT).show();
                Intent Splash_view = new Intent(MainActivity.this,Splash.class);
                startActivity(Splash_view);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }



    @Subscribe
    public void onMessage(Splash.Message event){
        //mytextview.setText(event.getMessage());
        updateChart(event.getX(),event.getY());
    }

    void updateChart(float x,float y)
    {
        data.addEntry(new Entry(x/1000,y),0);
        Log.i("y_incre y_val_before",""+y);

        Log.i("x_incre y_val",""+x);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.moveViewToX(x);
        // lineChart.moveViewToY(x,y, YAxis.AxisDependency.LEFT);
        lineChart.setVisibleXRangeMaximum(5f);


    }



}