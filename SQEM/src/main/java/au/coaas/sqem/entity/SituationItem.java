package au.coaas.sqem.entity;

import au.coaas.sqem.util.Utilty;
import au.coaas.cqp.proto.Operand;
import au.coaas.sqem.proto.SituationLookUp;

import org.json.JSONObject;

import java.util.List;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class SituationItem implements ContextCacheItem {
    private String id;
    private int predWindow;
    private String refreshLogic;
    private JSONObject lifetime;
    private List<Operand> operands;
    private LocalDateTime zeroTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private HashMap<String, ContextCacheItem> parents;
    private HashMap<String, ContextCacheItem> children;

    public String getId() {return this.id;}
    public int getPredWindow() {return this.predWindow;}
    public JSONObject getlifetime() {return this.lifetime;}
    public LocalDateTime getZeroTime() {return this.zeroTime;}
    public String getRefreshLogic() {return this.refreshLogic;}
    public HashMap<String, ContextCacheItem> getParents() {return this.parents;}
    public HashMap<String, ContextCacheItem> getChildren() {return this.children;}

    public void setZeroTime(LocalDateTime time) {this.zeroTime = time;}
    public void setLifetime(JSONObject lifetime) {this.lifetime = lifetime;}
    public void setUpdatedTime(LocalDateTime time) {this.updatedTime = time;}
    public void setPredWindow(int predWindow) {this.predWindow = predWindow;}
    public void setOperands(List<Operand> operands) {this.operands = operands;}
    public void setChildren(SituationItem situ) {this.children.put(situ.getId(), situ);}
    public void setChildren(ContextItem entity) {this.children.put(entity.getId(), entity);}
    public void setParents(String id, ContextCacheItem entity) {this.parents.put(id, entity);}

    public void removeChild(String id) {this.children.remove(id);}
    public void removeAllChildren() {this.children.clear();}
    public void removeAllParents() {this.parents.clear();}

    // Situation Reference Node
    public SituationItem(SituationLookUp lookUp, LocalDateTime zeroTime){
        this.predWindow = 0;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.id = lookUp.getFunction().getFunctionName();
        // No parents because of being the root
        this.parents = null;
        // Multiple specific instances of the same situation
        this.children = new HashMap<>();

        this.children.put(lookUp.getUniquehashkey(),
                new SituationItem(this, lookUp.getUniquehashkey(),
                        lookUp.getFunction().getArgumentsList(), zeroTime));
    }

    public SituationItem(SituationLookUp lookUp){
        this.predWindow = 0;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.id = lookUp.getFunction().getFunctionName();
        // No parents because of being the root
        this.parents = null;
        // Multiple specific instances of the same situation
        this.children = new HashMap<>();
    }

    // Situation Node
    public SituationItem(SituationItem parent, String ownId, List<Operand> operands, LocalDateTime zeroTime){
        this.id = ownId;
        this.predWindow = 0;
        this.operands = operands;
        this.zeroTime = zeroTime;
        this.refreshLogic = "reactive";
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();

        setLifetime(parent.getId());
        // Situation Type is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // Multiple Context Entities.
        // Not yet resolved here since the tree needs to be breadth searched
        // to find the entities from the other sub-tree when creating a cache reference.
        this.children = new HashMap<>();
    }

    private void setLifetime(String functionName){
        this.lifetime = new JSONObject();
        this.lifetime.put("unit","s");
        switch(functionName.toLowerCase()){
            case "goodforwalking": {
                this.lifetime.put("value",3600.0);
                break;
            }
            case "hazardous": {
                // Hazard levels are so short lived.
                this.lifetime.put("value",1);
                break;
            }
            default: this.lifetime.put("value",1800.0);
        }
    }
}
