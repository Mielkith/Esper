/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEP;

import com.espertech.esper.client.util.DateTime;
import static com.espertech.esper.client.util.DateTime.DEFAULT_XMLLIKE_DATE_FORMAT;
import com.espertech.esper.event.map.MapEventBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gs023850
 */
public abstract class Tick implements Map{
    
    private ArrayList keys;
    private ArrayList values;
    
    /*private String  time;
    private String  className;
    private String  classDisplayName;
    private String  elementClassName;
    private String  sourceEventType;
    private Integer duration;
    private Boolean isProblem;
    private String  eventType;
    private String  category;
    private Integer severity;
    private Integer impact;
    private Integer certainty;
    private Boolean troubleTicketID;*/
    
    Tick(ArrayList keys){
    time = DEFAULT_XMLLIKE_DATE_FORMAT;
    this.keys = keys;
    }
    
    
    
    
    
}
