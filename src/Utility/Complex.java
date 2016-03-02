/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 15, 2015
 * Time: 7:55:39 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: Complex
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

/**
 * The helper complex class to perform DFT transformation
 *
 * @author kal037
 */
public class Complex {

    //The real part of the complex number
    private double re;

    //The imaginary part of the complex number
    private double im;

    //Constructor
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    //Constructor for converting a double to a complex number
    public Complex(double re) {
        this.re = re;
        this.im = 0;
    }

    /**
     * Getter. Returns the real part of the complex number
     *
     * @return - the real part of the number
     * @author kal037
     */
    public double getRe() {
        return re;
    }

    /**
     * Getter. Returns the imaginary part of the complex number
     *
     * @return - the imaginary part of the number
     * @author kal037
     */
    public double getIm() {
        return im;
    }

    /**
     * Prints out the complex number in "a + bi" format
     *
     * @return - the representation of the number
     * @author kal037
     */
    @Override
    public String toString() {
        if (im > 0) {
            return String.format("%.2f + %.2fi", this.re, this.im);
        } else if (im < 0) {
            return String.format("%.2f - %.2fi", this.re, Math.abs(this.im));
        } else {
            return String.format("%.2f", this.re);
        }
    }

    /**
     * Performs complex addition
     *
     * @param number - the number to perform addition with
     * @return result the resulted complex number
     * @author kal037
     */
    public Complex add(Complex number) {
        Complex result = new Complex(this.getRe() + number.getRe(),
                                     this.getIm() + number.getIm());
        return result;
    }

    /**
     * Performs complex subtraction
     *
     * @param number - the number to perform subtraction with
     * @return result the resulted complex number
     * @author kal037
     */
    public Complex subtract(Complex number) {
        Complex result = new Complex(this.getRe() - number.getRe(),
                                     this.getIm() - number.getIm());
        return result;
    }

    /**
     * Performs complex multiplication
     *
     * @param number - the number to perform multiplication with
     * @return result the resulted complex number
     * @author kal037
     */
    public Complex multiply(Complex number) {
        Complex result = new Complex(
                this.getRe() * number.getRe() - this.getIm() * number.getIm(),
                this.getRe() * number.getIm() + this.getIm() * number.getRe());
        return result;
    }

    /**
     * Performs complex division
     *
     * @param number - the number to perform division with
     * @return result the resulted complex number
     * @author kal037
     */
    public Complex divide(Complex number) {
        Complex result = new Complex(
                (this.getRe() * number.getRe() + this.getIm() * number.getIm()) / (Math.pow(
                                                                                   number.getRe(),
                                                                                   2) + Math.pow(
                                                                                   number.getIm(),
                                                                                   2)),
                (this.getIm() * number.getRe() - this.getRe() * number.getIm()) / (Math.pow(
                                                                                   number.getRe(),
                                                                                   2) + Math.pow(
                                                                                   number.getIm(),
                                                                                   2)));
        return result;
    }

    /**
     * calculate the magnitude of the complex number
     *
     * @return - the magnitude of the complex number
     * @author kal037
     */
    public double magnitude() {
        return Math.sqrt(Math.pow(this.re, 2) + Math.pow(this.im, 2));
    }

}
