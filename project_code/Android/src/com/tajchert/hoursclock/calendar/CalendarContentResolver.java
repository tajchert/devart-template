package com.tajchert.hoursclock.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class CalendarContentResolver {
	public static ArrayList<CalendarObject> calendars = new ArrayList<CalendarObject>();
	public  ArrayList<Event> events = new ArrayList<Event>();

	public static final String[] FIELDS = { "_id", "calendar_displayName", "visible", "sync_events", "calendar_color", "ownerAccount"};

	public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");

	static ContentResolver contentResolver;

	public CalendarContentResolver(Context ctx) {
		contentResolver = ctx.getContentResolver();
		events.clear();
	}

	public static ArrayList<CalendarObject> getCalendars(Context context) {
		calendars = new ArrayList<CalendarObject>();
		contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
		try {
			if (cursor.getCount() > 0) {
				CalendarObject newOne;
				while (cursor.moveToNext()) {
					newOne = new CalendarObject();
					newOne.id = cursor.getInt(0);
					newOne.name = cursor.getString(1);
					newOne.isSync = !cursor.getString(3).equals("0");
					newOne.isVisible = !cursor.getString(2).equals("0");
					newOne.color  = cursor.getInt(4);
					newOne.owner = cursor.getString(5);
					calendars.add(newOne);
				}
			}
		} catch (AssertionError ex) {
		}
		try {
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
		return calendars;
		
	}
	public void clear(){
		events.clear();
	}
	public ArrayList<Event> testGet(Context context, int calendarId, int color){
		long nowTime = Calendar.getInstance().getTimeInMillis();
		long nextDay = nowTime + (1000 * 60 * 60 * 24);// 24h
		long halfday = nowTime + (1000 * 60 * 60 * 12);//12h
		String[] projection = new String[] { "calendar_id", "title", "begin", "end", "description", "event_id"};
		Cursor calCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/instances/when/"+nowTime+"/"+halfday), projection,"calendar_id = "+calendarId, null,null);
		
		Event tmpOne = new Event();
		if (calCursor.moveToFirst()) {
			do {
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(calCursor.getLong(2));
				Calendar tmpE = Calendar.getInstance();
				tmpE.setTimeInMillis(calCursor.getLong(3));
				if(tmpE.getTimeInMillis()- nextDay < 0 && tmpE.getTimeInMillis() - tmp.getTimeInMillis()< (1000 * 60 * 60 * 12)){
					tmpOne = new Event();
					tmpOne.dateStart = tmp;
					if(tmp.getTimeInMillis()<nowTime){
						tmpOne.dateStart.setTimeInMillis(nowTime);
					}
					tmpOne.dateEnd = tmpE;
					if(tmpE.getTimeInMillis()>halfday){
						tmpE.setTimeInMillis(halfday);
						tmpOne.dateEnd = tmpE;
					}
					tmpOne.calendarId = calCursor.getInt(0);
					tmpOne.title = calCursor.getString(1);
					tmpOne.description = calCursor.getString(4);
					int colorTmp = color;
					int colorEvent = getEventColor(context, Integer.parseInt(calCursor.getString(5)));
					if(colorTmp == 0 && colorEvent != 0){
						colorTmp = colorEvent;
						
					}
					tmpOne.color = colorTmp;
					events.add(tmpOne);
					
					Log.d("24hours", "tmpOne.color : " + tmpOne.color );
				}
			} while (calCursor.moveToNext());
			
		}
		try{
			if( calCursor != null && !calCursor.isClosed() ){
				calCursor.close();
			}
		} catch (Exception e) {
		}
		return events;
	}
	public int getEventColor(Context context, int eventid) {
		int color = 0;
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
				new String[] { "calendar_id", "eventColor", "_id" }, "_id = " + eventid , null, null);
		String CNames[] = new String[cursor.getCount()];
		cursor.moveToFirst();
		for (int i = 0; i < CNames.length; i++) {
			color = cursor.getInt(1);
		}
		try{
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
		return color;
	}
	
	public void getEvents(Context context, String calendarName, int color) {
		Event tmpOne = new Event();
		long nowTime = Calendar.getInstance().getTimeInMillis();
		long nextDay = nowTime + (1000 * 60 * 60 * 24);// 24h
		long halfday = nowTime + (1000 * 60 * 60 * 12);//12h
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
				new String[] { "calendar_id", "title", "description","dtstart", "dtend", "eventLocation", "selfAttendeeStatus",
						"calendar_displayName"}, "dtstart >= " + nowTime + " AND dtend <= " + nextDay +" AND calendar_displayName = \""+calendarName+"\"", null, null);
		String CNames[] = new String[cursor.getCount()];
		cursor.moveToFirst();
		for (int i = 0; i < CNames.length; i++) {
			if (cursor.getInt(6) != 2) {
				Log.d("24hours", "C2");
				tmpOne = new Event();
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(cursor.getLong(3));
				Calendar tmpp = Calendar.getInstance();
				tmpp.setTimeInMillis(cursor.getLong(4));
				tmpOne.dateStart = tmp;
				if(tmp.getTimeInMillis()<nowTime){
					tmpOne.dateStart.setTimeInMillis(nowTime);
				}

				Log.d("24hours", "C: "+ tmpOne.dateStart.getTimeInMillis() +" - " +nowTime);
				if(tmpp.getTimeInMillis()>halfday){
					tmpp.setTimeInMillis(halfday);
					tmpOne.dateEnd = tmpp;
				}else{
					tmpOne.dateEnd = tmpp;
				}
				tmpOne.title = cursor.getString(1);
				tmpOne.description = cursor.getString(2);
				tmpOne.color = color;
				events.add(tmpOne);
			}
			cursor.moveToNext();
		}
		try{
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",java.util.Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}