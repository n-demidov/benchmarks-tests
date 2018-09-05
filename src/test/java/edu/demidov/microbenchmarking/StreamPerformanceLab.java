package edu.demidov.microbenchmarking;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class StreamPerformanceLab {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StreamPerformanceLab.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Param({"100", "10000"})
    private int numEntries;

    private Map<String, Long> hashMap;
    private Object2LongMap<String> object2LongMap;


    @Setup
    public void setup() {
        hashMap = new HashMap<>();
        object2LongMap = new Object2LongLinkedOpenHashMap<>();
        for (int i = 0; i < numEntries; i++) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            long value = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
            hashMap.put(key, value);
            object2LongMap.put(key, value);
        }
    }

    @Benchmark
    public long maxHashMap() {
        long result = -1;
        for (Long value : hashMap.values()) {
            result = Math.max(result, value);
        }
        return result;
    }

    @Benchmark
    public long maxHashMapLambda() {
        return hashMap.values().stream().mapToLong(Long::longValue).max().getAsLong();
    }

    @Benchmark
    public long maxObject2LongMap() {
        long result = -1;
        LongIterator it = object2LongMap.values().iterator();
        while (it.hasNext()) {
            result = Math.max(result, it.nextLong());
        }
        return result;
    }

    @Benchmark
    public long maxObject2LongMapLambda() {
        return object2LongMap.values().stream().mapToLong(Long::longValue).max().getAsLong();
    }
}

/*

with OpenHashMap:
Benchmark                                     (numEntries)  Mode  Cnt       Score       Error  Units
StreamPerformanceLab.maxHashMap                        100  avgt   10     313.013 ±     0.909  ns/op
StreamPerformanceLab.maxHashMap                      10000  avgt   10  137941.828 ± 14865.117  ns/op
StreamPerformanceLab.maxHashMapLambda                  100  avgt   10     702.221 ±     5.776  ns/op
StreamPerformanceLab.maxHashMapLambda                10000  avgt   10  149757.865 ± 25274.729  ns/op
StreamPerformanceLab.maxObject2LongMap                 100  avgt   10     200.767 ±     1.774  ns/op
StreamPerformanceLab.maxObject2LongMap               10000  avgt   10   62949.109 ±   635.051  ns/op
StreamPerformanceLab.maxObject2LongMapLambda           100  avgt   10    1076.389 ±    16.014  ns/op
StreamPerformanceLab.maxObject2LongMapLambda         10000  avgt   10  161100.550 ± 17930.336  ns/op

with LinkedOpenHashMap:
Benchmark                                     (numEntries)  Mode  Cnt       Score       Error  Units
StreamPerformanceLab.maxHashMap                        100  avgt   10     326.360 ±     2.456  ns/op
StreamPerformanceLab.maxHashMap                      10000  avgt   10  159440.629 ± 15565.893  ns/op
StreamPerformanceLab.maxHashMapLambda                  100  avgt   10     747.908 ±     8.564  ns/op
StreamPerformanceLab.maxHashMapLambda                10000  avgt   10  151276.420 ± 27701.801  ns/op
StreamPerformanceLab.maxObject2LongMap                 100  avgt   10     168.016 ±     1.003  ns/op
StreamPerformanceLab.maxObject2LongMap               10000  avgt   10   51300.563 ±   302.745  ns/op
StreamPerformanceLab.maxObject2LongMapLambda           100  avgt   10    1016.000 ±     8.283  ns/op
StreamPerformanceLab.maxObject2LongMapLambda         10000  avgt   10  117632.009 ±  1761.954  ns/op

 */
