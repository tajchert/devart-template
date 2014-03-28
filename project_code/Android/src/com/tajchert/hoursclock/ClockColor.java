package com.tajchert.hoursclock;

public class ClockColor {
	public static final String TURN_ALL_OFF = "60";
	public static final int STRIP_LENGTH = 140;
	
	public static String turnOff(){
		String res ="";
		for(int i=0;i<59;i++){
			if(i<10){
				res += "000,000,000,00"+i+"z\n";
			}else{
				res += "000,000,000,0"+i+"z\n";
			}
		}
		return res;
		
	}

}
