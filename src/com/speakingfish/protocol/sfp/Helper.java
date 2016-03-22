package com.speakingfish.protocol.sfp;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.speakingfish.common.exception.wrapped.java.io.WrappedEOFException;

import static com.speakingfish.pipe.Helper.*;

public class Helper {
    
    protected Helper() {}

    public static final byte SFP_FRAME_BOUND                    = (byte) 0xF0; // bnd
    public static final byte SFP_ESCAPE_START                   = (byte) 0xF1; // esc
    public static final byte SFP_ESCAPE_COMMAND_MIN             =           0; // esc 00
    public static final byte SFP_ESCAPE_COMMAND_ECHO_FRAME_BOUND=        0x00; // esc 00
    public static final byte SFP_ESCAPE_COMMAND_ECHO_ESCAPE     =        0x01; // esc 01
    public static final byte SFP_ESCAPE_COMMAND_MAX             =        0x0F; // esc 0F

    public static void writeFrameBound(OutputStream dest) {
        internalWrite(dest, SFP_FRAME_BOUND);
    }
    

    public static void writeData(OutputStream dest, int offset, int size, byte[] buffer) {
        if(size <= 0) {
            return;
        }

        int startChunk = offset;
        int chunkSize  = 0     ;
        int totalLeft  = size  ;
        while(0 < totalLeft) {
            /*
            ++chunkSize;
            */
            if((SFP_FRAME_BOUND  == buffer[offset])
            || (SFP_ESCAPE_START == buffer[offset])
            ) {
                internalWrite(dest, startChunk, chunkSize, buffer);
                internalWrite(dest, SFP_ESCAPE_START);
                if(SFP_FRAME_BOUND  == buffer[offset]) {
                    internalWrite(dest, SFP_ESCAPE_COMMAND_ECHO_FRAME_BOUND);
                } else {
                    internalWrite(dest, SFP_ESCAPE_COMMAND_ECHO_ESCAPE);
                }
                /*
                totalLeft -= chunkSize;
                */
                startChunk = offset + 1;
                chunkSize  = 0;
            } else {
                ++chunkSize;
            }
            --totalLeft;
            ++offset;
        }
        if(0 < chunkSize) {
            internalWrite(dest, startChunk, chunkSize, buffer);
        }
    }
    

    public static void writeFrame(OutputStream dest, int offset, int size, byte[] buffer) {
        writeFrameBound(dest);
        writeData      (dest, offset, size, buffer);
        writeFrameBound(dest);
    }
  

    public static byte[] readRawFrame(InputStream src) throws WrappedEOFException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream(1024); 

        while(true) {
            final byte test = internalRead(src);
            if(SFP_FRAME_BOUND == test) {
                return result.toByteArray();
            }
            result.write(test);
        }
    }
    
    public static byte[] readFromRawFrame(final byte[] result) throws EOFException {
        int srcAnchor  = 0;
        int destAnchor = 0;
        int index      = 0;
        while(index < result.length) {
            if(SFP_ESCAPE_START == result[index]) {
                System.arraycopy(result, srcAnchor, result, destAnchor, index - srcAnchor);
                destAnchor+= index - srcAnchor;
                ++index;
                if(index < result.length) {
                    throw new InvalidPacketException("Invalid packet: Terminated escape");
                }
                if(SFP_ESCAPE_COMMAND_ECHO_FRAME_BOUND == result[index]) {
                    result[destAnchor] = SFP_FRAME_BOUND;
                    destAnchor+= 1;
                } else if(SFP_ESCAPE_COMMAND_ECHO_ESCAPE == result[index]) {
                    result[destAnchor] = SFP_ESCAPE_START;
                    destAnchor+= 1;
                } else {
                    throw new InvalidPacketException("Invalid packet: Illegal escape echo code");
                }
              srcAnchor = index + 1;
            } else if(SFP_ESCAPE_START == result[index]) {
                throw new InvalidPacketException("Invalid packet: Internal error: not filtered BND.");
            }
            ++index;
        }
        if(srcAnchor < index) {
            System.arraycopy(result, srcAnchor, result, destAnchor, index - srcAnchor);
            destAnchor+= index - srcAnchor;
        }
        
        if(destAnchor < result.length) {
            return Arrays.copyOf(result, destAnchor);
        } else {
            return result;
        }
    }

    public static byte[] readFrame(InputStream src) throws EOFException {
        return readFromRawFrame(readRawFrame(src));
    }
    
}
