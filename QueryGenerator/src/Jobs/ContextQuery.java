package Jobs;

public class ContextQuery {

    public String day;
    public int hour;
    public int minute;
    public int second;
    private String query;
    private String queryId;
    private String authToken;

    public ContextQuery(String day, int hour, int min, int sec,
                        String query, String queryId, String authToken){
        this.day = day;
        this.hour = hour;
        this.minute = min;
        this.second = sec;
        this.query = query;
        this.queryId = queryId;
        this.authToken = authToken;
    }

    public String getQuery(){
        return this.query;
    }

    public String getQueryId(){
        return this.queryId;
    }

    public String getToken(){
        return this.authToken;
    }
}
