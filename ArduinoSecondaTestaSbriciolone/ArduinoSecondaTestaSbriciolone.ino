struct ServoValues {
  int minValue;
  int maxValue;
  int channel;
  String servoName;
  bool mirror;
};

const byte howmanyservo = 19;//Sono 8 invero
ServoValues servoList[howmanyservo];
#include <PololuMaestro.h>
unsigned long nextPalpebre = 0;
byte contatoreOcchi = 0;
const byte limiteOcchi = 4;
const int minOcchiValue = 920;
const int maxOcchiValue = 80;

const byte shutDownEyesBrownEvery = 5;
int shutDownEyesBrownCounter = 0;

enum  statiOcchi {APERTI, INCHIUSURA, MANUAL, CHIUSMANUAL};

statiOcchi sitOcchi = MANUAL;



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

  servoList[0].minValue = 2400;
  servoList[0].maxValue = 7300;
  servoList[0].channel = 5;
  servoList[0].servoName = "BoccaS";
  servoList[0].mirror = false;

  servoList[1].minValue = 4400;
  servoList[1].maxValue = 9300;
  servoList[1].channel = 6;
  servoList[1].servoName = "BoccaCS";
  servoList[1].mirror = true;

  servoList[2].minValue = 3500;
  servoList[2].maxValue = 8000;
  servoList[2].channel = 7;
  servoList[2].servoName = "BoccaC";
  servoList[2].mirror = false;

  servoList[3].minValue = 3000;
  servoList[3].maxValue = 5900;
  servoList[3].channel = 8;
  servoList[3].servoName = "BoccaCD";//
  servoList[3].mirror = true;

  servoList[4].minValue = 4500;
  servoList[4].maxValue = 8200;//
  servoList[4].channel = 9;
  servoList[4].servoName = "BoccaD";//
  servoList[4].mirror = true;

  servoList[5].minValue = 5300;
  servoList[5].maxValue = 7500;
  servoList[5].channel = 10;
  servoList[5].servoName = "NasoS";
  servoList[5].mirror = false;

  servoList[6].minValue = 3500;
  servoList[6].maxValue = 5400;//
  servoList[6].channel = 11;
  servoList[6].servoName = "GuanciaS";
  servoList[6].mirror = true;

  servoList[7].minValue = 6000;
  servoList[7].maxValue = 6600;
  servoList[7].channel = 12;
  servoList[7].servoName = "NasoD";
  servoList[7].mirror = true;

  servoList[8].minValue = 5700;
  servoList[8].maxValue = 7700;
  servoList[8].channel = 13;
  servoList[8].servoName = "GuanciaD";
  servoList[8].mirror = true;

  servoList[9].minValue = 5000;
  servoList[9].maxValue = 8000;
  servoList[9].channel = 14;
  servoList[9].servoName = "SopraciglioCD";
  servoList[9].mirror = true;

  servoList[10].minValue = 4500;
  servoList[10].maxValue = 7800;
  servoList[10].channel = 15;
  servoList[10].servoName = "SopraciglioDX";
  servoList[10].mirror = true;

  servoList[11].minValue = 3700;
  servoList[11].maxValue = 5400;
  servoList[11].channel = 16;
  servoList[11].servoName = "SopraciglioCS";
  servoList[11].mirror = true;

  servoList[12].minValue = 4000;
  servoList[12].maxValue = 7500;
  servoList[12].channel = 17;
  servoList[12].servoName = "SopraciglioSX";
  servoList[12].mirror = true;

  servoList[13].minValue = 3900;
  servoList[13].maxValue = 8600;
  servoList[13].channel = 18;
  servoList[13].servoName = "OcchioSX";
  servoList[13].mirror = true;

  servoList[14].minValue = 2400;
  servoList[14].maxValue = 5500;
  servoList[14].channel = 4;
  servoList[14].servoName = "OcchioSY";
  servoList[14].mirror = true;

  servoList[15].minValue = 2450;
  servoList[15].maxValue = 6800;
  servoList[15].channel = 20;
  servoList[15].servoName = "PalpebraD";
  servoList[15].mirror = true;

  servoList[16].minValue = 3600;
  servoList[16].maxValue = 8800;
  servoList[16].channel = 21;
  servoList[16].servoName = "OcchioDX";
  servoList[16].mirror = true;

  servoList[17].minValue = 5100;
  servoList[17].maxValue = 9000;
  servoList[17].channel = 22;
  servoList[17].servoName = "OcchioDY";
  servoList[17].mirror = false;

  servoList[18].minValue = 5850;
  servoList[18].maxValue = 9200;
  servoList[18].channel = 23;
  servoList[18].servoName = "PalpebraS";
  servoList[18].mirror = false;


  nextPalpebre = random(2000, 10000) + millis();

}


