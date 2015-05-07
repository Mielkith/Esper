/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.DecoratingEventBean;
import com.espertech.esper.event.NaturalEventBean;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.associations.Apriori;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author gs023850
 */
public class CEPListener implements UpdateListener{
    private ArrayList<Attribute> dataSet = new ArrayList<Attribute>();
    private ArrayList<Attribute> eventValues = new ArrayList<Attribute>();
    int columnNumbers[];
    
    
    public void SetLabels(HeaderManager labels){
       // this.labels = labels;
    }   
    
    public void SetColNumber(int colNumbers[])
    {
        columnNumbers = colNumbers;
    }
    
 public void update(EventBean[] newData, EventBean[] oldData)  {
         
     System.out.println("Event received: " + newData[0].getUnderlying());
     
     
        if (newData.length > 2)
        {
            try{
                Instances data = HeaderManager.GetStructure();
                Apriori aprioriObj = new weka.associations.Apriori();
                for(EventBean bean:newData)
                {
                    EventType event = bean.getEventType();
                    Object inst = bean.getUnderlying();
                    data.add((Instance)inst);
                }
                try{
                    aprioriObj.buildAssociations(data);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                
                ArrayList<Object>[] rules = aprioriObj.getAllTheRules();
                
                
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(CEPListener.class.getName()).log(Level.SEVERE, null,ex);
            }


        }

         
    }
}
