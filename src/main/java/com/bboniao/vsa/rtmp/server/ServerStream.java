
package com.bboniao.vsa.rtmp.server;

import com.bboniao.vsa.rtmp.RtmpMessage;
import com.bboniao.vsa.util.Utils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerStream {

    public static enum PublishType {

        LIVE,
        APPEND,
        RECORD;

        public String asString() {
            return this.name().toLowerCase();
        }

        public static PublishType parse(final String raw) {
            return PublishType.valueOf(raw.toUpperCase());
        }

    }
    
    private final String name;
    private final PublishType publishType;
    private final ChannelGroup subscribers;
    private final List<RtmpMessage> configMessages;
    private Channel publisher;

    private static final Logger logger = LoggerFactory.getLogger(ServerStream.class);

    public ServerStream(final String rawName, final String typeString) {        
        this.name = Utils.trimSlashes(rawName).toLowerCase();
        if(typeString != null) {
            this.publishType = PublishType.parse(typeString); // TODO record, append
            subscribers = new DefaultChannelGroup(name, GlobalEventExecutor.INSTANCE);
            configMessages = new ArrayList<RtmpMessage>();
        } else {
            this.publishType = null;
            subscribers = null;
            configMessages = null;
        }
        logger.info("Created ServerStream {}", this);
    }

    public boolean isLive() {
        return publishType != null && publishType == PublishType.LIVE;
    }

    public PublishType getPublishType() {
        return publishType;
    }

    public ChannelGroup getSubscribers() {
        return subscribers;
    }

    public String getName() {
        return name;
    }


    public List<RtmpMessage> getConfigMessages() {
        return configMessages;
    }

    public void addConfigMessage(final RtmpMessage message) {
        configMessages.add(message);
    }

    public void setPublisher(Channel publisher) {
        this.publisher = publisher;
        configMessages.clear();
    }

    public Channel getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();        
        sb.append("[name: '").append(name);
        sb.append("' type: ").append(publishType);
        sb.append(" publisher: ").append(publisher);
        sb.append(" subscribers: ").append(subscribers);
        sb.append(" config: ").append(configMessages);
        sb.append(']');
        return sb.toString();
    }

}
