/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EventBean;
import java.util.ArrayList;
import weka.associations.Apriori;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author gs023850
 */
public class CEPListener {
    private ArrayList<Attribute> dataSet = new FastVector();
    private ArrayList<Attribute> eventValues = new FastVector();
    HeaderManager labels;
    int columnNumbers[];
    
    
    public void SetLabels(HeaderManager labels){
        this.labels = labels;
    }   
    
    public void SetColNumber(int colNumbers[])
    {
        columnNumbers = colNumbers;
    }
    
 public void update(EventBean[] newData, EventBean[] oldData)  {
         
     System.out.println("Event received: "
                            + newData[0].getUnderlying()
         );
         if (newData.length > 2)
         {
        //create the column name and type, these are strings
        //http://weka.wikispaces.com/Creating+an+ARFF+file
         Instances data;
          ArrayList<Attribute> atts = new ArrayList<Attribute>();
           
             
           for (int j = 0; j < columnNumbers.length; j++)
           {
                ArrayList<Object> values = new ArrayList<Object>();
                for (int i = 0; i < labels.NominalCount(j) ; i++)
                {
                    values.add(labels.GetLabel(columnNumbers[j], i));
                }
                atts.add(new Attribute(labels.GetHeader(columnNumbers[j]), values));
           }

           data = new Instances("Title", atts, 0);
           
         
           
           for (int i = 0; i < newData.length; i++) {
                Instance inst = new Instance(columnNumbers.length);
                for (int j =0; j < columnNumbers.length; j++)
                {
                  inst.setValue(j,newData[i].get("eventType").toString());
                }
               data.add(inst);
           }
           
   
       
        Apriori aprioriObj = new weka.associations.Apriori();
      
        try{
        aprioriObj.buildAssociations(data);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        
        ArrayList<Object>[] rules = aprioriObj.getAllTheRules();
        
       
         }

         
    }
}
