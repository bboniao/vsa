
package com.bboniao.vsa.rtmp;


import com.bboniao.vsa.rtmp.message.Metadata;

public interface RtmpReader {

    Metadata getMetadata();

    RtmpMessage[] getStartMessages();

    void setAggregateDuration(int targetDuration);

    long getTimePosition();

    long seek(long timePosition);

    void close();

    boolean hasNext();

    RtmpMessage next();

}
