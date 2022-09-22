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

    public static SimpleRegression getSlope(double[][] data) {
        try {
            SimpleRegression simpleRegression = new SimpleRegression();
            simpleRegression.addData(data);
            // Multiplying by -1 because the list is inverted. So, when the AR is increasing, the  resultant slope as in the
            // dataset would actually be negative, which therefore need to be inverted and vice versa.
            return simpleRegression;
        }
        catch(Exception ex){
            log.info("Could not perform regression due to error: " + ex.getMessage());
            return null;
        }
    }

    public static double getVariance(double[] data){
        if(data.length > 0){
            double sum = 0;
            for (int i = 0; i < data.length; i++)
                sum += data[i];
            double mean = (double) sum / data.length;

            double sqDiff = 0;
            for (int i = 0; i < data.length; i++)
                sqDiff += (data[i] - mean) * (data[i] - mean);

            return (double)sqDiff / data.length;
        }

        return 0;
    }

    static double getStandardDeviation(double arr[])
    {
        return Math.sqrt(getVariance(arr));
    }
}
