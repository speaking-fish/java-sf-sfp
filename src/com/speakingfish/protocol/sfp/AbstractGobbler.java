/**
 * 
 */
package com.speakingfish.protocol.sfp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author borka
 *
 */
public abstract class AbstractGobbler implements Runnable {

    final InputStream _input;
    
    /**
     * 
     */
    public AbstractGobbler(InputStream input) {
        super();
        _input = input;
    }

    public void run() {
        try {
            while (true) {
                final byte[] frame = Helper.readFrame(_input);
                if(null == frame) {
                    break;
                }
                incomingFrame(frame);
            }
        } catch (EOFException e) {
        } catch (@SuppressWarnings("hiding") IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void incomingFrame(byte[] frame);

}
