/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 18, 2015
 * Time: 11:34:19 AM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: DFTProcessing
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import static hw03.Utility.DFTProcessing.fastDFT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the class which store each peak as an object of magnitude and frequency
 * Implement the Comparable interface to utilize java's default sorting
 * algorithm for faster sorting
 *
 * @author khainguyen
 */
class Peak implements Comparable<Peak> {

//magnitude
    private double magnitude;
    private double frequency;

    public Peak(double magnitude, double frequency) {
        this.magnitude = magnitude;
        this.frequency = frequency;
    }

    /**
     * return 1 if the current peak's magnitude is smaller, -1 if bigger, and 0
     * if equal
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Peak t) {
        return -Double.compare(this.magnitude, t.magnitude);
    }

    /**
     * get the magnitude
     *
     * @return - the magnitude of the current peak
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * get the frequency
     *
     * @return - the frequency of the current peak
     */
    public double getFrequency() {
        return frequency;
    }
}

/**
 * the class for multi-threading FFT.
 *
 * Perform FFT transform by using multi-threading with each divide and conquer
 * step
 *
 * For some reasons, it actually runs slower than the normal method!?!?
 *
 * @author kqn001
 */
class multiFFT implements Runnable {
    // the input array to be transformed
    private Complex[] dftArray;
    // the result DFT of the input array
    private Complex[] result;
    //store the minimum value of array length to stop threading
    private double limit;

    /**
     * Constructor
     *
     * @param fft - a complex array
     * @author kqn001
     */
    public multiFFT(Complex[] fft, double limit) {
        this.dftArray = fft;
        this.limit = limit;
    }

    /**
     * the run method, used to initiate the multi-threading process of the class
     * SLOW
     *
     * @author kqn001
     */
    @Override
    public void run() {
        try {
            result = this.fastDFTMulti();
        } catch (DFTException ex) {
            Logger.getLogger(multiFFT.class.getName()).log(Level.SEVERE, null,
                                                           ex);
        }
    }

    /**
     * the method to perform FFT transformation. It uses the object's attribute
     * as the source for FFT transform and output return the transform of it.
     *
     * This method is similar to the usual FFT, except that it utilizes
     * multi-threading with each divide step
     *
     * The code, similar to FFT, is adapted from FFT Java code at princeton.edu.
     *
     * @see
     * <a href="http://introcs.cs.princeton.edu/java/97data/FFT.java.html">
     * http://introcs.cs.princeton.edu/java/97data/FFT.java.html</a>
     * @return - the DFT of the input
     * @author kqn001
     */
    public Complex[] fastDFTMulti() throws DFTException {
        int n = dftArray.length;
        //doesnt make any difference
        if (n == 32) {
            return DFTProcessing.slowDFT(dftArray);
        } else {
            Complex[] evenArray = new Complex[n / 2];
            Complex[] oddArray = new Complex[n / 2];
            for (int i = 0; i < n / 2; i++) {
                evenArray[i] = dftArray[i * 2];

                oddArray[i] = dftArray[i * 2 + 1];
            }
            // if too small, don't create new thread
            if (n < this.limit) {
                evenArray = fastDFT(evenArray);
                oddArray = fastDFT(oddArray);
                //create new thread
            } else {
                multiFFT evenMultiArray = new multiFFT(evenArray, this.limit);
                multiFFT oddMultiArray = new multiFFT(oddArray, this.limit);
                Thread evenThread = new Thread(evenMultiArray);
                Thread oddThread = new Thread(oddMultiArray);
                evenThread.start();
                oddThread.start();
                while (true) {
                    if (!evenThread.isAlive() && !oddThread.isAlive()) {
                        break;
                    }
                }
                evenArray = evenMultiArray.getArray();
                oddArray = oddMultiArray.getArray();
            }
            Complex[] newDFTArray = new Complex[n];
            for (int i = 0; i < n / 2; i++) {
                double twiddleFactor = -2 * Math.PI * i / n;
                Complex twComplex = new Complex(Math.cos(twiddleFactor),
                                                Math.sin(twiddleFactor));
                newDFTArray[i] = evenArray[i].add(
                        oddArray[i].multiply(twComplex));
                newDFTArray[i + n / 2] = evenArray[i].subtract(
                        oddArray[i].multiply(twComplex));

            }
            result = newDFTArray;
            return newDFTArray;

        }
    }

