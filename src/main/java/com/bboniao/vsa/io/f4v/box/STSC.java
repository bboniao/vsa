package com.bboniao.vsa.io.f4v.box;
import com.bboniao.vsa.io.f4v.Payload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class STSC implements Payload {

    private static final Logger logger = LoggerFactory.getLogger(STSC.class);

    public static class STSCRecord {

        private int firstChunk;
        private int samplesPerChunk;
        private int sampleDescIndex;

        public int getFirstChunk() {
            return firstChunk;
        }

        public int getSamplesPerChunk() {
            return samplesPerChunk;
        }

        public int getSampleDescIndex() {
            return sampleDescIndex;
        }

    }

    private List<STSCRecord> records;

    public STSC(ByteBuf in) {
        read(in);
    }

    public List<STSCRecord> getRecords() {
        return records;
    }

    public void setRecords(List<STSCRecord> records) {
        this.records = records;
    }

    public void read(ByteBuf in) {
        in.readInt(); // UI8 version + UI24 flags
        final int count = in.readInt();
        logger.debug("no of sample chunk records: {}", count);
        records = new ArrayList<STSCRecord>(count);
        for (int i = 0; i < count; i++) {
            final STSCRecord record = new STSCRecord();
            record.firstChunk = in.readInt();
            record.samplesPerChunk = in.readInt();
            record.sampleDescIndex = in.readInt();
//            logger.debug("#{} firstChunk: {} samplesPerChunk: {} sampleDescIndex: {}",
//                    new Object[]{i, record.firstChunk, record.samplesPerChunk, record.sampleDescIndex});
            records.add(record);
        }
    }

    public ByteBuf write() {
        ByteBuf out = Unpooled.buffer();
        out.writeInt(0); // UI8 version + UI24 flags
        out.writeInt(records.size());
        for (STSCRecord record : records) {
            out.writeInt(record.firstChunk);
            out.writeInt(record.samplesPerChunk);
            out.writeInt(record.sampleDescIndex);
        }
        return out;
    }
    
}
