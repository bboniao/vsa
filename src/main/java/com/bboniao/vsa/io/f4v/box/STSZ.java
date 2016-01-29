
package com.bboniao.vsa.io.f4v.box;

import com.bboniao.vsa.io.f4v.Payload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class STSZ implements Payload {

    private static final Logger logger = LoggerFactory.getLogger(STSZ.class);
    private List<Integer> sampleSizes;
    private int constantSize;

    public STSZ(ByteBuf in) {
        read(in);
    }

    public List<Integer> getSampleSizes() {
        return sampleSizes;
    }

    public void setConstantSize(int constantSize) {
        this.constantSize = constantSize;
    }

    public void setSampleSizes(List<Integer> sampleSizes) {
        this.sampleSizes = sampleSizes;
    }


    public void read(ByteBuf in) {
        in.readInt(); // UI8 version + UI24 flags
        constantSize = in.readInt();
        logger.debug("sample size constant size: {}", constantSize);
        final int count = in.readInt();
        logger.debug("no of sample size records: {}", count);
        sampleSizes = new ArrayList<Integer>(count);
        for (int i = 0; i < count; i++) {
            final Integer sampleSize = in.readInt();
            // logger.debug("#{} sampleSize: {}", new Object[]{i, sampleSize});
            sampleSizes.add(sampleSize);
        }
    }

    public ByteBuf write() {
        ByteBuf out = Unpooled.buffer();
        out.writeInt(0); // UI8 version + UI24 flags
        out.writeInt(constantSize);
        out.writeInt(sampleSizes.size());
        for (Integer sampleSize : sampleSizes) {
            out.writeInt(sampleSize);
        }
        return out;
    }
    
}
