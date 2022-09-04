package au.coaas.cpree.utils;

import au.coaas.cpree.utils.enums.MeasuredProperty;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

public class Utilities {
    public static Double unitConverter(MeasuredProperty prop, String originUnit, String targetUnit, double value) {
        switch(prop){
            case DISTANCE: {
                switch (originUnit.toLowerCase()) {
                    case "km":
                        switch (targetUnit.toLowerCase()) {
                            case "m":
                                return value*1000;
                            case "cm":
                                return value*1000*100;
                            case "mm":
                                return value*1000*100*10;
                        }

                    case "m":
                        switch (targetUnit.toLowerCase()) {
                            case "km":
                                return value/1000;
                            case "cm":
                                return value*100;
                            case "mm":
                                return value*100*10;
                        }
                    case "cm":
                        switch (targetUnit.toLowerCase()) {
                            case "km":
                                return value/100000;
                            case "m":
                                return value/100;
                            case "mm":
                                return value*10;
                        }
                    case "mm":
                        switch (targetUnit.toLowerCase()) {
                            case "mm":
                                return value;
                            case "km":
                                return value / 1000000;
                            case "cm":
                                return value / 10;
                            case "m":
                                return value / 1000;
                        }
                }
            }
            case TIME: {
                switch (originUnit.toLowerCase()) {
                    case "h":
                        switch (targetUnit.toLowerCase()) {
                            case "m":
                                return value*60;
                            case "s":
                                return value*3600;
                            case "ms":
                                return value*3600*1000;
                        }

                    case "m":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/60;
                            case "s":
                                return value*60;
                            case "ms":
                                return value*60*1000;
                        }
                    case "s":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/3600;
                            case "m":
                                return value/60;
                            case "ms":
                                return value*1000;
                        }
                    case "ms":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/(3600*1000);
                            case "m":
                                return value/(60*1000);
                            case "s":
                                return value/1000;
                        }
                }
            }
        }
        return value;
    }

    public static AbstractMap.SimpleEntry getDefaultUnitCode(String unitCode) {
        switch (unitCode) {
            case "H":
            case "M":
            case "s":
            case "ms":
                return new AbstractMap.SimpleEntry("s", MeasuredProperty.TIME);
            case "mm":
            case "km":
            case "cm":
            case "m":
                return new AbstractMap.SimpleEntry("m", MeasuredProperty.DISTANCE);
        }
        return null;
    }

    public static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue() + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }

    public static double getZValueForProbability(double prob) {
        if (prob < 0.5) return -getZValueForProbability(1 - prob);

        if (prob > 0.92) {
            if (prob == 1) return Double.NaN;
            double r = Math.sqrt(-Math.log(1-prob));
            return (((2.3212128 * r + 4.8501413) * r - 2.2979648) * r - 2.7871893)/
                    ((1.6370678 * r + 3.5438892) * r + 1);
        }
        prob -= 0.5;
        double r = prob * prob;
        return prob * (((-25.4410605 * r + 41.3911977) * r - 18.6150006) * r + 2.5066282)/
                ((((3.1308291 * r - 21.0622410) * r + 23.0833674) * r - 8.4735109) * r + 1);
    }

    public static double getStandardDeviation(Object[] input_array, double mean) {
        double standard_deviation = 0.0;
        int array_length = input_array.length;
        for(Object temp: input_array) {
            standard_deviation += Math.pow(((double)temp) - mean, 2);
        }
        return Math.sqrt(standard_deviation/array_length);
    }
}
