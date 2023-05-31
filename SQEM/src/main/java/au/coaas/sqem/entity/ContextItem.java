package au.coaas.sqem.entity;

import au.coaas.sqem.proto.CacheLookUp;
import com.mongodb.util.JSON;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;

public class ContextItem {
    private String id;
    private String refreshLogic;
    private JSONObject lifetime;
    private LocalDateTime zeroTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private HashMap<String, ContextItem> parents;
    private HashMap<String, ContextItem> children;

    private String getId() {return this.id;}
    public JSONObject getlifetime() {return this.lifetime;}
    public LocalDateTime getZeroTime() {return this.zeroTime;}
    public String getRefreshLogic() {return this.refreshLogic;}
    public LocalDateTime getUpdatedTime() {return this.updatedTime;}
    public HashMap<String, ContextItem> getParents() {return this.parents;}
    public HashMap<String, ContextItem> getChildren() {return this.children;}

    public void setZeroTime(LocalDateTime time) {this.zeroTime = time;}
    public void setRefreshLogic(String logic) {this.refreshLogic = logic;}
    public void setUpdatedTime(LocalDateTime time) {this.updatedTime = time;}
    public void setParents(String id, ContextItem entity) {this.parents.put(id, entity);}

    // Context Entity Node
    public ContextItem(CacheLookUp lookup, String hashKey, String refreshLogic, LocalDateTime zeroTime){
        this.id = lookup.getEt().getType();
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // No parents because of being the root
        this.parents = null;
        // Multiple Context Services
        this.children = new HashMap<>();
        String serId = lookup.getServiceId();
        if(serId.startsWith("{")){
            JSONObject obj = new JSONObject(serId);
            serId = obj.getString("$oid");
        }
        this.children.put(serId, new ContextItem(this, serId, hashKey, refreshLogic, zeroTime));
    }

    // Context Provider Node
    public ContextItem(ContextItem parent, String ownId, String hashKey, String refreshLogic, LocalDateTime zeroTime){
        this.id = ownId;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Entity Type is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // Multiple Context Entities.
        this.children = new HashMap<>();

        String entityType = parent.getId();
        this.children.put(hashKey, new ContextItem(this, hashKey, refreshLogic, zeroTime, entityType));
    }

    // Entity Object
    public ContextItem(ContextItem parent, String ownId, LocalDateTime zeroTime, String entityType){
        this.id = ownId;
        this.refreshLogic = "reactive";
        // Times
        setLifetime(entityType);
        this.zeroTime = zeroTime;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Root is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // No children for the context entities for now.
        this.children = null;
    }

    // Entity Object
    public ContextItem(ContextItem parent, String ownId, String refreshLogic, LocalDateTime zeroTime, String entityType){
        this.id = ownId;
        this.refreshLogic = refreshLogic;
        // Times
        setLifetime(entityType);
        this.zeroTime = zeroTime;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Root is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // No children for the context entities for now.
        this.children = null;
    }

    private void setLifetime(String entityType){
        this.lifetime = new JSONObject();
        this.lifetime.put("unit","s");
        switch(entityType){
            case "car":
            case "vehicle": {
                this.lifetime.put("value",30.0);
                break;
            }
            case "thing":
            case "weather": {
                this.lifetime.put("value",3600.0);
                break;
            }
            case "place": {
                this.lifetime.put("value",1800.0);
                break;
            }
            case "carpark":
            case "parkingfacility": {
                this.lifetime.put("value",60.0);
                break;
            }
            default: this.lifetime.put("value",1200.0);
        }
    }
}
