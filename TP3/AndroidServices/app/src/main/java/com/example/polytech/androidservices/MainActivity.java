package com.example.polytech.androidservices;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private Button connectButton;
    private Button disconnectButton;
    private TextView textArea;

    private ServiceConnection connection;
    private IBackgroundServiceListener listener;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get components
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        textArea = (TextView) findViewById(R.id.editText);

        //Service listener
        listener = new IBackgroundServiceListener() {
            @Override
            public void dataChanged(final Object o) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textArea.setText(o.toString());
                    }
                });
            }
        };

        connection = new ServiceConnection() {
            IBackgroundService backgroundService = null;
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                backgroundService = ((BackgroundServiceBinder) iBinder).getService();
                backgroundService.addListener(listener);
                Log.i("MyApp", "Service connected");
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
               if(backgroundService != null)
               {
                   backgroundService.removeListener(listener);
               }
                bound = false;
                Log.i("MyApp", "Service disconnected");
            }
        };

        //Clicks on buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startService(new Intent(MainActivity.this, MyService.class));
                Log.i("MyApp", "Start");
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bound) //If someone is bound, unbind it first
                {
                    view.getContext().unbindService(connection);
                    connection.onServiceDisconnected(null);
                }
                view.getContext().stopService(new Intent(MainActivity.this, MyService.class));
                Log.i("MyApp", "Stop");
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().bindService(new Intent(MainActivity.this, MyService.class), connection, BIND_AUTO_CREATE);
            }
        });
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bound)
                {
                    view.getContext().unbindService(connection);
                    connection.onServiceDisconnected(null);
                }
            }
        });
    }
}
