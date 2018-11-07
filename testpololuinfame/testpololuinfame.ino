
#include <PololuMaestro.h>

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
MicroMaestro maestro(maestroSerial);
//MiniMaestro maestro(maestroSerial);

void setup()
{
  // Set the serial baud rate.
  maestroSerial.begin(9600);
}

void loop()
{
  /* setTarget takes the channel number you want to control, and
     the target position in units of 1/4 microseconds. A typical
     RC hobby servo responds to pulses between 1 ms (4000) and 2
     ms (8000). */

  // Set the target of channel 0 to 1500 us, and wait 2 seconds.
  maestro.setTarget(15, 6000);
  maestro.setTarget(13, 6000);
  maestro.setTarget(21, 6000);
  maestro.setTarget(9, 6000);
  maestro.setTarget(16, 6000);
  maestro.setTarget(20, 6000);
  delay(2000);

  // Set the target of channel 0 to 1750 us, and wait 2 seconds.
  maestro.setTarget(15, 2000);
  maestro.setTarget(13, 2000);
  maestro.setTarget(21, 2000);
  maestro.setTarget(9, 2000);
  maestro.setTarget(16, 2000);
  maestro.setTarget(20, 2000);
  delay(2000);

  // Set the target of channel 0 to 1250 us, and wait 2 seconds.
  maestro.setTarget(15, 8000);
  maestro.setTarget(13, 8000);
  maestro.setTarget(21, 8000);
  maestro.setTarget(9, 8000);
  maestro.setTarget(16, 8000);
  maestro.setTarget(20, 8000);
  delay(2000);
}
