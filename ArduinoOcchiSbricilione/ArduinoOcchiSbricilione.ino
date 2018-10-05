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

const byte howmanyanalog = 3;
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

  Serial.begin(9600);
}


void loop() {

  for (int i = 0; i < howmanyanalog; i++)
  {
    if (listaMotori[i].sector == 'L')
      if (loopPalpebre)
        continue;

    readWriteMotor(listaMotori, i);
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

void readWriteMotor(Motor listaMotori[], int index)
{
  int sensorValue = analogRead(listaMotori[index].port);

  if (abs(listaMotori[index].oldValue - sensorValue) > Analogfilter)
  {
    Serial.print(listaMotori[index].sector);
    Serial.print(listaMotori[index].event);
    Serial.print(';');
    Serial.print(listaMotori[index].pinH);
    Serial.print(';');
    Serial.println(sensorValue);

    delay(5);
  }

  listaMotori[index].oldValue = sensorValue;

}

void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
