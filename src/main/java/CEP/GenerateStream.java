/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EPRuntime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author gs023850
 */
public class GenerateStream implements Runnable{
        private EPRuntime cepRT;
        public GenerateStream(EPRuntime cepRT){
            this.cepRT = cepRT;
        }

        public void run() {
            MakeStream();
        }
        
       public void MakeStream() {
            File file = new File("C:\\Users\\Weary\\Documents\\w4ndata\\w4ndata.arff");
            String pc =  System.getProperty("user.dir").toString();   
            if (pc.contains("gs023850"))
            {
                file = new File("C:\\Users\\gs023850\\Documents\\w4ndata\\w4ndata.arff");
            }
            try {
                ArffLoader loader = new ArffLoader(); 
                loader.setFile(file);
                Instances structure = loader.getStructure();
                int j = structure.numAttributes();

                HeaderManager.SetStructure(new Instances(structure));
                Instance current;
                long previousTimeStamp = 0;
                String timeStamp = "0";
                long wait = 0;

               while ((current = loader.getNextInstance(structure)) != null){
                   timeStamp = current.stringValue(0);
                   cepRT.sendEvent(current);
                   System.out.println("Sending event");
                   previousTimeStamp = WaitTime(timeStamp, previousTimeStamp, wait);
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
                    System.out.println(e.toString());   
                }
            }
        }
        ///Extract the time to hang the thread from the previous and next timestamp
        long WaitTime(String timeStamp, long previousTimeStamp, long wait) throws InterruptedException{
               timeStamp = timeStamp.substring(14, 16);
               long time = Long.parseLong(timeStamp);
                wait = (time - previousTimeStamp)*100;
                              if (wait < 0 || previousTimeStamp == 0) {
                                  wait = 1; 
                              }
                previousTimeStamp = time;
                System.out.println("Waiting: " + wait);
                Thread.sleep(wait);
                return previousTimeStamp;
        }
        
        void CleanHeader(ArrayList<String> header)
        {
            
        }
        
        
      
}
