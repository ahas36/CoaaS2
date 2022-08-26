package au.coaas.sqem.util;

import org.apache.commons.math3.stat.regression.SimpleRegression;

// Class that defines the statistical and machine leaning routines
public class StatisticalUtils {
    public static double predictExpectedValue(double[][] data, double x) {
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        return simpleRegression.predict(x);
    }

    public static double getSlope(double[][] data) {
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        // Multiplying by -1 because the list is inverted. So, when the AR is increasing, the  resultant slope as in the
        // dataset would actually be negative, which therefore need to be inverted and vice versa.
        return simpleRegression.getSlope() * -1;
    }
}
