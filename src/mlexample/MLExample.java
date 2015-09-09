package mlexample;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import gaussReduction.gauss;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MLExample {

    public static void main(String[] args) {
        MLExample mle = new MLExample();
        //mle.startFull();
        mle.startHalf();
        double x = 10.9;
        double y = mle.solveForY(x);
        System.out.println();
        System.out.println("Y for X of " + x + " is:");
        System.out.println(y);
    }
    
    private static final String logPath = "./MLLog/";
    private double slope;
    private double intercept;
    
    private void startFull() {
        dataGeneration();
    }
    
    private void startHalf() {
        File xLog = new File(logPath + "xLog.txt");
        File yLog = new File(logPath + "yLog.txt");
        
        dataAnalysisSetup(xLog, yLog);
    }
    
    private void dataLogging(String[] s) {
        File xLog = new File(logPath + "xLog.txt");
        File yLog = new File(logPath + "yLog.txt");
        PrintWriter xPrint;
        PrintWriter yPrint;
        try {
            xPrint = new PrintWriter(new FileWriter(xLog, true));
            yPrint = new PrintWriter(new FileWriter(yLog, true));
            
            for(String data : s) {
                String[] split = data.split("-");
                xPrint.println(split[0]);
                yPrint.println(split[1]);
            }
            
            xPrint.close();
            yPrint.close();
        } catch (Exception ex) {
            Logger.getLogger(MLExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dataAnalysisSetup(xLog, yLog);
    }
    
    private void dataGeneration() {
        String[] data = new String[100000];
        Random rand = new Random();
        for(int i = 0; i < 100000; i++) {
            int x = rand.nextInt(10);
            int y = rand.nextInt(100);
            
            data[i] = x + "-" + y;
        }
        dataLogging(data);
    }
    
    private void dataAnalysisSetup(File x, File y) {
        ArrayList gradeList = new ArrayList<>();
        ArrayList timeList = new ArrayList<>();
        int[] gradeArray = null;
        int[] timeArray = null;
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(x));
            String line;
            while ((line = br.readLine()) != null) {
               gradeList.add(line);
            }
            String[] gradeStringArray = (String[]) gradeList.toArray(new String[gradeList.size()]);
            gradeArray = new int[gradeStringArray.length];
            for(int i = 0; i < gradeStringArray.length; i++) {
                gradeArray[i] = Integer.parseInt(gradeStringArray[i]);
            }
            
            BufferedReader br2 = new BufferedReader(new FileReader(y));
            String line2;
            while ((line2 = br2.readLine()) != null) {
               timeList.add(line2);
            }
            String[] timeStringArray = (String[]) timeList.toArray(new String[timeList.size()]);
            timeArray = new int[timeStringArray.length];
            for(int i = 0; i < timeStringArray.length; i++) {
                timeArray[i] = Integer.parseInt(timeStringArray[i]);
            }
        } catch (IOException e) {
            Logger.getLogger(MLExample.class.getName()).log(Level.SEVERE, null, e);
        }
        
        dataAnalysis(gradeArray, timeArray);
    }
    
    private void dataAnalysis(int[] x, int[] y) {
        int len = x.length;
        long[][] xMatrix = new long[len][2];
        long[][] x2Matrix = new long[2][len];
        
        for(int i = 0; i < len; i++) {
            //placeholders
            xMatrix[i][0] = 1;
            x2Matrix[0][i] = 1;
            
            //x coordinates
            xMatrix[i][1] = x[i];
            x2Matrix[1][i] = x[i];
        }
        
        //sums for result matrix
        long xSumOne = 0;
        long xSumTwo = 0;
        long xSumThree = 0;
        long xSumFour = 0;
        
        long ySumOne = 0;
        long ySumTwo = 0;
        
        //multiplying matricies
        for(int j = 0; j < len; j++) {
            xSumOne = xSumOne + (xMatrix[j][0] * x2Matrix[0][j]);
            xSumTwo = xSumTwo + (xMatrix[j][0] * x2Matrix[1][j]);
            xSumThree = xSumThree + (xMatrix[j][1] * x2Matrix[0][j]);
            xSumFour = xSumFour + (xMatrix[j][1] * x2Matrix[1][j]);
            
            ySumOne = ySumOne + (xMatrix[j][0] * y[j]);
            ySumTwo = ySumTwo + (xMatrix[j][1] * y[j]);
        }
        
        //result matricies
        long[][] xResult = new long[2][2];
        long[] yResult = new long[2];
        
        xResult[0][0] = xSumOne;
        xResult[0][1] = xSumTwo;
        xResult[1][0] = xSumThree;
        xResult[1][1] = xSumFour;
        
        yResult[0] = ySumOne;
        yResult[1] = ySumTwo;
        
        //matlab function for gaussian elimination
        Object[] slopeIntercept = null;
        try {
            gauss g = new gauss();
            MWNumericArray xArray = new MWNumericArray(xResult, MWClassID.DOUBLE);
            MWNumericArray yArray = new MWNumericArray(yResult, MWClassID.DOUBLE);
            slopeIntercept = g.gaussReduction(2, xArray, yArray);
        } catch (MWException ex) {
            Logger.getLogger(MLExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //line equation variables
        String s = slopeIntercept[1].toString();
        String i = slopeIntercept[0].toString();
        slope = Double.parseDouble(s);
        intercept = Double.parseDouble(i);
        
        //prints
        System.out.println("X 2x2 Result Matrix:");
        System.out.println(xResult[0][0] + " " + xResult[0][1]);
        System.out.println(xResult[1][0] + " " + xResult[1][1]);
        System.out.println();
        System.out.println("Y 2x1 Result Matrix:");
        System.out.println(yResult[0]);
        System.out.println(yResult[1]);
        System.out.println();
        System.out.println("Intercept and Slope:");
        System.out.println(slopeIntercept[0]);
        System.out.println(slopeIntercept[1]);
    }
    
    private double solveForY(double x) {
        double y = (x * slope) + intercept;        
        return y;
    }
}