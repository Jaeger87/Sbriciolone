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

const byte delayLettura = 5;
const byte Analogfilter = 6;
const byte closeEyesButtonPin = 7;
int closeEyesState = 0;
int oldCloseEyesState = 0;

const byte loopPalpebreButton = 5;
const byte loopPalpebreButtonLed = 6;
int loopPalpebreState = 0;
int oldloopPalpebreState = 0;
bool loopPalpebre = true;


int aliveCounter = 0;
const byte aliveTrigger = 10;


const byte howmanyanalog = 8;
Motor listaMotori[howmanyanalog];
ButtonLed mirrorButton;


void setup()
{
  mirrorButton.pin = 3;
  mirrorButton.led = 4;
  mirrorButton.sector = 'E';
  mirrorButton.value = false;

  pinMode(mirrorButton.pin, INPUT);
  pinMode(mirrorButton.led, OUTPUT);
  pinMode(closeEyesButtonPin, INPUT);
  pinMode(loopPalpebreButton, INPUT);
  pinMode(loopPalpebreButtonLed, OUTPUT);


  listaMotori[0].sector = 'E';//OcchioDXY
  listaMotori[0].port = A0;//
  listaMotori[0].pinH = 22;

  listaMotori[1].sector = 'E';//OcchioDXX
  listaMotori[1].port = A1;
  listaMotori[1].pinH = 21;

  listaMotori[2].sector = 'L';//PalpebraDestra
  listaMotori[2].port = A2;
  listaMotori[2].pinH = 23;

  listaMotori[3].sector = 'B';//Sopraciglio DXD
  listaMotori[3].port = A3;//
  listaMotori[3].pinH = 15;

  listaMotori[4].sector = 'B';//Sopraciglio DXC
  listaMotori[4].port = A4;
  listaMotori[4].pinH = 14;

  listaMotori[5].sector = 'B';//Sopraciglio SXS
  listaMotori[5].port = A5;//
  listaMotori[5].pinH = 17;

  listaMotori[6].sector = 'B';//Sopraciglio SXC
  listaMotori[6].port = A6;
  listaMotori[6].pinH = 16;

  listaMotori[7].sector = 'L';//PalpebraSinistra
  listaMotori[7].port = A7;
  listaMotori[7].pinH = 20;


  loopPalpebreState = digitalRead(loopPalpebreButton);
  if (loopPalpebreState == HIGH)
  {
    loopPalpebre = true;
    digitalWrite(loopPalpebreButtonLed, HIGH);
  }
  else
  {
    loopPalpebre = false;
    digitalWrite(loopPalpebreButtonLed, LOW);
  }

  //readButtonLed(mirrorButton);


  Serial.begin(9600);
}


void loop() {

  for (int i = 0; i < howmanyanalog; i++)
  {
    if (listaMotori[i].sector == 'L')
      if (loopPalpebre)
        continue;

    readWriteMotor(i);
  }



  readCloseEyesButton();
  readLoopPalpebreButton();

  deadManButton();
  delay(40);
}


void readLoopPalpebreButton()
{
  loopPalpebreState = digitalRead(loopPalpebreButton);
  if (loopPalpebreState != oldloopPalpebreState)
    if (loopPalpebreState == LOW)
    {
      Serial.println("LS;0");
      digitalWrite(loopPalpebreButtonLed, LOW);
      loopPalpebre = false;
    }
    else
    {
      Serial.println("LS;1");
      digitalWrite(loopPalpebreButtonLed, HIGH);
      loopPalpebre = true;
    }

  oldloopPalpebreState = loopPalpebreState;
}

void readCloseEyesButton()
{
  closeEyesState = digitalRead(closeEyesButtonPin);

  if (closeEyesState != oldCloseEyesState)
    if (closeEyesState == LOW)
    {
      Serial.println("LE");
    }

  oldCloseEyesState = closeEyesState;
}

void readWriteMotor(int index)
{
  int sensorValue = analogRead(listaMotori[index].port);

  if (abs(listaMotori[index].oldValue - sensorValue) > Analogfilter)
    sendMotor(index, sensorValue);

  listaMotori[index].oldValue = sensorValue;
}


void readButtonLed(ButtonLed button)
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



void readButtonLedAndSend(ButtonLed button)
{
  int lettura = digitalRead(button.pin);
  if (lettura == HIGH && button.value != true)
  {
    button.value = true;
    digitalWrite(button.led, HIGH);
    Serial.print(button.sector);
    Serial.print(button.event);
    Serial.print(';');
    Serial.println('1');
    delay(delayLettura);
  }
  else
  {
    button.value = false;
    digitalWrite(button.led, LOW);
    Serial.print(button.sector);
    Serial.print(button.event);
    Serial.print(';');
    Serial.println('0');
    delay(delayLettura);
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
