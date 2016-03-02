/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 27, 2015
 * Time: 11:20:37 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: DFTException
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

/**
 * the exception class for DFT transform.
 *
 * @author kqn001
 */
public class DFTException extends Exception {
    /**
     * constructor for DFT exception
     *
     * @param message - the message to be thrown
     */
    public DFTException(String message) {
        super(message);
    }
}
