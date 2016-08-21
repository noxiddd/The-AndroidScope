package com.example.noxid.androidgraphingui;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {
    Button toggle,pause,save,auto_set;
    SeekBar seekBar;
    LineChart lineChart;
    ArrayList<Entry> entries=new ArrayList<>();
    boolean show_data_points=false;
    float MaxYValue=40;
    float MinYValue=-40;
    LineData data;
    LineDataSet dataSet;
    float start_time;
    float first_interval_x=0;
    float last_interval_x=0;
    boolean start_capture=true;
    boolean charting=true;
    float x_val=0;
    float y_val=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            public int progress_interval;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            //    progress=i;
              //  progress_interval=(progress/seekBar.getScrollBarSize())*5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lineChart=(LineChart) findViewById(R.id.chart);

        ////////////////////////////////////////////////

        /////////////////////////////////////////////////////
         dataSet=new LineDataSet(entries,"Channel 1");
        //dataSet.setCubicIntensity(1f);

        data=new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.BLACK);
        /////////////////////////////////////////
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
                { dataSet.setDrawCircles(false);
                  show_data_points=false;
                 }
                else if(show_data_points==false)
                { dataSet.setDrawCircles(true);
                  show_data_points=true;
                }

                dataSet.notifyDataSetChanged();

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });

        pause=(Button)findViewById(R.id.button_pause);
        pause.setText("PAUSE GRAPH");
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(pause.getText()=="PAUSE GRAPH")
                 {
                     pause.setText("START GRAPH");
                     charting =false;//pause chart
                 }
                  else if(pause.getText()=="START GRAPH")
                 {
                     pause.setText("PAUSE GRAPH");
                     charting=true;//start chart
                 }

            }
        });

        save=(Button)findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                lineChart.saveToGallery(timeStamp+"_Graph",50);
                Toast.makeText(MainActivity.this, timeStamp+"  Graph Saved To Gallery", Toast.LENGTH_LONG).show();
            }
        });

        auto_set=(Button)findViewById(R.id.auto_set);
        auto_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lineChart.moveViewTo(x_val,y_val, YAxis.AxisDependency.LEFT);
                lineChart.notifyDataSetChanged();

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
    public void onMessage(Splash.Message event){//recieves data from spalsh and sends it to charting method
        Log.d("progress",""+seekBar.getProgress());
        //updateChart(event.getX(),event.getY(),1f/*seekBar.getProgress()*/);
        //event.set_do_it(done);//for pausing graph
        x_val=event.getX();
        y_val=event.getY();
        if (charting==true)
           captureData(event.getX(),event.getY(),seekBar.getProgress()/10);
    }

    public void captureData(float x,float y,float interval)
    {

        data.addEntry(new Entry(x, y), 0);

        if(x>first_interval_x+interval && start_capture==false)
        {
            // start_capture=true;
             updateChart(x,y,interval);

            start_capture=true;
        } if (start_capture==true) {
            first_interval_x = x;
            start_capture=false;
            //dataSet.clear();
        }
        Log.i("first_interval",""+first_interval_x);
        Log.i("start_capture",""+start_capture);
    }

    void updateChart(float x,float y,float interval)
    {

        //data.addEntry(new Entry(x/1000,y),0);
        Log.i("y_main y_val_before",""+y);

        Log.i("x_incre y_val",""+x);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        //lineChart.setVisibleXRangeMaximum(4);
        //lineChart.moveViewTo(x,0, YAxis.AxisDependency.LEFT);
        //lineChart.moveViewToX(x);
        //lineChart.moveViewToY(x,y, YAxis.AxisDependency.LEFT);

        //data.clearValues();


    }



}