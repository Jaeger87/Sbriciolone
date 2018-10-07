const byte Analogfilter = 6;
const byte delayLettura = 5;
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

const byte howmanyanalog = 9;
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
    if (mirrorButton.value)
      if (i == 3 || i == 4)
        continue;
    readWriteMotor(listaMotori, i);
  }


  readButtonLed(mirrorButton);//non invia nulla alla testa
  readButtonLed(parlataButton);//non invia nulla alla testa
  deadManButton();

  delay(50);

}


void readWriteMotor(Motor listaMotori[], int index)
{
  int sensorValue = analogRead(listaMotori[index].port);

  if (abs(listaMotori[index].oldValue - sensorValue) > Analogfilter)
  {
    sendMotor(index, sensorValue);
    if (mirrorButton.value)
      if (index == 0 || index == 1)
        sendMotor(4 - index, sensorValue);
  }

  listaMotori[index].oldValue = sensorValue;

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

void sendMotor(int index, int sensorValue)
{
  Serial.print(listaMotori[index].sector);
  Serial.print(listaMotori[index].event);
  Serial.print(';');
  Serial.print(listaMotori[index].pinH);
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
