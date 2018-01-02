package com.alibaba.microbenchmark.test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * @author weipeng2k 2018年01月02日 下午15:12:19
 */
public class Helloworld {

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.SingleShotTime})
    public void m() {

    }
}
