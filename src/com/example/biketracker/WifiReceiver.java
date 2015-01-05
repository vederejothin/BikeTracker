package com.example.biketracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String stringUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + context.getResources().getString(R.string.apiKey);
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new RetrieveLocationTask(context).execute(stringUrl);
		} else {
			String phone_no = context.getResources().getString(R.string.phoneNo);

			String connection_text = "Your bike is on the move!!!";

			try {
				SmsManager smsm = SmsManager.getDefault();
				smsm.sendTextMessage(phone_no, null, connection_text, null, null);
				Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Toast.makeText(context,"SMS sending failed.", Toast.LENGTH_SHORT).show();
			}
		} 
	}
	private class RetrieveLocationTask extends AsyncTask<String, Void, String> {
		
		private Context context;

		public RetrieveLocationTask(Context context) {
			this.context = context;
		}
		@SuppressLint("UseSparseArrays") @Override
		protected String doInBackground(String... urls) {
			HttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject jsonMain = new JSONObject();
	        ArrayList<JSONObject> wifiList = new ArrayList<JSONObject>();
	        HttpResponse resp;
	        String result;
	        WifiManager w = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	        List<ScanResult> scanResults = w.getScanResults();
	        result = scanResults.toString();
        	try {
        		for(ScanResult s : scanResults) {
        			JSONObject wifiObject = new JSONObject();        			
        			wifiObject.put("macAddress", s.BSSID);
        			wifiObject.put("signalStrength", s.level);
        			wifiObject.put("channel", s.frequency);
        			wifiList.add(wifiObject);
        		}
        		jsonMain.put("wifiAccessPoints", wifiList);
				post.setEntity(new StringEntity(jsonMain.toString()));
				post.setHeader("Content-type", "application/json");
		        resp = client.execute(post);
				BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
				    builder.append(line).append("\n");
				}
				JSONObject location = new JSONObject(builder.toString());
				JSONObject latlng = (JSONObject) location.get("location");
				result = latlng.getString("lat") + "+" + latlng.getString("lng");
;            } catch(Exception e) {
            	result = e.getMessage();
            }
			System.out.println(result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			String phone_no = context.getResources().getString(R.string.phoneNo);

			String location_text = result.equals("") && result.equals("No value for location") ? "" : "Bike found!!! Please click on link to see location.\n http://maps.google.com/maps?z=10&q=" + result;

			try {
				SmsManager smsm = SmsManager.getDefault();
				smsm.sendTextMessage(phone_no, null, location_text, null, null);
				Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Toast.makeText(context,"SMS sending failed.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
