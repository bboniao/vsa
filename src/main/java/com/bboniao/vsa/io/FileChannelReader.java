
package com.bboniao.vsa.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelReader implements BufferReader {

    private static final Logger logger = LoggerFactory.getLogger(FileChannelReader.class);

    private final String absolutePath;
    private final FileChannel in;
    private final long fileSize;

    public FileChannelReader(final String path) {
        this(new File(path));
    }

    public FileChannelReader(final File file) {
        absolutePath = file.getAbsolutePath();
        try {
            in = new FileInputStream(file).getChannel();
            fileSize = in.size();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("opened file: {}", absolutePath);
    }


    public long size() {
        return fileSize;
    }


    public long position() {
        try {
            return in.position();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void position(final long newPosition) {
        try {
            in.position(newPosition);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ByteBuf read(final int size) {
        return Unpooled.wrappedBuffer(readBytes(size));
    }


    public int readInt() {
        return read(4).readInt();
    }


    public long readUnsignedInt() {
        return read(4).readUnsignedInt();
    }


    public byte[] readBytes(final int size) {
        final byte[] bytes = new byte[size];
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        try {
            in.read(bb);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }


    public void close() {
        try {
            in.close();
        } catch(Exception e) {
            logger.warn("error closing file {}: {}", absolutePath, e.getMessage());
        }
        logger.info("closed file: {}", absolutePath);
    }

}
