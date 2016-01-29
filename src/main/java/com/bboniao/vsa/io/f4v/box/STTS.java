
package com.bboniao.vsa.io.f4v.box;

import com.bboniao.vsa.io.f4v.Payload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class STTS implements Payload {

    private static final Logger logger = LoggerFactory.getLogger(STTS.class);

    public static class STTSRecord {

        private int sampleCount;
        private int sampleDuration;

        public int getSampleCount() {
            return sampleCount;
        }

        public int getSampleDuration() {
            return sampleDuration;
        }

    }

    public STTS(ByteBuf in) {
        read(in);
    }

    private List<STTSRecord> records;

    public List<STTSRecord> getRecords() {
        return records;
    }

    public void setRecords(List<STTSRecord> records) {
        this.records = records;
    }

    public void read(ByteBuf in) {
        in.readInt(); // UI8 version + UI24 flags
        final int count = in.readInt();
        logger.debug("no of time to sample records: {}", count);
        records = new ArrayList<STTSRecord>(count);
        for (int i = 0; i < count; i++) {
            final STTSRecord record = new STTSRecord();
            record.sampleCount = in.readInt();
            record.sampleDuration = in.readInt();
            logger.debug("#{} sampleCount: {} sampleDuration: {}",
                    new Object[]{i, record.sampleCount, record.sampleDuration});
            records.add(record);
        }
    }

    public ByteBuf write() {
        ByteBuf out = Unpooled.buffer();
        out.writeInt(0); // UI8 version + UI24 flags
        out.writeInt(records.size());
        for (STTSRecord record : records) {
            out.writeInt(record.sampleCount);
            out.writeInt(record.sampleDuration);
        }
        return out;
    }
    
}
