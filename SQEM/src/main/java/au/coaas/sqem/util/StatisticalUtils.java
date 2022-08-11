package au.coaas.sqem.util;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class StatisticalUtils {
    public static double predictExpectedValue(double[][] data, double x) {
        SimpleRegression simpleRegression = new SimpleRegression();
        simpleRegression.addData(data);
        return simpleRegression.predict(x);
    }
}
