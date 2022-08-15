package au.coaas.cpree.utils;

import au.coaas.cpree.utils.enums.MeasuredProperty;

import java.util.AbstractMap;

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
}
