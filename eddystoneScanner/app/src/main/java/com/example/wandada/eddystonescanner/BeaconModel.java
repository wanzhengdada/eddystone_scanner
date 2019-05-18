package com.example.wandada.eddystonescanner;

public class BeaconModel {
    String beaconName;
    String distance;
    String Namespace;
    String Instance;
    String Tlm;

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getNameSpace() {
        return Namespace;
    }

    public void setNameSpace(String Namespace) {
        this.Namespace = Namespace;
    }

    public String getInsTance() {
        return Instance;
    }
    public void setInsTance(String Instance) {
        this.Instance = Instance;
    }

    public String getTLM() {
        return Tlm;
    }
    public void setTLM(String Tlm) {
        this.Tlm = Tlm;
    }


}
