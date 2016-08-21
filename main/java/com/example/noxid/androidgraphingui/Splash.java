package com.example.noxid.androidgraphingui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;//library to pass data between activities

import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * Created by noxid on 7/14/16.
 */
public class Splash extends AppCompatActivity {


    ImageView iv;
    Button single,separated,double_,connect_arduino;
    TextView tv;
    String data_str="";
    float x=0;
    float y=0;
    boolean do_it=false;// controls whether charting happens or not
    boolean do_it2=true;// controls whether charting happens or not
    int count=0;
    boolean time_start=false;
    long time_started=0;
    long curent_time=0;
    private ConnectService connectService;
    private MyHandler mHandler;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ConnectService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            connectService = ((ConnectService.UsbBinder) arg1).getService();
            connectService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            connectService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mHandler = new MyHandler(this);

        iv=(ImageView)findViewById(R.id.image);
        iv.setImageResource(R.drawable.oscioscope_pic);//set the picture of the oscilioscope
        single=(Button)findViewById(R.id.single_splash);
        separated=(Button)findViewById(R.id.separated_splash);
        double_=(Button)findViewById(R.id.double_splash);
        connect_arduino=(Button)findViewById(R.id.connect);
        tv=(TextView)findViewById(R.id.textView);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);

        //connect_arduino.setBackgroundColor(Color.LTGRAY);
        connect_arduino.setText("CONNECT");//need this for button to work,fix it
        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//opens main activity single graph
                Toast.makeText(Splash.this, "SINGLE", Toast.LENGTH_SHORT).show();
                Intent single_view = new Intent(Splash.this,MainActivity.class);
                startActivity(single_view);

            }
        });

        separated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//opens twoChannels activity
                Toast.makeText(Splash.this, "SEPARATED", Toast.LENGTH_SHORT).show();
                Intent separate_view = new Intent(Splash.this,TwoChannels.class);
                startActivity(separate_view);
            }
        });

        double_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//make an activty for this-->both graphs on one graph
                Toast.makeText(Splash.this, "Double", Toast.LENGTH_SHORT).show();
            }
        });

        connect_arduino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(connect_arduino.getText()=="CONNECT") {


                 connect_arduino.setText("DISCONNECT");
             }
             else if(connect_arduino.getText()=="DISCONNECT")
             {

                 connect_arduino.setText("CONNECT");
             }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(ConnectService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!ConnectService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(ConnectService.ACTION_NO_USB);
        filter.addAction(ConnectService.ACTION_USB_DISCONNECTED);
        filter.addAction(ConnectService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(ConnectService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }


    /*
   * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
   */
    private static class MyHandler extends Handler {
        private final WeakReference<Splash> mActivity;

        public MyHandler(Splash splashActivity) {
            mActivity = new WeakReference<>(splashActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {///////use regex match to make sure string is numeric
                case ConnectService.MESSAGE_FROM_SERIAL_PORT:
                    String data_msg = (String) msg.obj;
                    float[] y_value=new float[6];
                    y_value[0]=10;
                    y_value[1]=10;

                    Log.i("Data_before",data_msg);
                    try {

                        y_value = mActivity.get().fix_string(data_msg);
                        if(mActivity.get().do_it==false)
                        {   break;}
                        Log.i("after_y",""+y_value[1]);
                        mActivity.get().updateChart(y_value[1]);

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    Log.i("Data_After",data_msg);


                    break;
                case ConnectService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case ConnectService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    void updateChart(float y)
    {
      //  data.addEntry(new Entry(x/1000,y),0);
        Log.i("y_incre y_val_before",""+y);
        Log.i("x_incre_before",""+x);
        if(time_start==false)
        {
            time_started=System.currentTimeMillis();//System.nanoTime();
            curent_time=System.currentTimeMillis();//System.nanoTime();
            x=(float)(curent_time-time_started);
            time_start=true;
        }
        else
        {
            curent_time=System.currentTimeMillis();//System.nanoTime();
            x=(float)(curent_time-time_started);
        }


        Log.i("x_incre",""+x);
        EventBus.getDefault().post(new Message(x/1000,y));
        do_it=false;

    }

    public float[] fix_string(String str)//concatenates the string 3 times, then get the middle and uses that value
    {
        float[] y_val=new float[100];//will this give an error????
        y_val[1]=10;
        y_val[0]=10;
        y_val[2]=10;
        if(count==0) {
            data_str = str;
            count=1;
            do_it=false;
        }
        else if(count==1)
        {  count = 2;
            data_str=data_str.concat(str);
            do_it=false;
        }
        else if(count==2)
        {
            try {
                data_str = data_str.concat(str);

                String[] split = data_str.split("\\s+");//should split up by whitespace
                for (int i = 0; i <= split.length - 1; i++) {

                    //split[i]=' '+split[i];
                    Log.i("fixi", "HERE" + "++" + split[i] + " i:" + i);
                    y_val[i] = Float.parseFloat(split[i]);//string length may not match
                    Log.i("fixi", "HERE");
                    Log.i("data_str2", split[i] + "++++" + y_val[0]);
                }
                Log.i("data_str_after-++",data_str);
                Log.i("data_str_after0", split[0] + "++++" + y_val[0]);
                Log.i("data_str_after1", split[1] + "++++" + y_val[1]);
                Log.i("data_str_after2", split[2] + "++++" + y_val[2]);
                do_it=true;
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
            count=0;
        }
      // if(do_it2==false)
        //   do_it=false;//this is a cheat
       // else if(do_it2==true)
        //   do_it=true;
        return y_val;
    }


    public class Message{//this allows data to be sent between ativies using
        private final float y;
        private final float x;
        public Message(float x,float y) {
            this.y=y;
            this.x=x;
        }

        public float getY() {
            return y;
        }

        public float getX() {
            return x;
        }


    }



}
