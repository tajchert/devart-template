#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>

//Author: Michal Tajchert
//Device: Bleduino - SparkFun DEV-12640 (Pro Micro)
//Bluetooth: HC-06
//LED Strip: 144 per meter RGB LEDs WS2812 (NeoPixel)

//For controlling strip
#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(144, PIN, NEO_GRB + NEO_KHZ800);


//For controlling bluetooth
const int RX_BT = 10;
const int TX_BT = 9;
const int RXLED = 17;

SoftwareSerial btSerial(RX_BT, TX_BT);

//For reading from BT
String tmp;  
char s;

//Pixel color
int R = 0;
int G = 0;
int B = 0;
int Num = 0;

void setup() {
  pinMode(RXLED, OUTPUT);
  Serial.begin(9600);
  Serial.println("USB Connected");
  btSerial.begin(9600);

  strip.begin();
  strip.show();
  //Test run
  theaterChase(strip.Color(  0,   0, 127), 50);
}
void loop() {
  // Read soft serial
  while (btSerial.available() > 0)  {
    s = btSerial.read();
    digitalWrite(RXLED, HIGH);
    if(s != '<' || s != '>'){
      tmp += s;
    }
    delay(2);
  }
  if(s == '<'){
    tmp = "";
    Serial.println("Cleaning");
  }
  else if(s == '>' && tmp.length() > 0){
    Serial.println("---");
    Serial.println(tmp);
    tmp = tmp.substring(1, tmp.length()-1);
    char lastOne = tmp[tmp.length()-1];
    Serial.println(tmp);
    Serial.println("---");
    if(lastOne == 'z'){
      
      //One LED
      Serial.println("One LED");
      tmp = tmp.substring(1, tmp.length());
      R = tmp.substring(0,3).toInt();
      G = tmp.substring(4,7).toInt();
      B = tmp.substring(8,11).toInt();
      Num = tmp.substring(11,15).toInt();
      Serial.println(Num);
      strip.setPixelColor(Num, strip.Color(R, G, B));
      strip.show();
      
    }
    else if(lastOne == 'r'){
      //Range
      Serial.println("RANGE");
      Serial.println(tmp);
      Serial.println(tmp.length());
      if(tmp.length()>=17){
        R = tmp.substring(0,3).toInt();
        G = tmp.substring(4,7).toInt();
        B = tmp.substring(8,11).toInt();
        int rangeEnd = abs(tmp.substring(12,15).toInt() - 139);
        int rangeStart = abs(tmp.substring(16,19).toInt() - 139) ;
        
        Serial.println(tmp.substring(12,15));
        Serial.println(tmp.substring(16,19));
        Serial.println("-TO-");
        Serial.println(rangeStart);
        Serial.println(rangeEnd);
        if(rangeEnd<rangeStart && rangeEnd==0){
          //Event ends at noon
          rangeEnd =139;
        }
        if(rangeStart < 140 && rangeEnd > 0 && rangeEnd < rangeStart){
          //Goes through top
          Serial.println("Over the top!");
          for(int i =rangeStart; i<140; i++){
            strip.setPixelColor(i, strip.Color(R, G, B));
          }
          for(int i =0; i<rangeEnd; i++){
            strip.setPixelColor(i, strip.Color(R, G, B));
          }
          strip.show();
        }
        else{
          //without top, normally
          for(int i =rangeStart; i<rangeEnd; i++){
            strip.setPixelColor(i, strip.Color(R, G, B));
          }
          strip.show();
        }
      }
    }
    else if(tmp.toInt() == 60){
      //Turn OFF
      Serial.println("All off");
      for(uint16_t i=0; i<strip.numPixels(); i++) {
        strip.setPixelColor(i, strip.Color(0, 0, 0));
      }
      strip.show();
      
    }
    else if(tmp.toInt() == 61){
      //DEMO
      Serial.println("DEMO");
      theaterChase(strip.Color(  0,   0, 127), 50);
       
    }
    else if(tmp.toInt() == 62){
      rainbowCycle(20);
    }

    Serial.println(tmp);
    tmp = "";
    delay(2);
  }
}
void rainbowCycle(uint8_t wait) {
  uint16_t i, j;

  for(j=0; j<256*5; j++) { // 5 cycles of all colors on wheel
    for(i=0; i< strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel(((i * 256 / strip.numPixels()) + j) & 255));
    }
    strip.show();
    delay(wait);
  }
}

//From Adafruit showreel
void theaterChase(uint32_t c, uint8_t wait) {
  for (int j=0; j<7; j++) {  //do 7 cycles of chasing
    for (int q=0; q < 10; q++) {
      for (int i=0; i < strip.numPixels(); i=i+5) {
        strip.setPixelColor(i+q, c);
      }
      strip.show();

      delay(wait);

      for (int i=0; i < strip.numPixels(); i=i+5) {
        strip.setPixelColor(i+q, 0);
      }
    }
  }
}

uint32_t Wheel(byte WheelPos) {
  if(WheelPos < 85) {
    return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
  } 
  else if(WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  } 
  else {
    WheelPos -= 170;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
}


