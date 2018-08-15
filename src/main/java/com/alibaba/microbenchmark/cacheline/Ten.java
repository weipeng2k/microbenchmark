package com.alibaba.microbenchmark.cacheline;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author weipeng2k 2018年08月15日 下午19:46:28
 */
public class Ten {
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void m_10() {
        for (int i = 0; i < 1000000; i++) {
            Datas.run(Datas.ints_10, Datas.ints_10, 10);
        }
    }

}
