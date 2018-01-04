# 使用JMH做Java微基准测试

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在使用Java编程过程中，我们对于一些代码调用的细节有多种编写方式，但是不确定它们性能时，往往采用重复多次计数的方式来解决。但是随着JVM不断的进化，随着代码执行次数的增加，JVM会不断的进行编译优化，使得重复多少次才能够得到一个稳定的测试结果变得让人疑惑，这时候有经验的同学就会在测试执行前先循环上万次并注释为预热。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;没错！这样做确实可以获得一个偏向正确的测试结果，但是我们试想如果每到需要斟酌性能的时候，都要根据场景写一段预热的逻辑吗？当预热完成后，需要多少次迭代来进行正式内容的测量呢？每次测试结果的输出报告是不是都需要用`System.out`来输出呢？

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其实这些工作都可以交给 **JMH** (the Java Microbenchmark Harness) ，它被作为Java9的一部分来发布，但是我们完全不需要等待Java9，而可以方便的使用它来简化我们测试，它能够照看好JVM的预热、代码优化，让你的测试过程变得更加简单。

## 开始

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先在项目中新增依赖，`jmh-core`以及`jmh-generator-annprocess`的依赖可以在maven仓库中找寻最新版本。

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.19</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.19</version>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建一个`Helloworld`类，里面只有一个空方法`m()`，标注了`@Benchmark`的注解，声明这个方法为一个微基准测试方法，**JMH** 会在编译期生成基准测试的代码，并运行它。

```java
public class Helloworld {

    @Benchmark
    public void m() {

    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;接着添加一个main入口，由它来启动测试。

```java
public class HelloworldRunner {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("Helloworld")
                .exclude("Pref")
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(3)
                .build();

