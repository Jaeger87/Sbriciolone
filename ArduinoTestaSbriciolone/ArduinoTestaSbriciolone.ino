#include <PololuMaestro.h>

unsigned long nextPalpebre = 0;

const byte channelPalpebraSinistra = 8;
const byte channelPalpebraDestra = 9;
byte contatoreOcchi = 0;
const byte limiteOcchi = 4;


enum  statiOcchi {
  APERTI, INCHIUSURA
};

statiOcchi sitOcchi = APERTI;

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

  /*
    maestro.setSpeed(11, 0);
    maestro.setAcceleration(11,0);
  */
}

void loop() {
  //Serial.println("mannaggia a...teee");
  String asd = Serial.readString();
  if (asd.length() > 0)
  {
    int value = asd.toInt();
    maestro.setTarget(17, value);
  }

  //Serial.println(asd);

  // Set the target of channel 0 to 1500 us, and wait 2 seconds.
  // maestro.setTarget(17, 4000);
  // maestro.setTarget(11, 1000);
  //maestro.setTarget(5, 2000);
  // Serial.println("1");
  //delay(2000);

  // Set the target of channel 0 to 1750 us, and wait 2 seconds.
  //  maestro.setTarget(17, 6000);
  //maestro.setTarget(11, 3000);
  //maestro.setTarget(5, 12000);
  // Serial.println("2");
  // delay(2000);

  // Set the target of channel 0 to 1250 us, and wait 2 seconds.
  //maestro.setTarget(17, 8000);
  //maestro.setTarget(11, 5600);
  //maestro.setTarget(5, 1000);
  //Serial.println("3");
  //delay(2000);
  //Serial.println("bshbshbdhbdh");

  delay(30);
}


void gestisciOcchi()
{
  if (sitOcchi == APERTI)
    if (nextPalpebre > millis())
    {
      nextPalpebre = random(2000, 10000) + millis();
      sitOcchi = INCHIUSURA;
      contatoreOcchi = 0;
      //maestro.setTarget(channelPalpebraSinistra, 8000);
      //maestro.setTarget(channelPalpebraDestra, 8000);
      return;
    }
    else
    {
      contatoreOcchi++;
      if (contatoreOcchi == limiteOcchi)
      {
        //maestro.setTarget(channelPalpebraSinistra, 4000);
        //maestro.setTarget(channelPalpebraDestra, 4000);
        
      }

      
    }


}


