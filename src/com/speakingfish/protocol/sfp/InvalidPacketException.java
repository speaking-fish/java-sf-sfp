/**
 * 
 */
package com.speakingfish.protocol.sfp;

/**
 * @author borka
 *
 */
public class InvalidPacketException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2798025146799779268L;

    /**
     * 
     */
    public InvalidPacketException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InvalidPacketException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InvalidPacketException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidPacketException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidPacketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