        new Runner(opt).run();
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;简单介绍一下这个`HelloworldRunner`，它是一个入口的同时还完成了 **JMH** 测试的配置工作。默认场景下，**JMH** 会找寻标注了`@Benchmark`类型的方法，可能会跑一些你所不需要的测试，这样就需要通过`include`和`exclude`两个方法来完成包含以及排除的语义。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`warmupIterations(10)`的意思是预热做10轮，`measurementIterations(10)`代表正式计量测试做10轮，而每次都是先执行完预热再执行正式计量，内容都是调用标注了`@Benchmark`的代码。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`forks(3)`指的是做3轮测试，因为一次测试无法有效的代表结果，所以通过3轮测试较为全面的测试，而每一轮都是先预热，再正式计量。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们运行`HelloworldRunner`，经过一段时间，测试结果如下：

```sh
Result "com.alibaba.microbenchmark.test.Helloworld.m":
  3084697483.521 ±(99.9%) 27096926.646 ops/s [Average]
  (min, avg, max) = (2951123277.601, 3084697483.521, 3121456015.904), stdev = 40557407.239
  CI (99.9%): [3057600556.875, 3111794410.166] (assumes normal distribution)


# Run complete. Total time: 00:01:02

Benchmark      Mode  Cnt           Score          Error  Units
Helloworld.m  thrpt   30  3084697483.521 ± 27096926.646  ops/s
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到分数是30亿次，但是这30亿指的是什么呢？仔细观察 **Mode** 一项中类型是`thrpt`，其实就是`Throughput`吞吐量，代表着每秒完成的次数。

## 测试类型

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;前面提到测试的类型是吞吐量，也就是一秒钟调用完成的次数，但是如果想知道做一次需要多少时间该怎么办？

> 其实 1 / 吞吐量 就是这个值

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**JMH** 提供了以下几种类型进行支持：

|类型|描述|
|----|----|
|Throughput|每段时间执行的次数，一般是秒|
|AverageTime|平均时间，每次操作的平均耗时|
|SampleTime|在测试中，随机进行采样执行的时间|
|SingleShotTime|在每次执行中计算耗时|
|All|顾名思义，所有模式，这个在内部测试中常用|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用这些模式也非常简单，只需要增加`@BenchmarkMode`注解即可，例如：

```java
@Benchmark
@BenchmarkMode({Mode.Throughput, Mode.SingleShotTime})
public void m() {

}
```

## 配置策略

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**JMH** 支持通过`@Fork`注解完成配置，例如：

```java
@Benchmark
@Fork(value = 1, warmups = 2)
@BenchmarkMode(Mode.Throughput)
public void init() {

}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以上注解指`init()`方法测试时，预热2轮，正式计量1轮，但是如果测试方法比较多，还是建议通过`Options`进行配置，具体可以参考`HelloworldRunner`。

## 数据准备

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;开始测试前，往往有数据准备的需求，比如：初始化一个变量或者一批数据，可以通过构造函数进行初始化，不过 **JMH** 提供了一些手段帮助我们完成这个工作。

```java
@State(Scope.Benchmark)
public class Helloworld {
    private long i;

    @Setup
    public void init() {
        i = System.currentTimeMillis();
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上面的例子中，初始化了成员变量`i`，**JMH** 会保证在构造`Helloworld`后调用`init()`方法。`@State(Scope.Benchmark)`是修饰成员变量作用域是整个测试，能够被整个测试过程可见。

## 实现策略

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**JMH** 是通过什么手段来完成测试的呢？预热我们可以通过循环去做，大概1-2万次左右的调用已经足够让代码优化，测试过程我们就通过`System.currentTimeMillis()`看一下耗时不就行了？

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这个问题先放一下，我们看一下 **JMH** 是如何驱动你的测试的。当我们运行 **JMH** 基准测试时，它会生成一些Java代码，通常会通过继承你的测试来完成，比如你的测试类型是`A`，测试方法`m()`，它会自动生成`A_jmhType_B1`、`A_jmhType_B2`、`A_jmhType_B3`和`A_jmhType`以及最终的测试执行逻辑`A_m_jmhTest`。生成代码的一般在`classes`目录下，看一下生成的类型信息：

```java
public class Helloworld_jmhType_B1 extends com.alibaba.microbenchmark.test.Helloworld {
    boolean p000, p001, p002, p003, p004, p005, p006, p007, p008, p009, p010, p011, p012, p013, p014, p015;
    boolean p240, p241, p242, p243, p244, p245, p246, p247, p248, p249, p250, p251, p252, p253, p254, p255;
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到类型中定义了256个boolean，这理论是在做cache line padding，可以猜测 **JMH** 是想避免CPU缓存对测试过程带来的影响。

```java
public BenchmarkTaskResult m_Throughput(InfraControl control, ThreadParams threadParams) throws Throwable {
  Helloworld_jmhType l_helloworld0_G = _jmh_tryInit_f_helloworld0_G(control);
  control.preSetup();
  control.announceWarmupReady();
  while (control.warmupShouldWait) {
      l_helloworld0_G.m();
      res.allOps++;
    }

  m_thrpt_jmhStub(control, res, benchmarkParams, iterationParams, threadParams, blackhole, notifyControl, startRndMask, l_helloworld0_G);
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在最终的测试逻辑代码`Helloworld_m_jmhTest`中对吞吐量的测试，首先获取了针对`Helloworld`生成的子类，然后调用Setup，接下来预热，最后进行正式计量。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过介绍 **JMH** 的测试过程，就可以回答之前的问题，因为 **JMH** 全面的考虑的性能测试中环境的影响，所以测试更加的客观。

## 例子：循环的微基准测试

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`for`循环大家平时经常使用，但是看到过一个优化策略，就是倒序遍历，比如：`for (int i = length; i > 0; i--)`优于`for (int i = 0; i < length; i++)`，有些不解。咨询了温少，温少给出的答案是`i > 0`优于`i < length`，因此倒序有优势，那么我们将这个场景做一下基准测试。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先是正向循环，次数是1百万次迭代。

```java
public class CountPerf {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void count() {
        for (int i = 0; i < 1_000_000; i++) {

        }
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;接着是逆向循环，次数也是1百万次。

```java
public class CountPerf {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void count() {
        for (int i = 1_000_000; i > 0; i--) {

        }
    }
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最后是一个测试的入口，我们采用3组，每组预热10轮，正式计量10轮，测试类型是吞吐量。

```java
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
```

测试结果如下，有数据表现可以看到逆序在宏观上是优于正序的。

```sh
Result "com.alibaba.microbenchmark.forward.CountPerf.count":
  3017436523.994 ±(99.9%) 74706077.393 ops/s [Average]
  (min, avg, max) = (2586477493.002, 3017436523.994, 3090537220.013), stdev = 111816548.191
  CI (99.9%): [2942730446.601, 3092142601.387] (assumes normal distribution)


# Run complete. Total time: 00:02:05

Benchmark                        Mode  Cnt           Score          Error  Units
c.a.m.backward.CountPerf.count  thrpt   30  3070589161.097 ± 30858669.885  ops/s
c.a.m.forward.CountPerf.count   thrpt   30  3017436523.994 ± 74706077.393  ops/s
```

## 优化的Hessian2微基准测试

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HSF默认使用Hessian2进行序列化传输，而Hessian2在传输时，每次会捎带上类型元信息，这些在实际场景下对资源会产生一定的开销。HSF2.2会使用优化的Hessian2进行序列化，与Hessian2的不同在于，它会基于长连接级别缓存元信息，每次只会发送数据内容，由于只发送数据内容，所以资源开销会更少，我们对Hessian2和优化后的Hssian2做了基准测试，结果如下：

```sh
Benchmark                                   Mode  Cnt       Score       Error  Units
c.a.m.h.hessian.DeserialPerf.deserial      thrpt   60  147255.638 ±  1057.106  ops/s
c.a.m.h.hessian.SerialPerf.serial          thrpt   60  146336.439 ±  1199.087  ops/s
c.a.m.h.optihessian.DeserialPerf.deserial  thrpt   60  327482.489 ±  3366.174  ops/s
c.a.m.h.optihessian.SerialPerf.serial      thrpt   60  176988.488 ±  1233.302  ops/s
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;优化后的hessian在序列化吞吐量上领先hessian2，达到每秒17W，反序列化出乎意料，超过hessian2两倍，达到32W每秒。

## 参考

* [Microbenchmarking with Java](http://www.baeldung.com/java-microbenchmark-harness)
* [JMH Samples](http://hg.openjdk.java.net/code-tools/jmh/file/fbe1b55eadf8/jmh-samples/src/main/java/org/openjdk/jmh/samples)
