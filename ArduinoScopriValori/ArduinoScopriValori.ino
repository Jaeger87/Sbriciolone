#include <PololuMaestro.h>

#ifdef SERIAL_PORT_HARDWARE_OPEN
#define maestroSerial SERIAL_PORT_HARDWARE_OPEN
#else
#include <SoftwareSerial.h>
SoftwareSerial maestroSerial(10, 11);
#endif
MiniMaestro maestro(maestroSerial);


void setup() {
  Serial.begin(9600);
  maestroSerial.begin(9600);
  Serial.setTimeout(20);

}

void loop() {
  String message = Serial.readStringUntil('\n');
  if (message.length() > 0)
  {
    int value = message.toInt();
    Serial.println(value);
    maestro.setTarget(23, value);
  }
}


