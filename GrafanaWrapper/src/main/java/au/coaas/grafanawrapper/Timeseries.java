/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafanawrapper;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ali
 */
@XmlRootElement
public class Timeseries {

    private String target;
    private List<Object[]> datapoints;

    public Timeseries() {
    }

    public Timeseries(String target) {
        this.target = target;
        this.datapoints = new ArrayList<>();
    }

    public Timeseries(String target, List<Object[]> datapoints) {
        this.target = target;
        this.datapoints = datapoints;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<Object[]> getDatapoints() {
        return datapoints;
    }

    public void addDatapoints(Object[] datapoints) {
        this.datapoints.add(datapoints);
    }

}
