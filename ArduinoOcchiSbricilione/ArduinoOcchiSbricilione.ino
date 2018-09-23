const byte Analogfilter = 6;

const byte testAnalog0 = A0;
const byte testAnalog1 = A1;
const byte analogPalpebraDXPin = A2;
const byte closeEyesButtonPin = 5;
int closeEyesState = 0;
int oldCloseEyesState = 0;

const byte loopPalpebreButton = 5;
const byte loopPalpebreButtonLed = 6;
int loopPalpebreState = 0;
int oldloopPalpebreState = 0;
bool loopPalpebre = true;


int aliveCounter = 0;
const byte aliveTrigger = 10;

void setup()
{
  pinMode(closeEyesButtonPin, INPUT);
  pinMode(loopPalpebreButton, INPUT);
  pinMode(loopPalpebreButtonLed, OUTPUT);

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


int sensorValue01Old = 0;
int sensorValue0Old = 0;
int analogPalpebraDXOLD = 0;

void loop() {

  int sensorValue01 = analogRead(testAnalog1);
  int sensorValue0 = analogRead(testAnalog0);
  
  if (abs(sensorValue01Old - sensorValue01) > Analogfilter)
  {
    Serial.print("A;22;");
    Serial.println(sensorValue01);
  }

  if (abs(sensorValue0Old - sensorValue0) > Analogfilter)
  {
    Serial.print("A;21;");
    Serial.println(sensorValue0);
  }

  if (!loopPalpebre)
  {
    int analogPalpebraDX = analogRead(analogPalpebraDXPin);
    if (abs(analogPalpebraDXOLD - analogPalpebraDX) > Analogfilter)
    {
      Serial.print("A;23;");
      Serial.println(analogPalpebraDX);
    }
    analogPalpebraDXOLD = analogPalpebraDX;
  }
  
  //readCloseEyesButton();
  readLoopPalpebreButton();
  sensorValue01Old = sensorValue01;
  sensorValue0Old = sensorValue0;

  deadManButton();
  delay(40);
}

void readLoopPalpebreButton()
{
  loopPalpebreState = digitalRead(loopPalpebreButton);
  if (loopPalpebreState != oldloopPalpebreState)
    if (loopPalpebreState == LOW)
    {
      Serial.println("S;P;0");
      digitalWrite(loopPalpebreButtonLed, LOW);
      loopPalpebre = false;
    }
    else
    {
      Serial.println("S;P;1");
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
      Serial.println("E;O");
    }

  oldCloseEyesState = closeEyesState;
}

void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}

