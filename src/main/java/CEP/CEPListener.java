/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.UpdateListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.associations.Apriori;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author gs023850
 */
public class CEPListener implements UpdateListener{
    
    Instances data, train;
    boolean training;
    FilteredClassifier tree;
    int cumulative, sampleSize, windowSize;

    public CEPListener(int windowSize) throws InterruptedException {
        tree = CreateClassifier();
        this.windowSize = windowSize;
        data = null;
        cumulative = 0;
        training = true;
        sampleSize = 20;
    }
    
    
    public void SetLabels(HeaderManager labels){
       // this.labels = labels;
    }   
    
 public void update(EventBean[] newData, EventBean[] oldData)  {
         
     System.out.println("Event received: " + newData[0].getUnderlying());

        if (newData.length > 0)
        {
            try
            {
                if (training)
                {
                    if (train == null)
                    {
                           train = HeaderManager.GetEmptyStructure();
                    }
                    for(EventBean bean:newData)
                    {
                        EventType event = bean.getEventType();
                        Object inst = bean.getUnderlying();
                        train.add((Instance)inst);                     
                    }
                    if (train.size() >= sampleSize)
                    { 
                        tree.buildClassifier(train);
                        training = false;
                    }    
                }
                else
                {
                    if (data == null)
                    {
                        data = HeaderManager.GetStructure();
                    }
                    
                    data = SetDuration(data);
                    cumulative += data.size();
                      
                    for(EventBean bean:newData)
                    {
                        EventType event = bean.getEventType();
                        Object inst = bean.getUnderlying();

                        data.add((Instance)inst);
                    }
                    for (int i = 0; i < data.numInstances(); i++) 
                    {
                        double pred = tree.classifyInstance(data.instance(i));
                        System.out.print("ID: " + data.instance(i).value(0));
                        System.out.print(", actual: " + data.classAttribute().value((int) data.instance(i).classValue()));
                        System.out.println(", predicted: " + data.classAttribute().value((int) pred));
                    }
                }
            }
            catch (InterruptedException ex){          
                Logger.getLogger(CEPListener.class.getName()).log(Level.SEVERE, null,ex);
            } 
            catch (Exception ex) {
             Logger.getLogger(CEPListener.class.getName()).log(Level.SEVERE, null, ex);
             }  


        }
}
 
  Instances SetDuration(Instances in) throws InterruptedException
    {
        Instances out = HeaderManager.GetEmptyStructure();
          for(Instance inst:in)
            {

               double time = inst.toDoubleArray()[5];
               if (time > 20) {
                    inst.setValue(5, time - windowSize);
                    out.add(inst);
                }
            }
        return out;
        
    }
  
  FilteredClassifier CreateClassifier()
  {
            Remove rm = new Remove();
            rm.setAttributeIndices("1");  // remove 1st attribute
            J48 j48 = new J48();
            j48.setUnpruned(true);        // using an unpruned J48
            // meta-classifier
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(j48);
            return fc;
  }
}
/*
public class oldData {
    
    EventBean[] oldData;
    
    oldData(){};
    
    void Data(EventBean[] data)
    {
        oldData = new EventBean[data.length];
        
    
    
     for(EventBean bean:oldData)
                {
                    Instance inst = (Instance)bean.getUnderlying();
                    Long t;
                    if ((t = Long.parseLong(inst.toString(5))) > 20) 
                    {
                    t = t - 20;
                    inst.setValue(5, t.toString());
                   // data.add(inst);
                    }
                    
                }
    
    }
    
    
}*/