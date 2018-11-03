#include <SoftwareSerial.h>
SoftwareSerial BTSerial(10, 11); // RX | TX

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  for (int i = 0; i < 1024; i++)
  {
    BTSerial.println(i);
    BTSerial.print("Mediani  ");
    BTSerial.println(parlataConversion(i, 100));
    BTSerial.print("Laterali  ");
    BTSerial.println(parlataConversion(i, 180));
    BTSerial.println("--------");
  }

  int oldLateral = 0;
  int oldMedian = 0;

  for (int i = 0; i < 1024; i++)
  {
    int cMedian = parlataConversion(i, 100);
    int cLateral = parlataConversion(i, 180);
    if (cMedian < oldMedian || cLateral < oldLateral)
    {
      BTSerial.println(i);
      BTSerial.print("Mediani  ");
      BTSerial.println(parlataConversion(i, 100));
      BTSerial.print("Laterali  ");
      BTSerial.println(parlataConversion(i, 180));
      BTSerial.println("--------");
    }
    oldLateral = cLateral;
    oldMedian = cMedian;


  }
  BTSerial.println("Fine");
}

void loop() {
  BTSerial.println("Dio ");
  delay(1000);

}


int parlataConversion(int value, int distanza)
{
  if (value < 512)
    return value - map(value, 0, 511, 0, distanza);
  return value - map(value, 512, 1023, distanza, 0);
}
