package au.coaas.sqem.entity;

import au.coaas.sqem.proto.CacheLookUp;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ContextItem {
    private String id;
    private String refreshLogic;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private HashMap<String, ContextItem> children;
    private HashMap<String, ContextItem> parents;

    public String getRefreshLogic() {return this.refreshLogic;}
    public LocalDateTime getCreatedTime() {return this.createdTime;}
    public LocalDateTime getUpdatedTime() {return this.updatedTime;}
    public HashMap<String, ContextItem> getChildren() {return this.children;}
    public HashMap<String, ContextItem> getParents() {return this.parents;}

    public void setRefreshLogic(String logic) {this.refreshLogic = logic;}
    public void setCreatedTime(LocalDateTime time) {this.createdTime = time;}
    public void setUpdatedTime(LocalDateTime time) {this.updatedTime = time;}
    public void setParents(String id, ContextItem entity) {this.parents.put(id, entity);}

    // Context Entity Node
    public ContextItem(CacheLookUp lookup, String hashKey, String refreshLogic){
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
        this.children.put(serId, new ContextItem(this, hashKey, refreshLogic));
    }

    public ContextItem(ContextItem parent, String ownId, String hashKey, String refreshLogic){
        this.id = ownId;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Entity Type is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // Multiple Context Entities.
        this.children = new HashMap<>();
        this.children.put(hashKey, new ContextItem(this, refreshLogic));
    }

    public ContextItem(ContextItem parent, String ownId){
        this.id = ownId;
        this.refreshLogic = "reactive";
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Root is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // No children for the context entities for now.
        this.children = null;
    }

    public ContextItem(ContextItem parent, String ownId, String refreshLogic){
        this.id = ownId;
        this.refreshLogic = refreshLogic;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Root is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // No children for the context entities for now.
        this.children = null;
    }

}
