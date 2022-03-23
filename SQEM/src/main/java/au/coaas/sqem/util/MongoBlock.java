package au.coaas.sqem.util;

import com.mongodb.Block;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

public class MongoBlock implements Block<Document> {

    private Long totalCount;
    private Integer page;
    private Integer limit;
    private Boolean isHistorical;
    private JSONArray finalResultJsonArr;

    private static final JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();

    public MongoBlock() {
        this.isHistorical = false;
        finalResultJsonArr = new JSONArray();
    }

    public MongoBlock(Long totalCount, Integer page, Integer limit) {
        this.isHistorical = true;
        this.page = page;
        this.limit = limit;
        finalResultJsonArr = new JSONArray();
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Boolean getHistorical() {
        return isHistorical;
    }

    public void setHistorical(Boolean historical) {
        isHistorical = historical;
    }

    @Override
    public void apply(Document document) {
        JSONObject resultJSON = new JSONObject(document.toJson(settings));
        finalResultJsonArr.put(resultJSON);
    }

    // Returns is pagination details
    public String getMeta(){
        if(!isHistorical || this.page == null){
            return "{}";
        }
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("totalNumberOfDocuments",this.totalCount);
        resultJSON.put("numberOfDocumentsInPage",this.finalResultJsonArr.length());

        if(this.page > 0){
            resultJSON.put("previous_page", Math.min(this.totalCount / this.limit,this.page-1));
        }
        if((this.page+1) * this.limit <= this.totalCount){
            resultJSON.put("next_page",this.page+1);
            resultJSON.put("last_page", this.totalCount / this.limit);
        }
        resultJSON.put("current_page",this.page);

        return resultJSON.toString();
    }

    public String getResultString() {
        return finalResultJsonArr.toString();
    }
}
