const byte testAnalog0 = A0;
const byte testAnalog1 = A1;


int aliveCounter = 0;
const byte aliveTrigger = 10;

void setup()
{
  Serial.begin(9600);

}


int sensorValue01Old = 0;
int sensorValue0Old = 0;


void loop() {

  int sensorValue01 = analogRead(testAnalog1);
  int sensorValue0 = analogRead(testAnalog0);

  if (abs(sensorValue01Old - sensorValue01) > 10)
  {
    Serial.print("A;11;");
    Serial.println(sensorValue01);
  }

  if (abs(sensorValue0Old - sensorValue0) > 10)
  {
    Serial.print("A;5;");
    Serial.println(sensorValue0);
  }

  sensorValue01Old = sensorValue01;
  sensorValue0Old = sensorValue0;

  delay(25);

}


void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}

