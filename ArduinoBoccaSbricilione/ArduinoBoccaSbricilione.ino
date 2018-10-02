const byte Analogfilter = 6;

int aliveCounter = 0;
const byte aliveTrigger = 10;

boolean mirror = false;
boolean parlata = false;

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
ButtonLed parlataButton;
ButtonLed mirrorButton;


void setup()
{
  Serial.begin(9600);

  listaMotori[0].sector = 'M';//BoccaSX
  listaMotori[0].port = A0;//
  listaMotori[0].pinH = 22;

  listaMotori[1].sector = 'M';//BoccaCSX
  listaMotori[1].port = A1;
  listaMotori[1].pinH = 21;

  listaMotori[2].sector = 'M';//BoccaC
  listaMotori[2].port = A2;
  listaMotori[2].pinH = 23;

  listaMotori[3].sector = 'M';//BoccaCDX
  listaMotori[3].port = A3;
  listaMotori[3].pinH = 23;

  listaMotori[4].sector = 'M';//BoccaDX
  listaMotori[4].port = A4;
  listaMotori[4].pinH = 23;

  listaMotori[5].sector = 'M';//NasoS
  listaMotori[5].port = A5;
  listaMotori[5].pinH = 23;

  listaMotori[6].sector = 'M';//NasoCS
  listaMotori[6].port = A6;
  listaMotori[6].pinH = 23;

  listaMotori[7].sector = 'M';//NasoCD
  listaMotori[7].port = A7;
  listaMotori[7].pinH = 23;

  listaMotori[8].sector = 'M';//NasoD
  listaMotori[8].port = A8;
  listaMotori[8].pinH = 23;



}

void loop() {

  for(int i = 0; i < howmanyanalog; i++)
  {
    if(mirror)
      if(i == 3 || i == 4)
        continue;
    readWriteMotor(listaMotori, i);
  }

  

  deadManButton();

  delay(50);

}


void readWriteMotor(Motor listaMotori[], int index)
{
  int sensorValue = analogRead(listaMotori[index].port);

  if (abs(listaMotori[index].oldValue - sensorValue) > Analogfilter)
  {
    sendMotor(listaMotori, index, sensorValue);
    if(mirror)
      if(index == 0 || index == 1)
        sendMotor(listaMotori, 4 - index, sensorValue);
  }

  listaMotori[index].oldValue = sensorValue;

}


void sendMotor(Motor listaMotori[], int index, int sensorValue)
{
    Serial.print(listaMotori[index].sector);
    Serial.print(listaMotori[index].event);
    Serial.print(';');
    Serial.print(listaMotori[index].pinH);
    Serial.print(';');
    Serial.println(sensorValue);

    delay(5);
  
}

void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
