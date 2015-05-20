/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
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
    double accuracy;
    
    public CEPListener(int windowSize) throws InterruptedException {
        tree = CreateClassifier();
        this.windowSize = windowSize;
        data = null;
        cumulative = 0;
        training = true;
        sampleSize = 200;
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
                        Object inst = bean.getUnderlying();
                        data.add((Instance)inst);
                    }
                    for (int i = data.numInstances()- newData.length; i < data.numInstances(); i++) 
                    {
                        double pred = tree.classifyInstance(data.instance(i));
                        System.out.print("ID: " + data.instance(i).value(0));
                        System.out.print(", actual: " + data.classAttribute().value((int) data.instance(i).classValue()));
                        System.out.println(", predicted: " + data.classAttribute().value((int) pred));
                        Evaluation eval = new Evaluation(data);
                        if ((accuracy = eval.rootMeanSquaredError()) < 0.7)
                        {
                           
                            training = true;
                            train.clear();
                            train = null;
                        }
                         System.out.print("Accuracy: " + accuracy);
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
            //rm.setAttributeIndices("1");  // remove 1st attribute
            PART c = new PART();
            // meta-classifier
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(c);
            return fc;
  }
}
