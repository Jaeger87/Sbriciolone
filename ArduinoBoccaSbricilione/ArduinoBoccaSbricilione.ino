const byte Analogfilter = 6;
const byte delayLettura = 5;
const byte delayLoop = 50;
int aliveCounter = 0;
const byte aliveTrigger = 10;

struct Motor {
  int port;
  char sector;
  int pinH;
  char event = 'M';
  int oldValue = 0;
};

struct ButtonLed {
  int pin;
  int led;
  char sector;
  char event = 'S';
  boolean value;
};

const byte howmanyanalog = 9;//9
Motor listaMotori[howmanyanalog];
ButtonLed parlataButton; //non deve mai inviare alla testa
ButtonLed mirrorButton; //non deve mai inviare alla testa


void setup()
{
  Serial.begin(9600);

  mirrorButton.pin = 5;
  mirrorButton.led = 4;
  mirrorButton.sector = 'M';
  mirrorButton.value = false;


  parlataButton.pin = 7;
  parlataButton.led = 6;
  parlataButton.sector = 'M';
  parlataButton.value = false;

  pinMode(mirrorButton.pin, INPUT);
  pinMode(mirrorButton.led, OUTPUT);
  pinMode(parlataButton.pin, INPUT);
  pinMode(parlataButton.led, OUTPUT);


  listaMotori[0].sector = 'M';//BoccaSX
  listaMotori[0].port = A0;//
  listaMotori[0].pinH = 5;

  listaMotori[1].sector = 'M';//BoccaCSX
  listaMotori[1].port = A1;
  listaMotori[1].pinH = 6;

  listaMotori[2].sector = 'M';//BoccaC
  listaMotori[2].port = A2;
  listaMotori[2].pinH = 7;

  listaMotori[3].sector = 'M';//BoccaCDX
  listaMotori[3].port = A3;
  listaMotori[3].pinH = 8;

  listaMotori[4].sector = 'M';//BoccaDX
  listaMotori[4].port = A4;
  listaMotori[4].pinH = 9;

  listaMotori[5].sector = 'M';//NasoS
  listaMotori[5].port = A5;
  listaMotori[5].pinH = 10;

  listaMotori[6].sector = 'M';//NasoCS
  listaMotori[6].port = A6;
  listaMotori[6].pinH = 11;

  listaMotori[7].sector = 'M';//NasoCD
  listaMotori[7].port = A7;
  listaMotori[7].pinH = 12;

  listaMotori[8].sector = 'M';//NasoD
  listaMotori[8].port = A8;
  listaMotori[8].pinH = 13;


  readButtonLed(mirrorButton);
  readButtonLed(parlataButton);

}

void loop() {

  for (int i = 0; i < howmanyanalog; i++)
  {
    if (parlataButton.value)
      if (i == 0 || i == 1 || i == 3 || i == 4)
        continue;
    if (mirrorButton.value)
      if (i == 3 || i == 4)
        continue;
    readWriteMotor(listaMotori[i], i);
  }


  readButtonLed(mirrorButton);//non invia nulla alla testa
  readButtonLed(parlataButton);//non invia nulla alla testa
  deadManButton();

  delay(delayLoop);

}


void readWriteMotor(Motor& m, int index)
{
  int sensorValue = analogRead(m.port);

  if (abs(m.oldValue - sensorValue) > Analogfilter)
  {
    sendMotor(m, sensorValue);
    if (parlataButton.value)
    {
      sendMotor(listaMotori[0], parlataConversion(sensorValue, 180));
      sendMotor(listaMotori[1], parlataConversion(sensorValue, 100));
      sendMotor(listaMotori[3], parlataConversion(sensorValue, 100));
      sendMotor(listaMotori[4], parlataConversion(sensorValue, 180));
    }
    else if (mirrorButton.value)
      if (index == 0 || index == 1)
        sendMotor(listaMotori[4 - index], sensorValue);
  }

  m.oldValue = sensorValue;

}


void readButtonLed(ButtonLed& button)
{
  int lettura = digitalRead(button.pin);
  if (lettura == HIGH)
  {
    button.value = true;
    digitalWrite(button.led, HIGH);
  }
  else
  {
    button.value = false;
    digitalWrite(button.led, LOW);
  }
}

int parlataConversion(int value, int distanza)
{
  if (value < 612)
    return value - map(value,0,611,0,distanza);
  return value - map(value,612,1023,distanza,0);
}


void sendMotor(Motor& m, int sensorValue)
{
  Serial.print(m.sector);
  Serial.print(m.event);
  Serial.print(';');
  Serial.print(m.pinH);
  Serial.print(';');
  Serial.println(sensorValue);
  delay(delayLettura);

}

void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
