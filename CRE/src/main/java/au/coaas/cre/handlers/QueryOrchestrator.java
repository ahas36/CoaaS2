package au.coaas.cre.handlers;

public class QueryOrchestrator {
    private static String siddhiQueryGenerator(String type, double windowSize, String functionSignature) {
        String siddhiQuery = "";
        switch (type) {
            case "decrease":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount > amount and (timestamp - e1.timestamp) < " + windowSize * 60000 + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize * 60000 + " and e1.amount > amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
            case "increase":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount < amount and (timestamp - e1.timestamp) < " + windowSize * 60000 + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize * 60000 + " and e1.amount < amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
            case "isValid":
                siddhiQuery = "    from every e1=subs,\n"
                        + "e2=subs[e1.amount == amount and (timestamp - e1.timestamp) < " + windowSize * 60000 + "]*,\n"
                        + "e3=subs[timestamp - e1.timestamp > " + windowSize * 60000 + " and e1.amount == amount]\n"
                        + "select '" + functionSignature + "' as functionSignature,  e1.amount as initialAmount, e3.amount as finalAmount, e3.timestamp\n"
                        + "insert into eventResultTable;\n";
                break;
        }
        return siddhiQuery;
    }
}
