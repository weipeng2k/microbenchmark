package com.alibaba.microbenchmark.test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author weipeng2k 2018年01月02日 下午15:12:19
 */
@State(Scope.Benchmark)
public class Helloworld {

    private long i;

    @Setup
    public void init() {
        i = System.currentTimeMillis();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.SingleShotTime})
    public void m() {

    }
}
