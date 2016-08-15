
int analogPin = 3;  
float val = 0;           // variable to store the value read

void setup() {
  // put your setup code here, to run once:
Serial.begin(9600);
}
 String str="12.1";
void loop() {
  val = analogRead(analogPin); 
  val=val*0.0049;
  str=String(val);
  
  Serial.print(" "+str+" ");
  //could serial.println fix my problems
  
}
