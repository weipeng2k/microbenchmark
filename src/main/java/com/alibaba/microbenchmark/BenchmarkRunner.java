package com.alibaba.microbenchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author weipeng2k 2018年01月02日 下午14:25:08
 */
public class BenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("Perf")
                .exclude("Helloworld")
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(3)
                .build();

        new Runner(opt).run();
    }
}
