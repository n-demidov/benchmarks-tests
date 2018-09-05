package edu.demidov.microbenchmarking;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
public class ComputePerformanceLab {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ComputePerformanceLab.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Param({"30"})
    private int numEntries;

    @Param({"100", "10000"})
    private int numPuts;

    @Param({"hashMap", "object2ObjectMap"})
    private String mapType;

    private String[] keys;
    private Map<String, CountHolder> map;


    @Setup
    public void setup() {
        keys = new String[numEntries];
        map = mapType.equals("hashMap") ? new HashMap<>() : new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < numEntries; i++) {
            keys[i] = RandomStringUtils.randomAlphanumeric(16);
        }
    }

    @Benchmark
    public int put() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < numPuts; i++) {
            String key = keys[random.nextInt(numEntries)];
            CountHolder holder = map.get(key);
            if (holder == null) {
                map.put(key, new CountHolder(System.currentTimeMillis()));
            } else {
                holder.count++;
                holder.timestamp = System.currentTimeMillis();
            }
        }
        return map.size();
    }

    @Benchmark
    public int putCompute() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < numPuts; i++) {
            String key = keys[random.nextInt(numEntries)];
            map.compute(key, (k, v) -> {
                if (v == null) {
                    return new CountHolder(System.currentTimeMillis());
                } else {
                    v.count++;
                    v.timestamp = System.currentTimeMillis();
                    return v;
                }
            });
        }
        return map.size();
    }


    private static class CountHolder {
        private long count = 1;
        private long timestamp;

        public CountHolder(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}

/*

Benchmark                                (mapType)  (numEntries)  (numPuts)  Mode  Cnt       Score       Error  Units
ComputePerformanceLab.put                  hashMap            30        100  avgt   10    3760.874 ±    20.785  ns/op
ComputePerformanceLab.put                  hashMap            30      10000  avgt   10  400807.570 ± 26813.583  ns/op
ComputePerformanceLab.put         object2ObjectMap            30        100  avgt   10    4130.416 ±   242.722  ns/op
ComputePerformanceLab.put         object2ObjectMap            30      10000  avgt   10  402132.903 ±  3711.605  ns/op
ComputePerformanceLab.putCompute           hashMap            30        100  avgt   10    3841.238 ±   135.177  ns/op
ComputePerformanceLab.putCompute           hashMap            30      10000  avgt   10  353745.156 ±  6827.742  ns/op
ComputePerformanceLab.putCompute  object2ObjectMap            30        100  avgt   10    4379.584 ±   159.552  ns/op
ComputePerformanceLab.putCompute  object2ObjectMap            30      10000  avgt   10  500795.503 ± 32155.056  ns/op

 */