    /**
     * return the result DFT of the input array
     *
     * @return - the DFT of the input dftArray
     * @author kqn001
     */
    public Complex[] getArray() {
        return result;
    }
}

/**
 * This class contains functions to perform different actions for DFT
 * processing, including DFT transformation and calculate the n peaks of a
 * certain data set
 *
 * Can also acts as an object to store different information about a DFT
 * process.
 *
 * @author kqn001
 */
public class DFTProcessing {
    // the input complex array on which DFT will be performanced
    private Complex[] inComplexes;
    // the frequency DFT array after transformation
    private double[] freqArray;
    //the peaks of the data set and their according frequency
    private Peak[] peakArray;
    //the sample rate of the input array
    private double sampleRate;

    /**
     * Constructor
     *
     * @param inSignal - a complex array
     * @author kqn001
     * @param sampleRate - the sample rate of the input data
     */
    public DFTProcessing(Complex[] inSignal, double sampleRate) {
        this.inComplexes = inSignal;
        this.freqArray = new double[inSignal.length];
        this.sampleRate = sampleRate;
    }

    /**
     * Perform one of the three DFT transformation based on input, then
     * calculate the peak and store the values
     *
     * @param type - the type of DFT transformation, SLOW, FAST or MULTI.
     * @author kqn001
     * @throws hw03.Utility.DFTException
     */
    public void calculateDFT(DFTType type) throws DFTException {
        long startTime = System.currentTimeMillis();
        Complex[] dftComplexs;
        if (type == DFTType.SLOW) {
            dftComplexs = slowDFT(inComplexes);
        } else if (type == DFTType.FAST) {
            dftComplexs = fastDFT(inComplexes);
        } else {
            dftComplexs = FFTMulti(inComplexes);
        }
        for (int i = 0; i < dftComplexs.length; i++) {
            this.freqArray[i] = dftComplexs[i].magnitude();
        }
        long total = System.currentTimeMillis() - startTime;
        this.calculatePeaks();
        System.out.println("Running Time:" + total);
    }

