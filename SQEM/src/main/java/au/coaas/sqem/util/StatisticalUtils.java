package au.coaas.sqem.util;

import java.util.logging.Logger;
import org.apache.commons.math3.stat.regression.SimpleRegression;

// Class that defines the statistical and machine leaning routines
public class StatisticalUtils {
    private static Logger log = Logger.getLogger(StatisticalUtils.class.getName());

    public static double predictExpectedValue(double[][] data, double x) {
        try{
            SimpleRegression simpleRegression = new SimpleRegression();
            simpleRegression.addData(data);
            return simpleRegression.predict(x);
        }
        catch(Exception ex){
            log.info("Could not perform regression due to error: " + ex.getMessage());
            return Double.NaN;
        }
    }

    public static double getSlope(double[][] data) {
        try {
            SimpleRegression simpleRegression = new SimpleRegression();
            simpleRegression.addData(data);
            // Multiplying by -1 because the list is inverted. So, when the AR is increasing, the  resultant slope as in the
            // dataset would actually be negative, which therefore need to be inverted and vice versa.
            return simpleRegression.getSlope() * -1;
        }
        catch(Exception ex){
            log.info("Could not perform regression due to error: " + ex.getMessage());
            return Double.NaN;
        }
    }
}
