
package com.bboniao.vsa.io.f4v;

import com.bboniao.vsa.io.BufferReader;
import com.bboniao.vsa.io.FileChannelReader;
import com.bboniao.vsa.io.flv.FlvAtom;
import com.bboniao.vsa.rtmp.RtmpHeader;
import com.bboniao.vsa.rtmp.RtmpMessage;
import com.bboniao.vsa.rtmp.RtmpReader;
import com.bboniao.vsa.rtmp.message.Aggregate;
import com.bboniao.vsa.rtmp.message.Audio;
import com.bboniao.vsa.rtmp.message.Metadata;
import com.bboniao.vsa.rtmp.message.Video;
import com.bboniao.vsa.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class F4vReader implements RtmpReader {

    private static final Logger logger = LoggerFactory.getLogger(F4vReader.class);

    private static final byte[] MP4A_BEGIN_PREFIX = Utils.fromHex("af00");
    private static final byte[] MP4A_PREFIX = Utils.fromHex("af01");
    private static final byte[] AVC1_BEGIN_PREFIX = Utils.fromHex("1700000000");
    private static final byte[] AVC1_PREFIX_KEYFRAME = Utils.fromHex("1701");
    private static final byte[] AVC1_PREFIX = Utils.fromHex("2701");

    private byte[] AVC1_BEGIN;
    private byte[] MP4A_BEGIN;

    private final BufferReader in;
    private final List<Sample> samples;
    private final Metadata metadata;

    private int cursor;
    private int aggregateDuration;

    public F4vReader(final String path) {
        in = new FileChannelReader(path);
        final MovieInfo movie = new MovieInfo(in);
        in.position(0);
        AVC1_BEGIN = movie.getVideoDecoderConfig();
        MP4A_BEGIN = movie.getAudioDecoderConfig();
        logger.debug("video decoder config inited: {}", Utils.toHex(AVC1_BEGIN));
        metadata = Metadata.onMetaData(movie);
        samples = movie.getSamples();
        cursor = 0;
    }


    public Metadata getMetadata() {
        return metadata;
    }


    public RtmpMessage[] getStartMessages() {
        return new RtmpMessage[] {
            getMetadata(),
            new Video(AVC1_BEGIN_PREFIX, AVC1_BEGIN),
            new Audio(MP4A_BEGIN_PREFIX, MP4A_BEGIN)
        };
    }


    public void setAggregateDuration(int targetDuration) {
        this.aggregateDuration = targetDuration;
    }


    public long getTimePosition() {
        final int index;
        if(cursor == samples.size()) {
            index = cursor - 1;
        } else {
            index = cursor;
        }
        return samples.get(index).getTime();
    }


    public long seek(long timePosition) {
        cursor = 0;
        while(cursor < samples.size()) {
            final Sample sample = samples.get(cursor);
            if(sample.getTime() >= timePosition) {
                break;
            }
            cursor++;
        }
        while(!samples.get(cursor).isSyncSample() && cursor > 0) {
            cursor--;
        }
        return samples.get(cursor).getTime();
    }


    public boolean hasNext() {
        return cursor < samples.size();
    }

    private static final int AGGREGATE_SIZE_LIMIT = 65536;


    public RtmpMessage next() {
        if(aggregateDuration <= 0) {
            return getMessage(samples.get(cursor++));
        }
        final ByteBuf out = Unpooled.buffer();
        int startSampleTime = -1;
        while(cursor < samples.size()) {
            final Sample sample = samples.get(cursor++);
            if(startSampleTime == -1) {
                startSampleTime = sample.getTime();
            }
            final RtmpMessage message = getMessage(sample);
            final RtmpHeader header = message.getHeader();
            final FlvAtom flvAtom = new FlvAtom(header.getMessageType(), header.getTime(), message.encode());
            final ByteBuf temp = flvAtom.write();
            if(out.readableBytes() + temp.readableBytes() > AGGREGATE_SIZE_LIMIT) {
                cursor--;
                break;
            }
            out.writeBytes(temp);
            if(sample.getTime() - startSampleTime > aggregateDuration) {
                break;
            }
        }
        return new Aggregate(startSampleTime, out);
    }

    private RtmpMessage getMessage(final Sample sample) {
        in.position(sample.getFileOffset());
        final byte[] sampleBytes = in.readBytes(sample.getSize());        
        final byte[] prefix;        
        if(sample.isVideo()) {
            if(sample.isSyncSample()) {
                prefix = AVC1_PREFIX_KEYFRAME;
            } else {
                prefix = AVC1_PREFIX;
            }
            // TODO move prefix logic to Audio / Video
            return new Video(sample.getTime(), prefix, sample.getCompositionTimeOffset(), sampleBytes);
        } else {
            prefix = MP4A_PREFIX;
            return new Audio(sample.getTime(), prefix, sampleBytes);
        }
    }

    public void close() {
        in.close();
    }   

    public static void main(String[] args) {
        F4vReader reader = new F4vReader("test2.5.mp4");
        while(reader.hasNext()) {
            logger.debug("read: {}", reader.next());
        }
    }

}
