
package com.bboniao.vsa.util;

import java.util.Arrays;

/**
 * a little bit of code reuse, would have been cleaner if enum types
 * could extend some other class - we implement an interface instead
 * and have to construct a static instance in each enum type we use
 */
public class ValueToEnum<T extends Enum<T> & ValueToEnum.IntValue> {

    public static interface IntValue {
        int intValue();
    }

    private final Enum[] lookupArray;
    private final int maxIndex;

    public ValueToEnum(final T[] enumValues) {
        final int[] lookupIndexes = new int[enumValues.length];
        for(int i = 0; i < enumValues.length; i++) {
            lookupIndexes[i] = enumValues[i].intValue();
        }
        Arrays.sort(lookupIndexes);
        maxIndex = lookupIndexes[lookupIndexes.length - 1];        
        lookupArray = new Enum[maxIndex + 1]; // use 1 based index
        for (final T t : enumValues) {
            lookupArray[t.intValue()] = t;
        }        
    }

    public T valueToEnum(final int i) {
        final T t;
        try {
            t = (T) lookupArray[i];
        } catch(Exception e) { // index out of bounds
            throw new RuntimeException(getErrorLogMessage(i) + ", " + e);
        }
        if (t == null) {
            throw new RuntimeException(getErrorLogMessage(i) + ", no match found in lookup");
        }
        return t;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    private String getErrorLogMessage(final int i) {
        return "bad value / byte: " + i + " (hex: " + Utils.toHex((byte) i) + ")";
    }

}
