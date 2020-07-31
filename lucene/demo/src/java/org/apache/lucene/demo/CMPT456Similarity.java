/*
 * Author: Ken Ngai
 * Similarity using tf and idf
 */
package org.apache.lucene.demo;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import java.lang.Math;

public class CMPT456Similarity extends ClassicSimilarity {
    public CMPT456Similarity(){
        super();
    }

    @Override
    public float tf(float freq) {
        // sqrt same as power base 0.5
        return (float) (Math.sqrt(1 + freq));
    }

    @Override
    public float idf(long docFreq, long docCount) {
    
        return (float) (Math.log((docCount + 2) / (double)(docFreq + 2)) + 1.0);
    }

    
}