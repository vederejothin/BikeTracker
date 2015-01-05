package com.example.biketracker;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void activateReceiver(View view) {
    	ComponentName receiver = new ComponentName(getApplicationContext(), WifiReceiver.class);
    	PackageManager pm = getPackageManager();
    	pm.setComponentEnabledSetting(receiver,
    	        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
    	        PackageManager.DONT_KILL_APP);
    	Toast.makeText(getApplicationContext(), "Tracker Activated", Toast.LENGTH_LONG).show();
    }
    
    public void deactivateReceiver(View view) {
    	ComponentName receiver = new ComponentName(getApplicationContext(), WifiReceiver.class);
    	PackageManager pm = getPackageManager();
    	pm.setComponentEnabledSetting(receiver,
    	        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    	        PackageManager.DONT_KILL_APP);
    	Toast.makeText(getApplicationContext(), "Tracker Deactivated", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
