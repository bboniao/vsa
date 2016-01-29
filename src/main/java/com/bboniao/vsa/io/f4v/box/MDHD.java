
package com.bboniao.vsa.io.f4v.box;

import com.bboniao.vsa.io.f4v.Payload;
import com.bboniao.vsa.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDHD implements Payload {

    private static final Logger logger = LoggerFactory.getLogger(MDHD.class);

    private byte version;
    private byte[] flags;
    private long creationTime;
    private long modificationTime;
    private int timeScale;
    private long duration;
    private byte pad;
    private byte language;
    private short reserved;

    public MDHD(ByteBuf in) {
        read(in);
    }

    public int getTimeScale() {
        return timeScale;
    }

    public long getDuration() {
        return duration;
    }


    public void read(ByteBuf in) {
        version = in.readByte();
        logger.debug("version: {}", Utils.toHex(version));
        flags = new byte[3];
        in.readBytes(flags);
        if (version == 0x00) {
            creationTime = in.readInt();
            modificationTime = in.readInt();
        } else {
            creationTime = in.readLong();
            modificationTime = in.readLong();
        }
        timeScale = in.readInt();
        if (version == 0x00) {
            duration = in.readInt();
        } else {
            duration = in.readLong();
        }
        logger.debug("creationTime {} modificationTime {} timeScale {} duration {}",
                new Object[]{creationTime, modificationTime, timeScale, duration});
        pad = in.readByte();
        language = in.readByte(); // TODO convert to ISO codes ?
        reserved = in.readShort();
    }


    public ByteBuf write() {
        ByteBuf out = Unpooled.buffer();
        out.writeByte(version);
        out.writeBytes(new byte[3]); // flags
        if (version == 0x00) {
            out.writeInt((int) creationTime);
            out.writeInt((int) modificationTime);
        } else {
            out.writeLong(creationTime);
            out.writeLong(modificationTime);
        }
        out.writeInt(timeScale);
        if (version == 0x00) {
            out.writeInt((int) duration);
        } else {
            out.writeLong(duration);
        }
        out.writeByte(pad);
        out.writeByte(language);
        out.writeShort(reserved);
        return out;
    }
    
}
