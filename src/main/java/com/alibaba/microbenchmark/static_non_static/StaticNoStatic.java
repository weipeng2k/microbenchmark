package com.alibaba.microbenchmark.static_non_static;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author weipeng2k 2018年01月02日 下午14:26:02
 */
@State(Scope.Benchmark)
public class StaticNoStatic {

    private static Foo foo = new Foo();

    private Foo foo1;

    @Setup
    public void init() {
        foo1 = new Foo();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void static_method() {
        for (int i = 0; i < 1_000_000; i++) {
            foo.getInt();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void no_static_method() {
        for (int i = 0; i < 1_000_000; i++) {
            foo1.getInt();
        }
    }
}
