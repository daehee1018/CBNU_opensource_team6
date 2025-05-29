package com.example.opensource_team6.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LatentUtils {

    /** 32-byte → float[8]  (Little-Endian) */
    public static float[] toFloatArray(byte[] blob) {
        if (blob == null || blob.length != 32) return new float[8];
        float[] out = new float[8];
        ByteBuffer.wrap(blob)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asFloatBuffer()
                .get(out);
        return out;
    }

    /** 코사인 유사도 */
    public static float cosine(float[] a, float[] b) {
        float dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na  += a[i] * a[i];
            nb  += b[i] * b[i];
        }
        return (float) (dot / (Math.sqrt(na) * Math.sqrt(nb) + 1e-6));
    }
}
