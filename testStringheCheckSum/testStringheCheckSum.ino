void setup() {

  Serial.begin(9600);
  String kk = "";

  int z = 6;
  kk += z;

  kk += 'h';

  kk += "porco dio";

  Serial.println(kk);

  char bufferChar[kk.length()];
  kk.toCharArray(bufferChar, kk.length());
  int sum = 0;
  for (int i = 0; i <  kk.length(); i++)
  {
    sum += bufferChar[i];
  }


Serial.println(sum);




}

void loop() {
  // put your main code here, to run repeatedly:

}
