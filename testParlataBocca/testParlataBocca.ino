

void setup() {
  Serial.begin(9600);
  for (int i = 0; i < 1024; i++)
  {
    Serial.println(i);
    Serial.print("Mediani  ");
    Serial.println(parlataConversion(i, 100));
    Serial.print("Laterali  ");
    Serial.println(parlataConversion(i, 180));
    Serial.println("--------");
  }

  int oldLateral = 0;
  int oldMedian = 0;

  for (int i = 0; i < 1024; i++)
  {
    int cMedian = parlataConversion(i, 100);
    int cLateral = parlataConversion(i, 180);
    if (cMedian < oldMedian || cLateral < oldLateral)
    {
      Serial.println(i);
      Serial.print("Mediani  ");
      Serial.println(parlataConversion(i, 100));
      Serial.print("Laterali  ");
      Serial.println(parlataConversion(i, 180));
      Serial.println("--------");
    }
    oldLateral = cLateral;
    oldMedian = cMedian;


  }
  Serial.println("Fine");
}

void loop() {
  Serial.println("Dio ");
  delay(1000);

}


int parlataConversion(int value, int distanza)
{
  if (value < 512)
    return value - map(value, 0, 511, 0, distanza);
  return value - map(value, 512, 1023, distanza, 0);
}
