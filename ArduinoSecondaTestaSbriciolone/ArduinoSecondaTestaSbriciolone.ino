struct ServoValues {
  int minValue;
  int maxValue;
  int channel;
  String servoName;
  bool mirror;
  int lastPosition = 512;
  int counterShutDown;
  bool stopAndGo;
  int shutDownWhen;
};

const char eventsC = 'e';
const char statusChangeC = 'C';
const char servoC = 'S';

const char eyesC = 'E';
const char eyeLidsC = 'L';
const char eyebrownsC = 'B';
const char mouthC = 'M';
const char noseC = 'N';


const byte howmanyservo = 19;//Sono 8 invero
ServoValues servoList[howmanyservo];
#include <PololuMaestro.h>
unsigned long nextPalpebre = 0;
byte contatoreOcchi = 0;
const byte limiteOcchi = 4;
const int minOcchiValue = 995;
const int maxOcchiValue = 190;

const int rangeBoccaMediani = 350;


enum  statiOcchi {APERTI, INCHIUSURA, MANUAL, CHIUSMANUAL};

statiOcchi sitOcchi = MANUAL;

const int eyeLidsSpeed = 72;

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


  Serial.begin(115200);
  maestroSerial.begin(115200);
  Serial.setTimeout(20);

  servoList[0].minValue = 3900;
  servoList[0].maxValue = 8000;
  servoList[0].channel = 5;
  servoList[0].servoName = "BoccaS";
  servoList[0].mirror = false;
  servoList[0].stopAndGo = false;
  servoList[0].shutDownWhen = 4;

  servoList[1].minValue = 4300;
  servoList[1].maxValue = 9400;
  servoList[1].channel = 6;
  servoList[1].servoName = "BoccaCS";
  servoList[1].mirror = false;
  servoList[1].stopAndGo = false;
  servoList[1].shutDownWhen = 4;

  servoList[2].minValue = 3500;
  servoList[2].maxValue = 8000;
  servoList[2].channel = 7;
  servoList[2].servoName = "BoccaC";
  servoList[2].mirror = false;
  servoList[2].stopAndGo = false;
  servoList[2].shutDownWhen = 4;

  servoList[3].minValue = 4000;
  servoList[3].maxValue = 8000;
  servoList[3].channel = 8;
  servoList[3].servoName = "BoccaCD";//
  servoList[3].mirror = true;
  servoList[3].stopAndGo = false;
  servoList[3].shutDownWhen = 4;

  servoList[4].minValue = 4400;
  servoList[4].maxValue = 8100;//
  servoList[4].channel = 9;
  servoList[4].servoName = "BoccaD";//
  servoList[4].mirror = true;
  servoList[4].stopAndGo = false;
  servoList[4].shutDownWhen = 4;

  servoList[5].minValue = 5300;
  servoList[5].maxValue = 7400;
  servoList[5].channel = 12;
  servoList[5].servoName = "NasoS";
  servoList[5].mirror = false;
  servoList[5].stopAndGo = false;
  servoList[5].shutDownWhen = 4;

  servoList[6].minValue = 3800;
  servoList[6].maxValue = 6600;//
  servoList[6].channel = 11;
  servoList[6].servoName = "GuanciaS";
  servoList[6].mirror = true;
  servoList[6].stopAndGo = false;
  servoList[6].shutDownWhen = 4;

  servoList[7].minValue = 5000;
  servoList[7].maxValue = 7000;
  servoList[7].channel = 10;
  servoList[7].servoName = "NasoD";
  servoList[7].mirror = true;
  servoList[7].stopAndGo = false;
  servoList[7].shutDownWhen = 4;

  servoList[8].minValue = 4800;
  servoList[8].maxValue = 6800;
  servoList[8].channel = 13;
  servoList[8].servoName = "GuanciaD";
  servoList[8].mirror = true;
  servoList[8].stopAndGo = false;
  servoList[8].shutDownWhen = 4;

  servoList[9].minValue = 4000;
  servoList[9].maxValue = 8400;
  servoList[9].channel = 14;
  servoList[9].servoName = "SopraciglioCS";
  servoList[9].mirror = true;
  servoList[9].stopAndGo = true;
  servoList[9].shutDownWhen = 3;

  servoList[10].minValue = 3900;
  servoList[10].maxValue = 8400;
  servoList[10].channel = 15;
  servoList[10].servoName = "SopraciglioS";
  servoList[10].mirror = true;
  servoList[10].stopAndGo = true;
  servoList[10].shutDownWhen = 3;

  servoList[11].minValue = 3700;
  servoList[11].maxValue = 8300;
  servoList[11].channel = 16;
  servoList[11].servoName = "SopraciglioCD";
  servoList[11].mirror = true;
  servoList[11].stopAndGo = true;
  servoList[11].shutDownWhen = 3;

  servoList[12].minValue = 3900;
  servoList[12].maxValue = 8300;
  servoList[12].channel = 17;
  servoList[12].servoName = "SopraciglioD";
  servoList[12].mirror = true;
  servoList[12].stopAndGo = true;
  servoList[12].shutDownWhen = 3;

  servoList[13].minValue = 3900;
  servoList[13].maxValue = 8800;
  servoList[13].channel = 18;
  servoList[13].servoName = "OcchioSX";
  servoList[13].mirror = true;
  servoList[13].stopAndGo = true;
  servoList[13].shutDownWhen = 3;

  servoList[14].minValue = 2700;
  servoList[14].maxValue = 9600;
  servoList[14].channel = 4;
  servoList[14].servoName = "OcchioSY";
  servoList[14].mirror = true;
  servoList[14].stopAndGo = true;
  servoList[14].shutDownWhen = 3;

  servoList[15].minValue = 3200;
  servoList[15].maxValue = 9600;
  servoList[15].channel = 20;
  servoList[15].servoName = "PalpebraS";
  servoList[15].mirror = true;
  servoList[15].stopAndGo = true;
  servoList[15].shutDownWhen = 6;

  servoList[16].minValue = 3700;
  servoList[16].maxValue = 8000;
  servoList[16].channel = 21;
  servoList[16].servoName = "OcchioDX";
  servoList[16].mirror = true;
  servoList[16].stopAndGo = true;
  servoList[16].shutDownWhen = 3;

  servoList[17].minValue = 2000;
  servoList[17].maxValue = 9200;
  servoList[17].channel = 22;
  servoList[17].servoName = "OcchioDY";
  servoList[17].mirror = false;
  servoList[17].stopAndGo = true;
  servoList[17].shutDownWhen = 3;

  servoList[18].minValue = 4800;
  servoList[18].maxValue = 9200;
  servoList[18].channel = 23;
  servoList[18].servoName = "PalpebraD";
  servoList[18].mirror = false;
  servoList[18].stopAndGo = true;
  servoList[18].shutDownWhen = 6;


  nextPalpebre = random(2000, 10000) + millis();

  for (int i = 0; i < howmanyservo; i++)
    maestro.setTarget(servoList[i].channel, analogServoConversion(servoList[i].lastPosition, servoList[i]));
}

