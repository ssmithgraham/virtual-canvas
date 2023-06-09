/* autogenerated by Processing revision 1286 on 2023-04-29 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import processing.serial.*;
import java.util.ArrayDeque;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class driver extends PApplet {
 
float mx;
float my;
float easing = 0.005f;
int radius = 24;
int edge = 100;
int inner = edge + radius;

int lf = 10;    // Linefeed in ASCII
String myString = null;
Serial myPort;  // The serial port
int pot1 = 0;
int pot2 = 0;
int pot3 = 0;
float a = 0.0f;
float rSize;  // rectangle size
float a1;
float a2;

 
int num = 800;
float mx2[] = new float[num];
float my2[] = new float[num];

 public void setup() {
  // List all the available serial ports
  printArray(Serial.list());
  // Open the port you are using at the rate you want:
  myPort = new Serial(this, Serial.list()[0], 115200);
  myPort.clear();
  // Throw out the first reading, in case we started reading 
  // in the middle of a string from the sender.
  myString = myPort.readStringUntil(lf);
  myString = null;
  
  /* size commented out by preprocessor */;
  rSize = width / 6;  
  noStroke();
  fill(204, 204);

}

 public void draw() {
  background(126);
  while (myPort.available() > 0) { //processes input from accelerometer
    myString = myPort.readStringUntil(lf);
    if (myString != null) {
      myString.trim();
      String newStr = trim(myString);
      int[] data = PApplet.parseInt(split(newStr, ','));
      println(data);
      if(data.length >= 3){ //saves input from accelerometer
        pot1 = data[1];
        pot2 = data[2];
        pot3 = data[0];
      }
    }
  }
  
  //uses input from accelerometer to draw canvas and paint drop
  a1 = (pot1/11.1f);
  a2 = (pot2/11.1f);
  draw_rect(a1,a2);
  draw_circ(a1,a2,pot3);
}

 public void draw_rect(float a1,float a2){
  noStroke();
  pushMatrix(); //handles layers
  translate(width/2, height/2); //places canvas
  
  rotateX(radians(a1));
  rotateY(-radians(a2));
  fill(255);
  rect(-rSize, -rSize, rSize*2.5f, rSize*2.5f);

}

 public void draw_circ(float a1, float a2, int col){
    noStroke();
    //changes colors based on potentiometer input
    if(col > 0 && col < 96){//red
      fill(255,(col/3),0); 
  } else if (col > 112 && col < 224){
      fill((col/3),255,0);
  } else if (col > 224 && col < 336){
      fill(0,255,(col/3));
  } else if (col > 336 && col < 448){
      fill(0,(col/3),255);
  } else if (col > 448 && col < 560){
      fill((col/3),0,255);
  } else {
      fill(255,0,(col/3));
  }
  
  popMatrix();
  translate(width/2,height/2,5);
  //rotates the canvas depending on accelerometer
  rotateX(radians(a1));
  rotateY(-radians(a2));

//handles movement of paint drop
  if (abs(pot2 - mx) > 0.1f) {
    mx = mx + (-pot2 - mx) * easing;
  }
  if (abs(pot1 - my) > 0.1f) {
    my = my + (-pot1- my) * easing;
  }
  
  int which = frameCount % num;
  mx2[which] = mx; //animating the paint drop
  my2[which] = my;
  
  mx = constrain(mx, -rSize, rSize * 1.5f);
  my = constrain(my, -rSize, rSize * 1.5f);
  
  //saves each drop into an array to create strokes
  for (int i = 0; i < num; i++) {
  // which+1 is the smallest (the oldest in the array)
  int index = (which+1 + i) % num;
  ellipse(mx2[index], my2[index], i/50, i/50);
  }

  
}


  public void settings() { size(1080, 720, P3D); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "driver" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
