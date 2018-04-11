package com.discorder.transformers;

public interface Transformer<R, P> {
    public R transform(P parameter);
}