    /**
     * perform slow DFT transformation on an complex array
     *
     * The code is based on the code found at http://www.nayuki.io
     *
     * @see <a href =
     * "http://www.nayuki.io/page/how-to-implement-the-discrete-fourier-transform"?
     * http://www.nayuki.io/page/how-to-implement-the-discrete-fourier-transform
     * <\a>
     *
     * @throws hw03.Utility.DFTException if the array length is not a power of 2
     * @param inputComplexes - a complex array to transform @return - a complex
     * array, the resulted DFT @author kqn001
     * @return - the resulted DFT array
     * @author kqn001
     */
    public static Complex[] slowDFT(Complex[] inputComplexes) throws DFTException {
        if (!ProcessingUtility.isPowerOf2(inputComplexes.length)) {
            throw new DFTException("Not a power of 2!");
        } else {
            Complex[] newDFT = new Complex[inputComplexes.length];
            int n = inputComplexes.length;
            for (int k = 0; k < n; k++) {
                double outReal = 0;
                double outImag = 0;
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i * k / n;
                    outReal += inputComplexes[i].getRe() * Math.cos(angle) + inputComplexes[i].getIm() * Math.sin(
                            angle);
                    outImag += -inputComplexes[i].getRe() * Math.sin(angle) + inputComplexes[i].getIm() * Math.cos(
                            angle);
                }
                newDFT[k] = new Complex(outReal, outImag);
            }
            return newDFT;
        }
    }

    /**
     * perform slow DFT transformation on an complex array
     *
     * The code is based on the code found at http://www.nayuki.io
     *
     * @see
     * <a href="http://introcs.cs.princeton.edu/java/97data/FFT.java.html">
     * http://introcs.cs.princeton.edu/java/97data/FFT.java.html</a>
     *
     * @throws hw03.Utility.DFTException - if the array's length is not a power of 2
     * @param inputComplexes - a complex array on which to perform
     * transformation
     * @return - the DFT of the input
     * @author kqn001
     */
    public static Complex[] fastDFT(Complex[] inputComplexes) throws DFTException {
        if (!ProcessingUtility.isPowerOf2(inputComplexes.length)) {
            throw new DFTException("Not a power of 2!");
        } else {
            int n = inputComplexes.length;
            if (n <= 32) {
                return slowDFT(inputComplexes);
            } else {
                Complex[] evenArray = new Complex[n / 2];
                Complex[] oddArray = new Complex[n / 2];
                for (int i = 0; i < n / 2; i++) {
                    evenArray[i] = inputComplexes[i * 2];

                    oddArray[i] = inputComplexes[i * 2 + 1];
                }
                evenArray = fastDFT(evenArray);
                oddArray = fastDFT(oddArray);
                Complex[] newDFTArray = new Complex[n];
                for (int i = 0; i < n / 2; i++) {
                    double twiddleFactor = -2 * Math.PI * i / n;
                    Complex twComplex = new Complex(Math.cos(twiddleFactor),
                                                    Math.sin(twiddleFactor));
                    newDFTArray[i] = evenArray[i].add(
                            oddArray[i].multiply(twComplex));
                    newDFTArray[i + n / 2] = evenArray[i].subtract(
                            oddArray[i].multiply(twComplex));

                }
                return newDFTArray;

            }
        }
    }

    /**
     * perform slow DFT transformation on an complex array using multithreading
     * Utilize the multiFFT class
     *
     * The code is based on the code found at http://www.nayuki.io
     *
     * @see
     * <a href="http://introcs.cs.princeton.edu/java/97data/FFT.java.html">
     * http://introcs.cs.princeton.edu/java/97data/FFT.java.html</a>
     * @param dftArray- a complex array on which to perform transformation
     * @throws hw03.Utility.DFTException - if the array length is not a power of 2
     * @return - the DFT of the input
     * @author kqn001
     */
    public static Complex[] FFTMulti(Complex[] dftArray) throws DFTException {
        if (!ProcessingUtility.isPowerOf2(dftArray.length)) {
            throw new DFTException("Not a power of 2!");
        }
        multiFFT fftMultiThreading = new multiFFT(dftArray,
                                                  dftArray.length / 4);
        fftMultiThreading.fastDFTMulti();
        Complex[] result = fftMultiThreading.getArray();
        return result;

    }

    /**
     * calculate the peaks of the input frequency array.
     *
     * @param freqArray - a double array of magnitude of each bin of frequency
     * @return - a peak array
     * @author kqn001
     */
    private void calculatePeaks() {
        List<Peak> peaks = new ArrayList<>();
        // get the first half of the magnitude array
        for (int i = 0; i < freqArray.length / 2; i++) {
            peaks.add(new Peak(freqArray[i], i * sampleRate / freqArray.length));
        }
        //sort the peak array
        Collections.sort(peaks);
        peakArray = new Peak[peaks.size()];
        for (int i = 0; i < peaks.size(); i++) {
            peakArray[i] = peaks.get(i);
        }
    }

    /**
     * output to the screen n peaks of the program
     *
     * @param n
     * @author kqn001
     */
    public void showPeaks(int n) {
        for (int i = 0; i < n; i++) {
            System.out.format("Peak #%d : Frequency %.2f  |  Magnitude: %.2f \n",
                              i,
                              peakArray[i].getFrequency(),
                              peakArray[i].getMagnitude());
        }
        System.out.println();
    }

    /**
     * Get n peak from the current DFT data and return as an 2DArray.
     *
     * The 2DArray has two row. The first row is the frequency, the second row
     * is the magnitude.
     *
     * @param n - the number of peak to get
     * @return - 2DArray of frequency and magnitude.
     * @author khainguyen
     */
    public double[][] getPeaks(int n) {
        double[][] peakList = new double[2][n];
        for (int i = 0; i < n; i++) {
            peakList[0][i] = peakArray[i].getFrequency();
            peakList[1][i] = peakArray[i].getMagnitude();
        }
        return peakList;
    }

    /**
     * getter method for the input array
     *
     * @return - the input complex array
     * @author kqn001
     */
    public Complex[] getInSignal() {
        return inComplexes;
    }

    /**
     * getter method for the DFT frequency array
     *
     * @return - a double array, the DFT of input
     * @author kqn001
     */
    public double[] getOutFreq() {
        return freqArray;
    }
}
