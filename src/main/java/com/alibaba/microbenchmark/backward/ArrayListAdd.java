package com.alibaba.microbenchmark.backward;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weipeng2k 2018年01月02日 下午14:26:51
 */
public class ArrayListAdd {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void m_c() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(i);
        }
        boolean i = list.size() > 0;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void t_c() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        boolean i = list.size() > 0;
    }
}
