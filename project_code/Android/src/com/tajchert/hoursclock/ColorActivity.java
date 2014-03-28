package com.tajchert.hoursclock;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.tajchert.hoursclock.calendar.SenderFactory;

public class ColorActivity extends Activity {
	
	private static ColorPicker picker;
	private static Button butSet;
	private static Button butOFF;
	private static Button buttonDemo;
	private static Button buttonCalendar;
	private static EditText rangeText;
	private static boolean isConn = false;
	private static SharedPreferences prefs;
	
	private static int colors[] = new int[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.color, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_color,container, false);
			prefs = getActivity().getSharedPreferences("com.tajchert.hoursclock", Context.MODE_PRIVATE);
			return rootView;
		}
		
		
		public void onResume(){
			   super.onResume();
			   new InitBluetooth().execute("");
			   picker = (ColorPicker) getActivity().findViewById(R.id.picker);
			   butSet = (Button) getActivity().findViewById(R.id.buttonSet);
			   butOFF= (Button) getActivity().findViewById(R.id.buttonOff);
			   buttonDemo= (Button) getActivity().findViewById(R.id.buttonDemo);
			   buttonCalendar= (Button) getActivity().findViewById(R.id.buttonCalendar);
			   butSet.setActivated(false);
			   butOFF.setActivated(false);
			   buttonDemo.setActivated(false);
			   buttonCalendar.setActivated(false);
			   
			   rangeText = (EditText) getActivity().findViewById(R.id.editTextRange);
			   
				butSet.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(isConn){
							prefs.edit().putBoolean(Data.SOURCE_CALENDAR, false).commit();
							//Toast msg = Toast.makeText(getActivity(),"You have clicked Button 1", Toast.LENGTH_LONG);
							//msg.show();
							//Log.d(Data.AWESOME_TAG, "!"+ picker.getColor());
							//String tmpStr = (picker.getColor()+"");
							//butSet.setBackgroundColor(picker.getColor());
							//Log.d(Data.AWESOME_TAG, "RED: "+ c.red(picker.getColor()));
							colors[0] = Color.red(picker.getColor());
							colors[1] = Color.green(picker.getColor());
							colors[2] = Color.blue(picker.getColor());
							
							String range = rangeText.getText()+"";
							
							if(range.contains("-")){
								int start = Integer.parseInt(range.split("-")[0]);
								int end = Integer.parseInt(range.split("-")[1]);
								Log.d(Data.AWESOME_TAG, "Start: "+ start + ", end: " + end);
								Log.d(Data.AWESOME_TAG, "Start: "+ String.format("%03d", start) + ", end: " + String.format("%03d", end));
								
								ClockBTManager.write(String.format("%03d", colors[0]) + "," + String.format("%03d", colors[1]) + "," + String.format("%03d", colors[2]) + ","+ String.format("%03d", start)+ ","+ String.format("%03d", end) + "r");
							
							}else{
								int tmpGot = Integer.parseInt(range);
								if(tmpGot>=0 && tmpGot < ClockColor.STRIP_LENGTH){
									ClockBTManager.write(String.format("%03d", colors[0]) + "," + String.format("%03d", colors[1]) + "," + String.format("%03d", colors[2]) + ","+ String.format("%03d", tmpGot) + "z");
								}
							}
						}
						
						//tmpStr = tmpStr.substring(0, tmpStr.length()-2);
						//col = new Color( Color.parseColor("#" + tmpStr));
					}
					
				});
				buttonDemo.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(isConn){
							prefs.edit().putBoolean(Data.SOURCE_CALENDAR, false).commit();
							ClockBTManager.write("62");
						}
					}
				});
				butOFF.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(isConn){
							prefs.edit().putBoolean(Data.SOURCE_CALENDAR, false).commit();
							ClockBTManager.write("60");
						}
					}
				});
				buttonCalendar.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Log.d(Data.AWESOME_TAG, "BBB");
						if(isConn){
							Log.d(Data.AWESOME_TAG, "AAA");
							prefs.edit().putBoolean(Data.SOURCE_CALENDAR, true).commit();
							new SenderFactory.UpdateView().execute(getActivity());
						}
					}
				});
			}
		public void onPause(){
	        super.onPause();
	        ClockBTManager.close();
	    }
		
		private class InitBluetooth extends AsyncTask<String, Void, String> {

	        @Override
	        protected String doInBackground(String... params) {
	        	if(ClockBTManager.socket == null || ClockBTManager.socket.isConnected() == false){
					   ClockBTManager.open(0);
				   }
	            return "Executed";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	isConn = ClockBTManager.socket.isConnected();
	        	if(isConn){
	        		rangeText.setBackgroundColor(Color.TRANSPARENT);
	        		butSet.setActivated(true);
					butOFF.setActivated(true);
					buttonDemo.setActivated(true);
					buttonCalendar.setActivated(true);
	        	}
			}

	        @Override
	        protected void onPreExecute() {
	        	rangeText = (EditText) getActivity().findViewById(R.id.editTextRange);
	        	rangeText.setBackgroundColor(Color.BLACK);
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
	}

}
