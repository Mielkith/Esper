/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EPRuntime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import weka.core.Instances;

/**
 *
 * @author gs023850
 */
public class GenerateStream implements Runnable{
        public EPRuntime cepRT;
         public String[] currentLine;
         public String getStringAttribute(int i) {return currentLine[i];}
         public Double getDoubleAttribute(int i) {return Double.parseDouble(currentLine[i]);}
         private int[] colNumbers;
        public GenerateStream(EPRuntime cepRT, int[] colNumbers){
            this.cepRT = cepRT;
            this.colNumbers = colNumbers; 
        }
        
        public void run() {
            MakeStream();
        }
        
       public void MakeStream() {
            File file = new File("C:\\Users\\Weary\\Documents\\w4ndata\\w4ndata.arff");
            ArrayList<String> header = new ArrayList<String>();
            String pc =  System.getProperty("user.dir").toString();   
            if (pc.contains("gs023850"))
            {
                file = new File("C:\\Users\\gs023850\\Documents\\w4ndata\\w4ndata.arff");
            }
            try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Instances data = new Instances(reader);
            reader.close();
            data.setClassIndex((data.numAttributes() -1));
            
            long wait = 0;
            long previousTimeStamp = 0;
            long timeStamp = 0;
            
                FileInputStream inputStream = new FileInputStream(file);
                Scanner sc = new Scanner(inputStream);

                    while(sc.hasNext()) {
                        String line  = sc.nextLine();
                           if (line.startsWith("@")){
                               header.add(line);
                           }
                           else {
                              
                              currentLine = sc.nextLine().split(","); 
                              String stamp = currentLine[0].substring(14, 16);
                              timeStamp = Long.parseLong(currentLine[0].substring(14, 16));
                              //attribute 11 - event name, 15 - element class name, 29 - severity
                              //20 - active, 22 - duration, 25 - is problem, 28 - catgeory
                              
                              int i = 0;
                              Tick tick = new Tick();
                              tick.PopulateTick(getStringAttribute(colNumbers[i++]), 
                                                getStringAttribute(colNumbers[i++]), 
                                                getStringAttribute(colNumbers[i++]),                                                 
                                                getStringAttribute(colNumbers[i++]), 
                                                getStringAttribute(colNumbers[i++]),
                                                getStringAttribute(colNumbers[i++]),
                                                getStringAttribute(colNumbers[i++]), 
                                                getStringAttribute(colNumbers[i++]));
                              previousTimeStamp = WaitTime(timeStamp, previousTimeStamp, wait);
                              cepRT.sendEvent(tick);

                              System.out.println("Sending event");
                          } 
                    }       
            }
            catch (Exception e) {
                
                if (e.equals(new FileNotFoundException())) {
                    System.out.println("File not found - could not generate stream");
                    return;
                }
                else if (e.equals(new IOException())){
                    System.out.println("Unable to read file");
                }
                else if (e.equals(new NumberFormatException())){
                     System.out.println("Unable to convert to time to number - bad time");
                }
                else {
                    return;
                }
            
            }
        }
        
        long WaitTime(long timeStamp, long previousTimeStamp, long wait) throws InterruptedException{
            
                wait = (timeStamp - previousTimeStamp)*1000;
                              if (wait < 0 || previousTimeStamp == 0) {
                                  wait = 1; 
                              }
                previousTimeStamp = timeStamp;
                System.out.println("Waiting: " + wait);
                Thread.sleep(wait);
                return previousTimeStamp;
        }
        
        void CleanHeader(ArrayList<String> header)
        {
            
        }
      
}
