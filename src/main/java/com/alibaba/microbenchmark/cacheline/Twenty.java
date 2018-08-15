package com.alibaba.microbenchmark.cacheline;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * @author weipeng2k 2018年08月15日 下午19:46:28
 */
public class Twenty {
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void m_20() {
        for (int i = 0; i < 1000000; i++) {
            Datas.run(Datas.ints_20, Datas.ints_20, 20);
        }
    }

}
