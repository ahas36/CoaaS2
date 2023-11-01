package au.coaas.sqem.entity;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;

public class AttributeItem implements ContextCacheItem {
    private String id;
    private JSONObject lifetime;
    private LocalDateTime zeroTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String refreshLogic = "reactive";
    private HashMap<String, ContextCacheItem> parents;

    public JSONObject getlifetime() {return this.lifetime;}
    public LocalDateTime getZeroTime() {return this.zeroTime;}
    public String getRefreshLogic() {return this.refreshLogic;}
    public HashMap<String, ContextCacheItem> getParents() {return this.parents;}
    public HashMap<String, ContextCacheItem> getChildren() {return null;}

    public void setZeroTime(LocalDateTime time) {this.zeroTime = time;}
    public void setLifetime(JSONObject lifetime) {this.lifetime = lifetime;}
    public void setUpdatedTime(LocalDateTime time) {this.updatedTime = time;}
    public void setParents(String id, ContextCacheItem entity) {this.parents.put(id, entity);}

    public void removeAllParents() {this.parents.clear();}

    // Attribute Node
    public AttributeItem(ContextItem parent, String ownId, LocalDateTime zeroTime, String refAttribute){
        this.id = ownId;
        this.zeroTime = zeroTime;
        this.refreshLogic = "reactive";
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();

        setLifetime(refAttribute);
        // An entity instance is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.getId(), parent);
    }

    private void setLifetime(String refAttribute){
        this.lifetime = new JSONObject();
        this.lifetime.put("unit","s");
        switch(refAttribute.toLowerCase()){
            case "location":
            case "geo": {
                this.lifetime.put("value", 180.0);
                break;
            }
            default: this.lifetime.put("value", 60.0);
        }
    }
}
