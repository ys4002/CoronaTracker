
package com.example.coronatracker.modelInfected;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Confirmed {

    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;
    @SerializedName("latest")
    @Expose
    private Integer latest;
    @SerializedName("locations")
    @Expose
    private List<Location> locations = null;
    @SerializedName("source")
    @Expose
    private String source;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getLatest() {
        return latest;
    }

    public void setLatest(Integer latest) {
        this.latest = latest;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
