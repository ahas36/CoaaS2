package au.coaas.sqem.entity;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;

public interface ContextCacheItem {
    String getRefreshLogic();
    JSONObject getlifetime();
    LocalDateTime getZeroTime();
    HashMap<String, ContextCacheItem> getParents();
    HashMap<String, ContextCacheItem> getChildren();

    void setZeroTime(LocalDateTime time);
    void setUpdatedTime(LocalDateTime time);
    void setParents(String id, ContextCacheItem entity);
}
