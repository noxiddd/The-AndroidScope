package com.example.noxid.androidgraphingui;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by Jevaughn S. Dixon on 7/12/2016.
 */
public class TwoChannels extends AppCompatActivity {
    Button pause1,pause2,save1,save2;
    LineChart lineChart,lineChart2;
    ArrayList<Entry> entries=new ArrayList<>();
    LineDataSet dataSet;
    boolean show_data_points=false;
    float MaxYValue=40;
    float MinYValue=-40;
    float x=0;
    float y=0;
    int graph1count=0;
    int graph2count=0;
    LineData data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lineChart=(LineChart) findViewById(R.id.chart);
        lineChart2=(LineChart)findViewById(R.id.chart2);
        pause1=(Button)findViewById(R.id.button_pause);
        pause2=(Button)findViewById(R.id.button_pause2);
        save1=(Button)findViewById(R.id.button_save);
        save2=(Button)findViewById(R.id.button_save2);

        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//saves the upper graph to gallery
                if(graph1count==0)
                {lineChart.saveToGallery("UpperGraph",60);}
                else
                {
                    graph1count++;
                    lineChart.saveToGallery("UpperGraph"+graph1count,60);
                }
                Toast.makeText(TwoChannels.this, "Upper Graph Saved To Gallery", Toast.LENGTH_LONG).show();
            }
        });

        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//save the lower graph to gallery
                if(graph2count==0)
                {lineChart2.saveToGallery("LowerGraph",50);}
                else
                {
                    graph2count++;
                    lineChart2.saveToGallery("LowerGraph"+graph2count,50);
                }
                Toast.makeText(TwoChannels.this, "Lower Graph Saved To Gallery", Toast.LENGTH_LONG).show();

            }
        });


        dataSet=new LineDataSet(entries,"Channel 1");

        data=new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart2.setBackgroundColor(Color.BLACK);
        /////////////////////////////////////////


        //lineChart.getaxis

        Legend legend=lineChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);

        XAxis xAxis=lineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAvoidFirstLastClipping(true);



        YAxis yAxis=lineChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisMaxValue(MaxYValue);//max voltage
        yAxis.setAxisMinValue(MinYValue);


        Legend legend2=lineChart2.getLegend();
        legend2.setTextColor(Color.WHITE);
        legend2.setForm(Legend.LegendForm.CIRCLE);

        XAxis xAxis2=lineChart2.getXAxis();
        xAxis2.setTextColor(Color.WHITE);
        xAxis2.setAvoidFirstLastClipping(true);


        YAxis yAxis2=lineChart2.getAxisLeft();
        yAxis2.setTextColor(Color.WHITE);
        yAxis2.setAxisMaxValue(MaxYValue);//max voltage
        yAxis2.setAxisMinValue(MinYValue);

        dataSet.setDrawCircles(false);


        lineChart.setData(data);
        lineChart2.setData(data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.separate_view_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case R.id.double_channel:
                Toast.makeText(TwoChannels.this, "DOUBLE CHANNEL", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.single_channel:
                Toast.makeText(TwoChannels.this, "SINGLE", Toast.LENGTH_SHORT).show();
                Intent single_view = new Intent(TwoChannels.this,MainActivity.class);
                startActivity(single_view);
                return true;
            case R.id.splash:
                Toast.makeText(TwoChannels.this, "SPLASH", Toast.LENGTH_SHORT).show();
                Intent Splash_view = new Intent(TwoChannels.this,Splash.class);
                startActivity(Splash_view);
                return true;
            default:

                return super.onOptionsItemSelected(menuItem);
        }
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

        lineChart2.notifyDataSetChanged();
        lineChart2.invalidate();
        lineChart2.moveViewToX(x);
        // lineChart.moveViewToY(x,y, YAxis.AxisDependency.LEFT);
        lineChart2.setVisibleXRangeMaximum(5f);
    }

/*
    ArrayList<Entry> graphing()//plots the sine graph that goes into both upper and lower graphs
    {
        while(x<500)
        {
            entries.add(new Entry(x,y));
            x=x+0.1f;
            y=(float)Math.sin(x)*20;
        }
        return entries;
    }*/
}