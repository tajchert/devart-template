#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>
//For controlling strip
#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(60, PIN, NEO_GRB + NEO_KHZ800);

//For controlling bluetooth
const int TX_BT = 11;
const int RX_BT = 10;
SoftwareSerial btSerial(RX_BT, TX_BT);

//For reading from BT
String txtMsg = "";  
char s;

//Pixel color
int R = 0;
int G = 0;
int B = 0;
int Num = 0;

void setup() {
  strip.begin();
  strip.show();
  
  //Test run
  theaterChase(strip.Color(  0,   0, 127), 50);
  
  Serial.begin(9600);
  Serial.println("USB Connected");
  btSerial.begin(9600);
}
void loop() {
  if(btSerial.available()) {
    s=(char)btSerial.read();
        if(s == 'x'){
          //To prevent including old data, start every incoming task with 'x' to clean the buffer
          Serial.println("Cleaning"); 
          txtMsg = "";
        }
        if(s =='z' || s =='\n' || s =='r'){
          //Special characters used for particular tasks
          //z - single pixel
          //r - range of pixels
          //\n - others: all off, demo etc.
          
          txtMsg = txtMsg.substring(1, txtMsg.length());  //delete 'x'
          Serial.println(txtMsg);
          if(s == 'z'){
            //Single pixel
            if(txtMsg.length()>=14){
               R = txtMsg.substring(0,3).toInt();
               G = txtMsg.substring(4,7).toInt();
               B = txtMsg.substring(8,11).toInt();
               Num = txtMsg.substring(12,15).toInt();
               strip.setPixelColor(Num, strip.Color(R, G, B));
               strip.show();
            }
            txtMsg = "";
            Serial.println("Single pixel ON"); 
          }else if(s== 'r'){
            //Range
            if(txtMsg.length()>=17){
               R = txtMsg.substring(0,3).toInt();
               G = txtMsg.substring(4,7).toInt();
               B = txtMsg.substring(8,11).toInt();
               int rangeStart = txtMsg.substring(12,15).toInt();
               int rangeEnd = txtMsg.substring(16,19).toInt();
               if(rangeEnd<rangeStart && rangeEnd==0){
                 //Event ends at noon
                 rangeEnd =57;
               }if(rangeStart < 58 && rangeEnd > 0 && rangeEnd < rangeStart){
                 //Goes through top
                 Serial.println("Over the top!");
                 for(int i =rangeStart; i<58; i++){
                   strip.setPixelColor(i, strip.Color(R, G, B));
                 }
                 for(int i =0; i<rangeEnd; i++){
                   strip.setPixelColor(i, strip.Color(R, G, B));
                 }
                 strip.show();
               }else{
                 //without top, normally
                 for(int i =rangeStart; i<rangeEnd; i++){
                 strip.setPixelColor(i, strip.Color(R, G, B));
               }
               strip.show();
            }
            txtMsg = "";
            Serial.println("TURN ON RANGE"); 
          }else if(txtMsg.toInt() == 60){
            //Turn OFF
            for(uint16_t i=0; i<strip.numPixels(); i++) {
              strip.setPixelColor(i, strip.Color(0, 0, 0));
            }
            strip.show();
            txtMsg = "";
            Serial.println("TURN OFF"); 
          }else if(txtMsg.toInt() == 61){
            //DEMO
            //for(uint16_t i=0; i<strip.numPixels(); i++) {
            //  strip.setPixelColor(i, strip.Color(200, 200, 200));
            //}
            theaterChase(strip.Color(  0,   0, 127), 50);
            txtMsg = "";
            Serial.println("DEMO")
          }else{
            //mistake or not correct task
            txtMsg = "";
          }
        }else {
           //Not completed input task, keep on buffering 
            txtMsg +=s;
        }
  }
}

//From Adafruit showreel
void theaterChase(uint32_t c, uint8_t wait) {
  for (int j=0; j<7; j++) {  //do 7 cycles of chasing
    for (int q=0; q < 15; q++) {
      for (int i=0; i < strip.numPixels(); i=i+15) {
        strip.setPixelColor(i+q, c);
      }
      strip.show();
     
      delay(wait);
     
      for (int i=0; i < strip.numPixels(); i=i+15) {
        strip.setPixelColor(i+q, 0);
      }
    }
  }
}

uint32_t Wheel(byte WheelPos) {
  if(WheelPos < 85) {
   return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
  } else if(WheelPos < 170) {
   WheelPos -= 85;
   return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  } else {
   WheelPos -= 170;
   return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
}
