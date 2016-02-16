package com.clj.jaf.storage.helpers;

import java.io.Serializable;

public class ImmutablePair<T, S> implements Serializable {
    private static final long serialVersionUID = 40L;
    public final T element1;
    public final S element2;

    public ImmutablePair() {
        this.element1 = null;
        this.element2 = null;
    }

    public ImmutablePair(T element1, S element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public boolean equals(Object object) {
        if(!(object instanceof ImmutablePair)) {
            return false;
        } else {
            Object object1 = ((ImmutablePair)object).element1;
            Object object2 = ((ImmutablePair)object).element2;
            return this.element1.equals(object1) && this.element2.equals(object2);
        }
    }

    public int hashCode() {
        return this.element1.hashCode() << 16 + this.element2.hashCode();
    }
}
