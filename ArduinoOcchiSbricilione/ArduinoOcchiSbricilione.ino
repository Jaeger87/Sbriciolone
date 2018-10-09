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

const byte delayLettura = 4;
const byte delayLoop = 52;
const byte Analogfilter = 4;
const byte closeEyesButtonPin = 7;
int closeEyesState = 0;
int oldCloseEyesState = 0;


int aliveCounter = 0;
const byte aliveTrigger = 10;


const byte howmanyanalog = 8;//Sono 8 invero
Motor listaMotori[howmanyanalog];
Motor eyeSXX;
Motor eyeSXY;
ButtonLed mirrorButton;
ButtonLed palpebreButton;

void setup()
{
  mirrorButton.pin = 3;
  mirrorButton.led = 4;
  mirrorButton.sector = 'E';
  mirrorButton.value = false;


  palpebreButton.pin = 5;
  palpebreButton.led = 6;
  palpebreButton.sector = 'L';
  palpebreButton.value = false;

  pinMode(mirrorButton.pin, INPUT);
  pinMode(mirrorButton.led, OUTPUT);
  pinMode(palpebreButton.pin, INPUT);
  pinMode(palpebreButton.led, OUTPUT);
  pinMode(closeEyesButtonPin, INPUT);

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

  eyeSXX.sector = 'E';
  eyeSXX.port = -1;
  eyeSXX.pinH = 18;

  eyeSXY.sector = 'E';
  eyeSXY.port = -1;
  eyeSXY.pinH = 19;

  readButtonLed(mirrorButton);
  readButtonLedAndSend(palpebreButton);

  Serial.begin(9600);
}


void loop() {

  for (int i = 0; i < howmanyanalog; i++)
  {
    if (listaMotori[i].sector == 'L')
      if (!palpebreButton.value)
        continue;

    readWriteMotor(listaMotori[i]);
  }



  readCloseEyesButton();
  readButtonLed(mirrorButton);
  readButtonLedAndSend(palpebreButton);

  deadManButton();
  delay(delayLoop);
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

void readWriteMotor(Motor& m)
{
  int sensorValue = analogRead(m.port);

  if (abs(m.oldValue - sensorValue) > Analogfilter)
  {
    if (m.pinH == 22)
      sendMotor(eyeSXY, sensorValue);

    if (m.pinH == 21)
    {
      if (mirrorButton.value)
        sendMotor(eyeSXX, mirrorEye(sensorValue));
      else
        sendMotor(eyeSXX, sensorValue);
    }


    sendMotor(m, sensorValue);
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



void readButtonLedAndSend(ButtonLed& button)
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


int mirrorEye(int value)
{
  return map(value, 0, 1023, 1023, 0);
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
