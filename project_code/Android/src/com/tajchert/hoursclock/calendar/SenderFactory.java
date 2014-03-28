package com.tajchert.hoursclock.calendar;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.tajchert.hoursclock.ClockBTManager;
import com.tajchert.hoursclock.ClockColor;
import com.tajchert.hoursclock.Data;


public class SenderFactory {
	
	private static int maxVal;
	private static int maxValColor;
	private static int minVal;
	private static int minValColor;

	private int col[] = new int[3];
	
	public static int prevColor;
	TreeMap<Long, Integer> colors = new TreeMap<Long, Integer>(); 
	public void draw(int startAngle, int EndAngle, long startDate){
		
		int colorId;
		int [] colorList = Data.colors_aggressive;
		
		colorId = (int) (Math.random() * colorList.length);
		while (colorId == closestUp(startDate, colors) || colorId == closestDown(startDate, colors)) {
			colorId = (int) (Math.random() * colorList.length);
		}
		if(startAngle>maxVal){
			maxVal = startAngle;
			while(colorId == minValColor){
				colorId = (int) (Math.random() * colorList.length);
			}
			maxValColor = colorId;
		}
		if(colors.size()>0 && startAngle < minVal){
			minVal = startAngle;
			while(colorId == maxValColor){
				colorId = (int) (Math.random() * colorList.length);
			}
			minValColor = colorId;
		}
		colors.put(startDate, colorId);
		prevColor = colorId;
		col[0] = Color.red(Data.colors_aggressive[colorId]);
		col[1] = Color.green(Data.colors_aggressive[colorId]);
		col[2] = Color.blue(Data.colors_aggressive[colorId]);
		String toSend = String.format("%03d", col[0]) + "," + String.format("%03d", col[1]) + "," + String.format("%03d", col[2]) + ","+ String.format("%03d", startAngle)+ ","+ String.format("%03d", EndAngle) + "r";
		ClockBTManager.write(toSend);
		Log.d(Data.AWESOME_TAG, toSend);
	}
	public int closestDown(long of, TreeMap<Long, Integer>in) {
	    long min = Integer.MAX_VALUE;
	    Integer closest = 0;
	    for(Entry<Long, Integer> entry : in.entrySet()) {
	    	  Long key = entry.getKey();
	    	  Integer value = entry.getValue();
	    	  if(key < of){
		    	  final long diff = Math.abs(key - of);
			        if (diff < min) {
			            min = diff;
			            closest = value;
			        }
	    	  }
	    	}
	    return closest;
	}
	public int closestUp(long of, TreeMap<Long, Integer>in) {
	    long min = Integer.MAX_VALUE;
	    Integer closest = 0;
	    for(Entry<Long, Integer> entry : in.entrySet()) {
	    	  Long key = entry.getKey();
	    	  Integer value = entry.getValue();
	    	  if(key> of){
		    	  final long diff = Math.abs(key - of);
			        if (diff < min) {
			            min = diff;
			            closest = value;
			        }
	    	  }
	    	}
	    return closest;
	}
	
	public static class UpdateView extends AsyncTask<Context, Void, String> {
		private boolean wasTurnedOn = false;
        @Override
        protected String doInBackground(Context... params) {
        	Log.d(Data.AWESOME_TAG, "doInBackground");
        	SharedPreferences prefs = params[0].getSharedPreferences("com.tajchert.hoursclock", Context.MODE_PRIVATE);
        	if(prefs.getBoolean(Data.SOURCE_CALENDAR, false)){
        		try {
            		ArrayList<CalendarObject> calendarsNames = CalendarContentResolver.getCalendars(params[0]);
            		
        			CalendarContentResolver calRevolver = new CalendarContentResolver(params[0]);
        			calRevolver.clear();
    				if (calendarsNames.size() > 0) {
    					for (int i = 0; i < calendarsNames.size(); i++) {
    						Log.d(Data.AWESOME_TAG, "name: " + calendarsNames.get(i).name +", id: " + calendarsNames.get(i).id);
    						if(calendarsNames.get(i).id==1 || calendarsNames.get(i).id==6)
    							calRevolver.testGet(params[0], calendarsNames.get(i).id,0);
    					}
    				}
    				Log.d(Data.AWESOME_TAG, "calRevolver.events.size(): " + calRevolver.events.size());
        			Calendar now = Calendar.getInstance();
        			if(calRevolver.events.size()>0){
        				ClockBTManager.write("60");
        				Thread.sleep(300); 
        			}
        			SenderFactory factorySend = new SenderFactory();
        			for(int i=0; i<calRevolver.events.size(); i++){
        				
        				//clock.draw(dateToDegrees(calRevolver.events.get(i).dateStart), dateToDegrees(calRevolver.events.get(i).dateEnd), canvas, opInner, transparencyOutColor, calRevolver.events.get(i).color, calRevolver.events.get(i).dateStart.getTimeInMillis(), size);
        				//int end = dateToDegrees(calRevolver.events.get(i).dateEnd);
        				//int start = dateToDegrees(calRevolver.events.get(i).dateStart);
        				int newStart = (dateToDegrees(calRevolver.events.get(i).dateStart) *(ClockColor.STRIP_LENGTH-1)/360);
        				int newEnd = (dateToDegrees(calRevolver.events.get(i).dateEnd) *(ClockColor.STRIP_LENGTH-1)/360);
        				if(newStart>0){
        					newStart -=1;
        				}
        				if(newEnd>0){
        					newEnd -=1;
        				}
        				//Log.d(Data.AWESOME_TAG, "Col: " + calRevolver.events.get(i).color);
        				
        				
        				//Log.d(Data.AWESOME_TAG, "Start: " + start +", end: " + end);
        				//Log.d(Data.AWESOME_TAG, "newStart: " + newStart +", newEnd: " + newEnd);
        				factorySend.draw(newStart, newEnd, calRevolver.events.get(i).dateStart.getTimeInMillis());
        				
        				Thread.sleep(500); 
        			}
        			Log.d(Data.AWESOME_TAG, "Ufff..");
        		} catch (Exception e) {
        			Log.d(Data.AWESOME_TAG, "Exception " + e.getMessage());
        			//Clock not added - no room or something
        		}
        	}
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        	if(!wasTurnedOn){
        		ClockBTManager.close();
        	}
        }

        @Override
        protected void onPreExecute() {
        	if(ClockBTManager.socket != null && ClockBTManager.socket.isConnected()){
        		wasTurnedOn = true;
        	}else{
        		ClockBTManager.open(0);
        	}
        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        public static int dateToDegrees(Calendar in){
    		int angle = 0;
    		int hour = in.get(Calendar.HOUR);
    		int minutes = in.get(Calendar.MINUTE);
    		angle = hour * 30;
    		angle += minutes * 0.5;
    		return angle;
    	}
    }
}
