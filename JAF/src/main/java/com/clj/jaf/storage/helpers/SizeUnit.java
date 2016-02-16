package com.clj.jaf.storage.helpers;

public enum SizeUnit {
    B(1L),
    KB(1024L),
    MB(1048576L),
    GB(1073741824L),
    TB(0L);

    private long inBytes;
    private static final int BYTES = 1024;

    private SizeUnit(long bytes) {
        this.inBytes = bytes;
    }

    public long inBytes() {
        return this.inBytes;
    }
}
