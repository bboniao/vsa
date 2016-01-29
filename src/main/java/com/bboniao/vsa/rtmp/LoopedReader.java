
package com.bboniao.vsa.rtmp;


import com.bboniao.vsa.rtmp.message.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LoopedReader implements RtmpReader {

    private static final Logger logger = LoggerFactory.getLogger(LoopedReader.class);

    private final int loopCount;
    private final RtmpReader reader;
    private long timePosition;
    private double duration = -1;
    private int loopsCompleted = 0;
    private final Metadata metadata;
    private RtmpMessage[] startMessages;

    public LoopedReader(final RtmpReader reader, final int loopCount) {
        this.reader = reader;
        this.loopCount = loopCount;        
        this.metadata = reader.getMetadata();
        double originalDuration = metadata.getDuration();
        if(originalDuration > 0) {
            double durationSeconds = originalDuration * loopCount;
            metadata.setDuration(durationSeconds);            
        } else {
            metadata.setDuration(-1);
        }        
        logger.info("looped reader init: count {}", loopCount);
    }


    public Metadata getMetadata() {
        return metadata;
    }


    public RtmpMessage[] getStartMessages() {
        if(startMessages == null) {
            final List<RtmpMessage> list = new ArrayList<RtmpMessage>();
            list.add(metadata);
            for(final RtmpMessage message : reader.getStartMessages()) {
                if(!message.getHeader().isMetadata()) {
                    list.add(message);
                }
            }
            startMessages = list.toArray(new RtmpMessage[list.size()]);
        }
        return startMessages;
    }


    public void setAggregateDuration(int targetDuration) {
        reader.setAggregateDuration(targetDuration);
    }


    public long getTimePosition() {
        return timePosition;
    }


    public long seek(long timePosition) {
        if(duration < 0 || timePosition < duration) {
            return reader.seek(timePosition);
        }                               
        loopsCompleted = (int) Math.floor(timePosition / duration);
        return reader.seek((long) (timePosition % duration));
    }


    public void close() {
        reader.close();
    }


    public boolean hasNext() {
        if(reader.hasNext()) {
            return true;
        }
        if(loopsCompleted == 0 && duration == -1) {
            duration = timePosition;
        }
        loopsCompleted++;
        if(loopsCompleted < loopCount) {
            reader.seek(0);
            logger.info("re-wound media after loop #{}", loopsCompleted);
            return true;
        }
        return false;
    }


    public RtmpMessage next() {
        final RtmpMessage message = reader.next();
        if(loopsCompleted == 0) {
            timePosition = message.getHeader().getTime();
            return message;
        }        
        timePosition = (long) duration * loopsCompleted + message.getHeader().getTime();        
        message.getHeader().setTime((int) timePosition); // TODO find and cleanup all these (int) casts
        return message;
    }

}
