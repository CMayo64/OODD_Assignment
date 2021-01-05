package org.solent.com528.project.model.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StationsList {

    @XmlElementWrapper(name = "stationList")
    @XmlElement(name = "station")
    List<Station> stationList = new ArrayList<Station>();

    public List<Station> getStationList() {
        return stationList;
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("StationList{" + "stationList:\n");
        for (Station stn : stationList) {
            sb.append("   " + stn.toString() + "\n");
        }
        return sb.toString();
    }

}
