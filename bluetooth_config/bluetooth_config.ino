void setup() {
Serial.begin(9600);
delay(2000); 

// AT+NAMEXXX where XXX is the new name
Serial.print("AT+Mouth");
delay(2000);

// AT+BAUDX where X from 1 to 8
// 1 -> 1200 Bauds
// 2 -> 2400 Bauds
// 3 -> 4800 Bauds
// 4 -> 9600 Bauds
// 5 -> 19200 Bauds
// 6 -> 38400 Bauds
// 7 -> 57600 Bauds
// 8 -> 115200 Bauds
Serial.print("AT+BAUD8"); 
}

void loop() {
  // put your main code here, to run repeatedly:

}
