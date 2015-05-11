/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author gs023850
 */
public class CEP {
    /** 
     * @param args the command line arguments
     */
    
    /**set this to determine the size of the window and the amount to decrease events 
    *with duration by every update
    */
        private static int windowSize = 2;
        private static GenerateStream streamGen;
  
        public static void main(String[] args) throws Exception{
  
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel((Level) Level.WARN);
        
      //  HeaderManager labels = new HeaderManager();
      
       
        //The Configuration is meant only as an initialization-time object.
        Configuration cepConfig = new Configuration();
        // We register Ticks as objects the engine will have to handle
        cepConfig.addEventType("Instance",Instance.class.getName());

        // We setup the engine
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
        
        EPRuntime cepRT = cep.getEPRuntime();

        // We register an EPL statement
        EPAdministrator cepAdm = cep.getEPAdministrator();
        
        String  selectStatement =  
        /*   "select attribute1 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute2 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute3 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute4 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute5 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute6 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute7 as "  + labels.GetHeader(colNumbers[i++]) + ", "
                + "attribute8 as " + labels.GetHeader(colNumbers[i++])
                + " from Tick.win:time_batch(20 sec)";*/
                
              //"select * from Instance.win:time_batch(10)";
           "select * from Instance.win:length(2)";

        
        EPStatement cepStatement = cepAdm.createEPL(
               selectStatement); 
       
         //set the labels for the nominal attributes
       //  listener.SetLabels(labels);
         //listener.SetColNumber(colNumbers);
         streamGen = new GenerateStream(cepRT);
         Thread t = new Thread(streamGen);

         
         CEPListener listener = new CEPListener(windowSize);
         cepStatement.addListener((UpdateListener) listener);
                                   t.run();
   
           
        }
        
        
}
