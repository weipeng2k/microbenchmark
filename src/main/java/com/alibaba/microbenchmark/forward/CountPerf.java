package com.alibaba.microbenchmark.forward;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * @author weipeng2k 2018年01月02日 下午14:26:02
 */
public class CountPerf {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void count() {
        for (int i = 0; i < 1_000_000; i++) {

        }
    }
}
