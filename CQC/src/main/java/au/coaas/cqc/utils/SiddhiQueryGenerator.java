package au.coaas.cqc.utils;

import org.json.JSONObject;

import java.util.jar.JarEntry;

public class SiddhiQueryGenerator {
    // Function Signature sent here is the fully-parsed context function. I don't think that is right.
    public static String generateQueries(String type, String window, String functionSignature) {
        Double windowSize;
        String siddhiQuery = "";
        if(window.startsWith("{")){
            JSONObject value = new JSONObject(window);
            switch(value.getString("unit")){
                case "m":
                case "min":
                case "minute":
                    windowSize = value.getDouble("value") * 60000;
                    break;
                case "ms":
                case "mili":
                case "miliseconds":
                    windowSize = value.getDouble("value");
                    break;
                default:
                    windowSize = value.getDouble("value") * 1000;
            }
        } else {
            // Considering the default unit is seconds
            windowSize = Double.valueOf(window) * 1000;
        }

        switch (type) {
            case "decrease":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount > amount and (timestamp - e1.timestamp) < " + windowSize + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize + " and e1.amount > amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
            case "increase":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount < amount and (timestamp - e1.timestamp) < " + windowSize + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize + " and e1.amount < amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
            case "isValid":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount == amount and (timestamp - e1.timestamp) < " + windowSize + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize + " and e1.amount == amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
        }
        return siddhiQuery;
    }

    public static String registerQuery(String id, StringBuilder siddhiQueryBody){
        String siddhiApp = "@app:name('sub_" + id + "') \n"
                + "define stream subs(name string, amount double, timestamp long); \n"
                + "define table eventResultTable (functionSignature string, initialAmount double, "
                + "finalAmount double,timestamp long); \n"
                + "partition with (name of subs) \n"
                + "begin \n"
                + siddhiQueryBody.toString() + "end;";

        return siddhiApp;
    }
}
