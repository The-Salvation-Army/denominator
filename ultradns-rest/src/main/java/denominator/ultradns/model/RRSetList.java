package denominator.ultradns.model;

import java.util.ArrayList;
import java.util.List;

public class RRSetList {

    private String zoneName;
    private List<RRSet> rrSets;
    private ResultInfo resultInfo;

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public List<RRSet> getRrSets() {
        return rrSets;
    }

    public void setRrSets(List<RRSet> rrSets) {
        this.rrSets = rrSets;
    }

    public List<RRSet> rrSets() {
        if (getRrSets() != null && !getRrSets().isEmpty()) {
            return getRrSets();
        }
        return new ArrayList<RRSet>();
    }

    public ResultInfo getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
    }
}