void loop() {
  String message = Serial.readStringUntil('\n');
  if (message.length() > 0)
  {
    if (message.charAt(0) == 'E')
    {
      if (message.charAt(1) == 'M')
        eyesMotorMessage(message);

    }
    else if (message.charAt(0) == 'L')
    {
      eyelidsMessage(message);
    }

    else if (message.charAt(0) == 'B')
    {
      eyeBrowMessage(message);
    }

    else if (message.charAt(0) == 'N')
    {
      noseMessage(message);
    }

    else if (message.charAt(0) == 'M')
    {
      mouthMessage(message);
    }
  }

  gestisciOcchi();
  deadManButton();
  shutdownEyes();
  delay(25);
}

void noseMessage(String message)
{
  if (message.charAt(1) == 'M')
  {
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}

void eyelidsMessage(String message)
{
  if (message.charAt(1) == 'M')
    palpebraMotorMessage(message);

  else if (message.charAt(1) == 'E')
    eventoPalpebre();

  else if (message.charAt(1) == 'S')
    if (message.charAt(3) == '0')
      sitOcchi = MANUAL;
    else if (message.charAt(3) == '1')
      sitOcchi = APERTI;
}

void eyeBrowMessage(String message)
{
  if (message.charAt(1) == 'M')
  {
    shutDownEyesBrownCounter = 0;
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}


void mouthMessage(String message)
{
  if (message.charAt(1) == 'M')
  {
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}

void eyesMotorMessage(String message)
{
  String indexString = getValueStringSplitter(message, ';', 1);
  int index = indexString.toInt();
  String valueString = getValueStringSplitter(message, ';', 2);
  int value = valueString.toInt();
  maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
}


void palpebraMotorMessage(String message)
{

  if (sitOcchi != MANUAL)
    return;
  String indexString = getValueStringSplitter(message, ';', 1);
  int index = indexString.toInt();
  String valueString = getValueStringSplitter(message, ';', 2);
  int value = valueString.toInt();
  maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
}

void eventoPalpebre()
{

  if (sitOcchi == MANUAL)
  {
    sitOcchi = CHIUSMANUAL;
    contatoreOcchi = 0;
    maestro.setTarget(servoList[15].channel, analogServoConversion(minOcchiValue, servoList[15]));
    maestro.setTarget(servoList[18].channel, analogServoConversion(minOcchiValue, servoList[18]));
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
      maestro.setTarget(servoList[15].channel, analogServoConversion(minOcchiValue, servoList[15]));
      maestro.setTarget(servoList[18].channel, analogServoConversion(minOcchiValue, servoList[18]));
    }
    return;
  }
  if (sitOcchi == INCHIUSURA)
  {
    contatoreOcchi++;
    if (contatoreOcchi == limiteOcchi)
    {
      maestro.setTarget(servoList[15].channel, analogServoConversion(maxOcchiValue, servoList[15]));
      maestro.setTarget(servoList[18].channel, analogServoConversion(maxOcchiValue, servoList[18]));
      sitOcchi = APERTI;
    }
    return;
  }

  if (sitOcchi == CHIUSMANUAL)
  {
    contatoreOcchi++;
    if (contatoreOcchi == limiteOcchi)
    {
      maestro.setTarget(servoList[15].channel, analogServoConversion(maxOcchiValue, servoList[15]));
      maestro.setTarget(servoList[18].channel, analogServoConversion(maxOcchiValue, servoList[18]));
      sitOcchi = MANUAL;
    }
    return;
  }
}

/*
  int analogConversionMotor180(int analogValue)
  {
  return map(analogValue, 0, 1023, 2100, 9200);
  }
*/

int analogServoConversion(int analogValue, ServoValues& servo)
{
  if (servo.mirror)
    return map(analogValue, 1023, 0, servo.minValue, servo.maxValue);
  return map(analogValue, 0, 1023, servo.minValue, servo.maxValue);
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

void shutdownEyes()
{
  if(shutDownEyesBrownCounter > shutDownEyesBrownEvery)
    {
      maestro.setTarget(servoList[9].channel,0);//
      maestro.setTarget(servoList[10].channel ,0);//
      maestro.setTarget(servoList[11].channel,0);//
      maestro.setTarget(servoList[12].channel,0);//

      return;
    }
  shutDownEyesBrownCounter++;
}


void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