void loop() {
  String message = Serial.readStringUntil('\n');
  if (message.length() > 0)
  {
    int lenghtMessage = getLenghtBeforeCheckSum(message, ';');
    int numberSeparators = homManySeparator(message, ';');
    int checksum = getValueStringSplitter(message, ';', numberSeparators).toInt();

    char bufferChar[lenghtMessage];
    message.toCharArray(bufferChar, lenghtMessage);

    int sum = 0;
    for (int i = 0; i <  lenghtMessage; i++)
    {
      sum += bufferChar[i];
    }


    int myCheckSum = sum % 100;

    if (myCheckSum == checksum)
    {
      if (message.charAt(0) == eyesC)
      {
        eyesMotorMessage(message);
      }
      else if (message.charAt(0) == eyeLidsC)
      {
        eyelidsMessage(message);
      }

      else if (message.charAt(0) == eyebrownsC)
      {
        eyeBrowMessage(message);
      }

      else if (message.charAt(0) == noseC)
      {
        noseMessage(message);
      }

      else if (message.charAt(0) == mouthC)
      {
        mouthMessage(message);
      }
    }
  }
  gestisciOcchi();
  deadManButton();
  shutDownMotors();
  delay(20);
}

void noseMessage(String message)
{
  if (message.charAt(1) == servoC)
  {

    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    servoList[index].lastPosition = value;
    servoList[index].counterShutDown = 0;
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}

void eyelidsMessage(String message)
{
  if (message.charAt(1) == servoC)
    palpebraMotorMessage(message);

  else if (message.charAt(1) == eventsC)
    eventoPalpebre();

  else if (message.charAt(1) == statusChangeC)
    if (message.charAt(3) == '0')
      sitOcchi = MANUAL;
    else if (message.charAt(3) == '1')
      sitOcchi = APERTI;
}

void eyeBrowMessage(String message)
{
  if (message.charAt(1) == servoC)
  {
    maestro.setTarget(servoList[9].channel, analogServoConversion(servoList[9].lastPosition, servoList[9]));
    maestro.setTarget(servoList[10].channel, analogServoConversion(servoList[10].lastPosition, servoList[10]));
    maestro.setTarget(servoList[11].channel, analogServoConversion(servoList[11].lastPosition, servoList[11]));
    maestro.setTarget(servoList[12].channel, analogServoConversion(servoList[12].lastPosition, servoList[12]));
    servoList[9].counterShutDown = 0;
    servoList[10].counterShutDown = 0;
    servoList[11].counterShutDown = 0;
    servoList[12].counterShutDown = 0;
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    servoList[index].lastPosition = value;
    servoList[index].counterShutDown = 0;
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}


void mouthMessage(String message)
{
  if (message.charAt(1) == servoC)
  {
    if (servoList[2].stopAndGo)
    {
      maestro.setTarget(servoList[0].channel, analogServoConversion(servoList[0].lastPosition, servoList[0]));
      maestro.setTarget(servoList[1].channel, analogServoConversion(servoList[1].lastPosition, servoList[1]));
      maestro.setTarget(servoList[2].channel, analogServoConversion(servoList[2].lastPosition, servoList[2]));
      maestro.setTarget(servoList[3].channel, analogServoConversion(servoList[3].lastPosition, servoList[3]));
      maestro.setTarget(servoList[4].channel, analogServoConversion(servoList[4].lastPosition, servoList[4]));
      servoList[0].counterShutDown = 0;
      servoList[1].counterShutDown = 0;
      servoList[2].counterShutDown = 0;
      servoList[3].counterShutDown = 0;
      servoList[4].counterShutDown = 0;
    }
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();

    if (index == 1 || index == 3)
      if (abs(servoList[2].lastPosition - value) > rangeBoccaMediani)
        return;

    if (index == 2)
    {
      if (abs(servoList[1].lastPosition - value) > rangeBoccaMediani)
      {
        int diff = servoList[1].lastPosition - value;
        if (diff > 0)
        {
          int correction = diff - rangeBoccaMediani + 1;
          servoList[1].lastPosition = servoList[1].lastPosition - correction;
          maestro.setTarget(servoList[1].channel, analogServoConversion(servoList[1].lastPosition, servoList[1]));
        }
        else
        {
          int correction = diff + rangeBoccaMediani + 1;
          servoList[1].lastPosition = servoList[1].lastPosition + correction;
          maestro.setTarget(servoList[1].channel, analogServoConversion(servoList[1].lastPosition, servoList[1]));
        }
      }

      if (abs(servoList[3].lastPosition - value) > rangeBoccaMediani)
      {

        int diff = servoList[3].lastPosition - value;
        if (diff > 0)
        {
          int correction = diff - rangeBoccaMediani + 1;
          servoList[3].lastPosition = servoList[3].lastPosition - correction;
          maestro.setTarget(servoList[3].channel, analogServoConversion(servoList[3].lastPosition, servoList[3]));
        }
        else
        {
          int correction = diff + rangeBoccaMediani + 1;
          servoList[3].lastPosition = servoList[3].lastPosition + correction;
          maestro.setTarget(servoList[3].channel, analogServoConversion(servoList[3].lastPosition, servoList[3]));
        }

      }
    }
    servoList[index].lastPosition = value;

    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}

void eyesMotorMessage(String message)
{
  if (message.charAt(1) == servoC)
  {
    String indexString = getValueStringSplitter(message, ';', 1);
    int index = indexString.toInt();
    String valueString = getValueStringSplitter(message, ';', 2);
    int value = valueString.toInt();
    servoList[index].lastPosition = value;
    servoList[index].counterShutDown = 0;
    maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
  }
}


void palpebraMotorMessage(String message)
{

  if (sitOcchi != MANUAL)
    return;
  String indexString = getValueStringSplitter(message, ';', 1);
  int index = indexString.toInt();
  String valueString = getValueStringSplitter(message, ';', 2);
  int value = valueString.toInt();
  servoList[index].lastPosition = value;
  servoList[index].counterShutDown = 0;
  maestro.setTarget(servoList[index].channel, analogServoConversion(value, servoList[index]));
}

void eventoPalpebre()
{

  if (sitOcchi == MANUAL)
  {
    sitOcchi = CHIUSMANUAL;
    contatoreOcchi = 0;
    servoList[15].counterShutDown = 0;
    servoList[18].counterShutDown = 0;
    maestro.setSpeed(servoList[15].channel, 0);
    maestro.setSpeed(servoList[18].channel, 0);
    maestro.setAcceleration(servoList[15].channel, 0);
    maestro.setAcceleration(servoList[18].channel, 0);
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
    maestro.setSpeed(servoList[15].channel, eyeLidsSpeed);
    maestro.setSpeed(servoList[18].channel, eyeLidsSpeed);
    return;
  }

  if (sitOcchi == APERTI)
  {
    maestro.setSpeed(servoList[15].channel, eyeLidsSpeed);
    maestro.setSpeed(servoList[18].channel, eyeLidsSpeed);
    if (millis() > nextPalpebre)
    {
      nextPalpebre = random(2000, 10000) + millis();
      sitOcchi = INCHIUSURA;
      contatoreOcchi = 0;
      maestro.setSpeed(servoList[15].channel, 0);
      maestro.setSpeed(servoList[18].channel, 0);
      maestro.setAcceleration(servoList[15].channel, 0);
      maestro.setAcceleration(servoList[18].channel, 0);
      servoList[15].counterShutDown = 0;
      servoList[18].counterShutDown = 0;
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
      servoList[15].counterShutDown = 0;
      servoList[18].counterShutDown = 0;
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
      servoList[15].counterShutDown = 0;
      servoList[18].counterShutDown = 0;
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

int analogServoConversion(int analogValue, ServoValues & servo)
{
  if (servo.mirror)
    return map(analogValue, 1023, 0, servo.minValue, servo.maxValue);
  return map(analogValue, 0, 1023, servo.minValue, servo.maxValue);
}

int homManySeparator(String data, char separator)
{
  int s = 0;
  for (int i = 0; i < data.length(); i++)
    if (data.charAt(i) == separator)
      s++;
  return s;
}


int getLenghtBeforeCheckSum(String data, char separator)
{
  int l = -1;

  for (int i = 0; i < data.length(); i++)
    if (data.charAt(i) == separator)
      l = i;
  return (l + 1);
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

void shutDownMotors()
{
  for (int i = 0; i < howmanyservo; i++)
  {
    if (!servoList[i].stopAndGo)
      continue;
    if (servoList[i].counterShutDown > servoList[i].shutDownWhen)
      maestro.setTarget(servoList[i].channel, 0);
    else
      servoList[i].counterShutDown = servoList[i].counterShutDown + 1;
  }
}


void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
