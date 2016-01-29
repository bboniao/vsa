
package com.bboniao.vsa.rtmp.message;


import com.bboniao.vsa.amf.Amf0Object;
import com.bboniao.vsa.rtmp.RtmpHeader;
import com.bboniao.vsa.rtmp.RtmpMessage;
import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractMessage implements RtmpMessage {
    
    protected final RtmpHeader header;

    public AbstractMessage() {
        header = new RtmpHeader(getMessageType());
    }

    public AbstractMessage(RtmpHeader header, ByteBuf in) {
        this.header = header;
        decode(in);
    }

    public RtmpHeader getHeader() {
        return header;
    }

    abstract MessageType getMessageType();

    @Override
    public String toString() {
        return header.toString() + ' ';
    }

    //==========================================================================

    public static Amf0Object object(Amf0Object object, Pair ... pairs) {
        if(pairs != null) {
            for(Pair pair : pairs) {
                object.put(pair.name, pair.value);
            }
        }
        return object;
    }

    public static Amf0Object object(Pair ... pairs) {
        return object(new Amf0Object(), pairs);
    }

    public static Map<String, Object> map(Map<String, Object> map, Pair ... pairs) {
        if(pairs != null) {
            for(Pair pair : pairs) {
                map.put(pair.name, pair.value);
            }
        }
        return map;
    }

    public static Map<String, Object> map(Pair ... pairs) {
        return map(new LinkedHashMap<String, Object>(), pairs);
    }

    public static class Pair {
        String name;
        Object value;
    }

    public static Pair pair(String name, Object value) {
        Pair pair = new Pair();
        pair.name = name;
        pair.value = value;
        return pair;
    }



}
