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
    private String refreshLogic;
    private JSONObject lifetime;
    private List<Operand> operands;
    private LocalDateTime zeroTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private HashMap<String, ContextCacheItem> parents;
    private HashMap<String, ContextCacheItem> children;

    public String getId() {return this.id;}
    public JSONObject getlifetime() {return this.lifetime;}
    public LocalDateTime getZeroTime() {return this.zeroTime;}
    public String getRefreshLogic() {return this.refreshLogic;}
    public HashMap<String, ContextCacheItem> getParents() {return this.parents;}
    public HashMap<String, ContextCacheItem> getChildren() {return this.children;}

    public void setZeroTime(LocalDateTime time) {this.zeroTime = time;}
    public void setLifetime(JSONObject lifetime) {this.lifetime = lifetime;}
    public void setUpdatedTime(LocalDateTime time) {this.updatedTime = time;}
    public void setChildren(ContextItem entity) {this.children.put(entity.getId(), entity);}
    public void setParents(String id, ContextCacheItem entity) {this.parents.put(id, entity);}

    // Situation Reference Node
    public SituationItem(SituationLookUp lookUp){
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.id = lookUp.getFunction().getFunctionName();
        // No parents because of being the root
        this.parents = null;
        // Multiple specific instances of the same situation
        this.children = new HashMap<>();

//        String situId = Utilty.combineHashKeys(lookUp.getFunction().getArgumentsList().stream().map(x -> {
//            String strValue = x.getStringValue();
//            return (new JSONObject(strValue)).getString("hashkey");
//        }).collect(Collectors.toList()));

        this.children.put(lookUp.getUniquehashkey(),
                new SituationItem(this, lookUp.getUniquehashkey(), lookUp.getFunction().getArgumentsList()));
    }

    // Situation Node
    public SituationItem(SituationItem parent, String ownId, List<Operand> operands){
        this.id = ownId;
        this.operands = operands;
        this.refreshLogic = "reactive";
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        // Situation Type is the parent
        this.parents = new HashMap<>();
        this.parents.put(parent.id, parent);
        // Multiple Context Entities.
        // Not yet resolved here since the tree needs to be breadth searched
        // to find the entities from the other sub-tree when creating a cache reference.
        this.children = new HashMap<>();
    }
}
