#include <PololuMaestro.h>

unsigned long nextPalpebre = 0;

const byte pinOcchioDXX = 21;
const byte pinOcchioDXY = 22;

const byte channelPalpebraSinistra = 5;
const byte channelPalpebraDestra = 23;
byte contatoreOcchi = 0;
const byte limiteOcchi = 4;
const int minOcchiValue = 920;
const int maxOcchiValue = 80;

enum  statiOcchi {APERTI, INCHIUSURA, MANUAL, CHIUSMANUAL};

statiOcchi sitOcchi = APERTI;



int aliveCounter = 0;
const byte aliveTrigger = 10;
/* On boards with a hardware serial port available for use, use
  that port to communicate with the Maestro. For other boards,
  create a SoftwareSerial object using pin 10 to receive (RX) and
  pin 11 to transmit (TX). */
#ifdef SERIAL_PORT_HARDWARE_OPEN
#define maestroSerial SERIAL_PORT_HARDWARE_OPEN
#else
#include <SoftwareSerial.h>
SoftwareSerial maestroSerial(10, 11);
#endif

/* Next, create a Maestro object using the serial port.
  Uncomment one of MicroMaestro or MiniMaestro below depending
  on which one you have. */
//MicroMaestro maestro(maestroSerial);
MiniMaestro maestro(maestroSerial);

void setup() {

  /* setTarget takes the channel number you want to control, and
    the target position in units of 1/4 microseconds. A typical
    RC hobby servo responds to pulses between 1 ms (4000) and 2
    ms (8000). */


  Serial.begin(9600);
  maestroSerial.begin(9600);
  Serial.setTimeout(20);
  nextPalpebre = random(2000, 10000) + millis();
}


int sensorValue01Old = 0;
int sensorValue0Old = 0;


void loop() {
  String asd = Serial.readStringUntil('\n');
  if (asd.length() > 0)
  {
    if (asd.charAt(0) == 'A')
    {
      String pinString = getValueStringSplitter(asd, ';', 1);
      int pin = pinString.toInt();
      String value = getValueStringSplitter(asd, ';', 2);


      
      if (pin == pinOcchioDXX)
      {
        maestro.setTarget(pin, analogConversionMotorOcchioX(value.toInt()));
      }
      else if (pin == pinOcchioDXY)
        maestro.setTarget(pin, analogConversionMotorOcchioY(value.toInt()));
      else if (pin == channelPalpebraDestra)
        maestro.setTarget(pin, analogConversionMotorPalpebra(value.toInt()));
      else
        maestro.setTarget(pin, analogConversionMotor180(value.toInt()));

    }
    if (asd.charAt(0) == 'E')
    {
      eventoPalpebre();
    }
    if (asd.charAt(0) == 'S')
    {
      if (asd.charAt(2) == 'P')
      {
        if (asd.charAt(4) == '0')
        {
          sitOcchi = MANUAL;
        }

        else
        {
          sitOcchi = APERTI;
        }
      }
    }


  }

  gestisciOcchi();
  deadManButton();
  delay(25);
}


void eventoPalpebre()
{

  if (sitOcchi == MANUAL)
  {
    sitOcchi = CHIUSMANUAL;
    contatoreOcchi = 0;
    maestro.setTarget(channelPalpebraSinistra, analogConversionMotorPalpebra(minOcchiValue));
    maestro.setTarget(channelPalpebraDestra, analogConversionMotorPalpebra(minOcchiValue));
  }
  else
  {
    nextPalpebre = 0;
  }
}

void gestisciOcchi()
{
  if (sitOcchi == MANUAL)
  {

    return;
  }

  if (sitOcchi == APERTI)
  {
    if (millis() > nextPalpebre)
    {
      nextPalpebre = random(2000, 10000) + millis();
      sitOcchi = INCHIUSURA;
      contatoreOcchi = 0;
      maestro.setTarget(channelPalpebraSinistra, analogConversionMotorPalpebra(minOcchiValue));
      maestro.setTarget(channelPalpebraDestra, analogConversionMotorPalpebra(minOcchiValue));
    }
    return;
  }
  if (sitOcchi == INCHIUSURA)
  {
    contatoreOcchi++;
    if (contatoreOcchi == limiteOcchi)
    {
      maestro.setTarget(channelPalpebraSinistra, analogConversionMotorPalpebra(maxOcchiValue));
      maestro.setTarget(channelPalpebraDestra, analogConversionMotorPalpebra(maxOcchiValue));
      sitOcchi = APERTI;
    }
    return;
  }

  if (sitOcchi == CHIUSMANUAL)
  {
    contatoreOcchi++;
    if (contatoreOcchi == limiteOcchi)
    {
      maestro.setTarget(channelPalpebraSinistra, analogConversionMotorPalpebra(maxOcchiValue));
      maestro.setTarget(channelPalpebraDestra, analogConversionMotorPalpebra(maxOcchiValue));
      sitOcchi = MANUAL;
    }
    return;
  }
}

int analogConversionMotor180(int analogValue)
{
  return map(analogValue, 0, 1023, 1700, 9200);
}

int analogConversionMotorPalpebra(int analogValue)
{
  return map(analogValue, 0, 1023, 2500, 7600);
}

int analogConversionMotorOcchioX(int analogValue)
{
  return map(analogValue, 0, 1023, 3705, 8444);
}

int analogConversionMotorOcchioY(int analogValue)
{
  return map(analogValue, 0, 1023, 5500, 8550);
}


String getValueStringSplitter(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}


void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}

