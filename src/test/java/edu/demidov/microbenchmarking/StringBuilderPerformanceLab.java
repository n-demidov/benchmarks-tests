package edu.demidov.microbenchmarking;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class StringBuilderPerformanceLab {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBuilderPerformanceLab.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    private int count;
    private String[] randomStrings;
    private StringBuilder reusedStringBuilder = new StringBuilder();

    @Setup
    public void setup() {
        randomStrings = new String[]{
                RandomStringUtils.randomAlphanumeric(32),
                RandomStringUtils.randomAlphanumeric(32),
                RandomStringUtils.randomAlphanumeric(32),
                RandomStringUtils.randomAlphanumeric(32)
        };
    }

    private String randomString() {
        int i = (count++) & 3;
        return randomStrings[i];
    }

    @Benchmark
    public String plusCall() {
        return randomString() + randomString() + randomString() + randomString();
    }

    @Benchmark
    public String plusVar() {
        String a = randomString();
        String b = randomString();
        String c = randomString();
        String d = randomString();
        return a + b + c + d;
    }

    @Benchmark
    public String stringBuilder() {
        return new StringBuilder()
                .append(randomString())
                .append(randomString())
                .append(randomString())
                .append(randomString())
                .toString();
    }

    @Benchmark
    public String stringBuilderReused() {
        reusedStringBuilder.setLength(0);
        return reusedStringBuilder
                .append(randomString())
                .append(randomString())
                .append(randomString())
                .append(randomString())
                .toString();
    }
}

/*

Benchmark                                        Mode  Cnt    Score   Error  Units
StringBuilderPerformanceLab.plusCall             avgt   10  153.771 ± 1.912  ns/op
StringBuilderPerformanceLab.plusVar              avgt   10   48.448 ± 7.650  ns/op
StringBuilderPerformanceLab.stringBuilder        avgt   10  156.577 ± 4.016  ns/op
StringBuilderPerformanceLab.stringBuilderReused  avgt   10   66.265 ± 1.435  ns/op

 */
