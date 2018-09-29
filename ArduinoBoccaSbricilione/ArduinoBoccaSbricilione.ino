const byte pinButton = 12;
int buttonState12 = 0;
int oldButtonState12 = 0;

int aliveCounter = 0;
const byte aliveTrigger = 10;


struct Motor {
  int port;
  char sector;
  int pinH;
  char event = 'M';
  int oldValue = 0;
};

const byte howmanyanalog = 3;
Motor listaMotori[howmanyanalog];


void setup()
{
  Serial.begin(9600);
  pinMode(pinButton, INPUT);
}

void loop() {
  buttonState12 = digitalRead(pinButton);

  if (buttonState12 != oldButtonState12)
    if (buttonState12 == LOW)
    {
      Serial.println("B;17");
    }

  oldButtonState12 = buttonState12;

  deadManButton();

  delay(50);

}



void deadManButton()
{
  if (aliveCounter % aliveTrigger == 0)
    Serial.println("ALIVE");

  aliveCounter++;
}
