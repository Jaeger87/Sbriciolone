void setup() {
  Serial.begin(9600);
  Serial2.begin(115200);
  Serial2.setTimeout(20);
  Serial.println("Hello Computer");
}

void loop()
{
  String message = Serial2.readStringUntil('\n');
  if (message.length() > 0)
  {
    Serial.println(message);
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

    Serial.println(myCheckSum);
  }

  delay(20);
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

