
package com.bboniao.vsa.util;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class ChannelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ChannelUtils.class);

    public static void exceptionCaught(final Throwable e, ChannelHandlerContext ctx) {
        if (e instanceof ClosedChannelException) {
            logger.info("exception: {}", e);
        } else if(e.getCause() instanceof IOException) {
            logger.info("exception: {}", e.getCause().getMessage());
        } else {
            logger.warn("exception: {}", e.getCause());
        }
        if(ctx.channel().isOpen()) {
            ctx.channel().close();
        }
    }

}
