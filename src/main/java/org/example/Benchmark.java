package org.example;

import org.openjdk.jmh.annotations.*;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) // Изменено на микросекунды
@State(Scope.Thread)
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS) // Настройка прогрева
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS) // Настройка измерений
@Fork(1)
public class Benchmark {

    private Bzip2Compression bzip2Compressor;
    private GzipCompression gzipCompressor;
    private ZstdCompression zstdCompressor;
    private XzCompression xzCompressor;
    private PpmdCompression ppmdCompressor;

    @Param({"100", "1000", "10000"})
    private int textSize;

    private String randomText;
    private byte[] bzip2CompressedData;
    private byte[] gzipCompressedData;
    private byte[] zstdCompressedData;
    private byte[] xzCompressedData;
    private byte[] ppmdCompressedData;
    private Metrics metrics;

    @Setup
    public void setup() {
        bzip2Compressor = new Bzip2Compression();
        gzipCompressor = new GzipCompression();
        zstdCompressor = new ZstdCompression();
        xzCompressor = new XzCompression();
        ppmdCompressor = new PpmdCompression();
        metrics = new Metrics();
        randomText = generateRandomText(textSize);
    }

    @TearDown
    public void tearDown() {
        metrics.printMaxMetrics();
        System.gc();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkBzip2Compression(Metrics counters) throws IOException {
        measureResourcesBefore();
        bzip2CompressedData = bzip2Compressor.compress(randomText);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkBzip2Decompression(Metrics counters) throws IOException {
        if (bzip2CompressedData == null) {
            bzip2CompressedData = bzip2Compressor.compress(randomText);
        }
        measureResourcesBefore();
        bzip2Compressor.decompress(bzip2CompressedData);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkBzip2CompressionRatio() throws IOException {
        if (bzip2CompressedData == null) {
            bzip2CompressedData = bzip2Compressor.compress(randomText);
        }
        return ((double) randomText.getBytes(StandardCharsets.UTF_8).length) / bzip2CompressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkGzipCompression(Metrics counters) throws IOException {
        measureResourcesBefore();
        gzipCompressedData = gzipCompressor.compress(randomText);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkGzipDecompression(Metrics counters) throws IOException {
        if (gzipCompressedData == null) {
            gzipCompressedData = gzipCompressor.compress(randomText);
        }
        measureResourcesBefore();
        gzipCompressor.decompress(gzipCompressedData);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkGzipCompressionRatio() throws IOException {
        if (gzipCompressedData == null) {
            gzipCompressedData = gzipCompressor.compress(randomText);
        }
        return ((double) randomText.getBytes(StandardCharsets.UTF_8).length) / gzipCompressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkZstdCompression(Metrics counters) throws IOException {
        measureResourcesBefore();
        zstdCompressedData = zstdCompressor.compress(randomText);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkZstdDecompression(Metrics counters) throws IOException {
        if (zstdCompressedData == null) {
            zstdCompressedData = zstdCompressor.compress(randomText);
        }
        measureResourcesBefore();
        zstdCompressor.decompress(zstdCompressedData);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkZstdCompressionRatio() throws IOException {
        if (zstdCompressedData == null) {
            zstdCompressedData = zstdCompressor.compress(randomText);
        }
        return ((double) randomText.getBytes(StandardCharsets.UTF_8).length) / zstdCompressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkXzCompression(Metrics counters) throws IOException {
        measureResourcesBefore();
        xzCompressedData = xzCompressor.compress(randomText);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkXzDecompression(Metrics counters) throws IOException {
        if (xzCompressedData == null) {
            xzCompressedData = xzCompressor.compress(randomText);
        }
        measureResourcesBefore();
        xzCompressor.decompress(xzCompressedData);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkXzCompressionRatio() throws IOException {
        if (xzCompressedData == null) {
            xzCompressedData = xzCompressor.compress(randomText);
        }
        return ((double) randomText.getBytes(StandardCharsets.UTF_8).length) / xzCompressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkPpmdCompression(Metrics counters) {
        measureResourcesBefore();
        ppmdCompressedData = ppmdCompressor.compress(randomText);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkPpmdDecompression(Metrics counters) {
        if (ppmdCompressedData == null) {
            ppmdCompressedData = ppmdCompressor.compress(randomText);
        }
        measureResourcesBefore();
        ppmdCompressor.decompress(ppmdCompressedData);
        measureResourcesAfter(counters);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkPpmdCompressionRatio() {
        if (ppmdCompressedData == null) {
            ppmdCompressedData = ppmdCompressor.compress(randomText);
        }
        return ((double) randomText.getBytes(StandardCharsets.UTF_8).length) / ppmdCompressedData.length;
    }

    private String generateRandomText(int size) {
        Random random = new Random();
        char[] text = new char[size];
        for (int i = 0; i < size; i++) {
            text[i] = (char) (random.nextInt(26) + 'a');
        }
        return new String(text);
    }

    // Методы для измерения ресурсов
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private double getProcessCpuLoad() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getProcessCpuLoad();
    }

    private void measureResourcesBefore() {
        // Принудительное выполнение сборки мусора перед измерением
        System.gc();
        // Небольшая задержка для стабилизации системы
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        metrics.usedMemoryBefore = getUsedMemory();
        metrics.cpuLoadBefore = getProcessCpuLoad();
    }

    private void measureResourcesAfter(Metrics counters) {
        // Принудительное выполнение сборки мусора после измерения
        System.gc();
        // Небольшая задержка для стабилизации системы
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        metrics.usedMemoryAfter = getUsedMemory();
        metrics.cpuLoadAfter = getProcessCpuLoad();

        // Update max values
        counters.maxMemoryUsedInBytes = Math.max(metrics.usedMemoryAfter - metrics.usedMemoryBefore, counters.maxMemoryUsedInBytes);
        counters.maxCpuLoadPercentage = Math.max((metrics.cpuLoadAfter - metrics.cpuLoadBefore) * 100, counters.maxCpuLoadPercentage);
        counters.iterations++;
    }

    @AuxCounters(AuxCounters.Type.EVENTS)
    @State(Scope.Thread)
    public static class Metrics {
        public long usedMemoryBefore;
        public long usedMemoryAfter;
        public double cpuLoadBefore;
        public double cpuLoadAfter;

        public long maxMemoryUsedInBytes;
        public double maxCpuLoadPercentage;
        public int iterations;

        public void printMaxMetrics() {
            System.out.printf("Max Memory used: %d bytes, Max CPU load: %.2f%%%n",
                    maxMemoryUsedInBytes, maxCpuLoadPercentage);
        }
    }

    // Метод для запуска JMH бенчмарков
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}