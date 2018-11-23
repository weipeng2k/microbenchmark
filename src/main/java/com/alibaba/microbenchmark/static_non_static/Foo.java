package com.alibaba.microbenchmark.static_non_static;

/**
 * @author weipeng2k 2018年11月23日 上午10:23:42
 */
public class Foo {

    public Foo() {
        System.out.println("Init");
    }

    static int getStaticInt() {
        return 0;
    }

    int getInt() {
        return 0;
    }
}
