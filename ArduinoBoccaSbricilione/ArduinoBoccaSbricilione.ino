const byte pinButton = 12;
int buttonState12 = 0;
int oldButtonState12 = 0;

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

  delay(50);

}
