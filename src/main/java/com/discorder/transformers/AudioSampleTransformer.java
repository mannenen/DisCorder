package com.discorder.transformers;

public class AudioSampleTransformer implements Transformer<byte[], byte[]> {
    public void transform() {
        byte[] sample = this.source.retrieve();

        byte[] transSample = this.doTransform(sample);

        this.destination.submit(transSample);
    }

    public byte[] transform(byte[] packet) {
        return packet;
    }
}
