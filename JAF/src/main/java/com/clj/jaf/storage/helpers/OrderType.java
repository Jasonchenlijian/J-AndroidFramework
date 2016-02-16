package com.clj.jaf.storage.helpers;

import java.io.File;
import java.util.Comparator;

public enum OrderType {
    NAME,
    DATE,
    SIZE;

    private OrderType() {
    }

    public Comparator<File> getComparator() {
        switch (this.ordinal()) {
            case 0:
                return new Comparator<File>() {
                    public int compare(File lhs, File rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                };
            case 1:
                return new Comparator<File>() {
                    public int compare(File lhs, File rhs) {
                        return (int) (rhs.lastModified() - lhs.lastModified());
                    }
                };
            case 2:
                return new Comparator<File>() {
                    public int compare(File lhs, File rhs) {
                        return (int) (lhs.length() - rhs.length());
                    }
                };
            default:
                return null;
        }
    }
}
