/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import weka.core.Instances;

/**
 *
 * @author gs023850
 */
public  class HeaderManager {
   
    static Instances ArffStructure;
    static Boolean lock = true;
    
    static void SetStructure(Instances structure)
    { ArffStructure = structure;
     structure.setClassIndex(structure.numAttributes()-1);

    lock = false;}
    
    static Instances GetStructure() throws InterruptedException
    {
        while (lock == true){
            Thread.sleep(1);
        }
        return ArffStructure;
    }
    
    

 static Instances GetEmptyStructure() throws InterruptedException
    {
        while (lock == true){
            Thread.sleep(1);
        }
        return ArffStructure.stringFreeStructure();
    }
    
    

}
    



