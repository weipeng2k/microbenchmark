package com.alibaba.microbenchmark.cacheline;

import java.util.stream.IntStream;

/**
 * @author weipeng2k 2018年08月15日 下午19:47:13
 */
public class Datas {

    final static int[] ints_20;
    final static int[] ints_16;
    final static int[] ints_10;


    static {
        ints_20 = IntStream.range(1, 21).toArray();
        ints_16 = IntStream.range(1, 17).toArray();
        ints_10 = IntStream.range(1, 11).toArray();
    }


    public static int run(int[] row, int[] column, int num) {
        int sum = 0;
        for(int i = 0; i < num; i++ ) {
            sum += row[i] * column[i];
        }
        return sum;
    }
}
