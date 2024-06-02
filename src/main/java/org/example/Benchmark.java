package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
public class Benchmark {

    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String text5;

    private Bzip2Compression bzip2Compressor;
    private GzipCompression gzipCompressor;
    private ZstdCompression zstdCompressor;
    private XzCompression xzCompressor;
    private PpmdCompression ppmdCompressor;

    @Setup
    public void setup() throws IOException {
        text1 = new String(Files.readAllBytes(Paths.get("test1.txt")));
        text2 = new String(Files.readAllBytes(Paths.get("test2.txt")));
        text3 = new String(Files.readAllBytes(Paths.get("test3.txt")));
        text4 = new String(Files.readAllBytes(Paths.get("test4.txt")));
        text5 = new String(Files.readAllBytes(Paths.get("test5.txt")));

        bzip2Compressor = new Bzip2Compression();
        gzipCompressor = new GzipCompression();
        zstdCompressor = new ZstdCompression();
        xzCompressor = new XzCompression();
        ppmdCompressor = new PpmdCompression();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkBzip2Compression() throws IOException {
        return bzip2Compressor.compress(text1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkBzip2CompressionRatio() throws IOException {
        byte[] compressedData = bzip2Compressor.compress(text1);
        return ((double) text1.getBytes().length) / compressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkGzipCompression() throws IOException {
        return gzipCompressor.compress(text2);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkGzipCompressionRatio() throws IOException {
        byte[] compressedData = gzipCompressor.compress(text2);
        return ((double) text2.getBytes().length) / compressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkZstdCompression() throws IOException {
        return zstdCompressor.compress(text3);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkZstdCompressionRatio() throws IOException {
        byte[] compressedData = zstdCompressor.compress(text3);
        return ((double) text3.getBytes().length) / compressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkXzCompression() throws IOException {
        return xzCompressor.compress(text4);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkXzCompressionRatio() throws IOException {
        byte[] compressedData = xzCompressor.compress(text4);
        return ((double) text4.getBytes().length) / compressedData.length;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public byte[] benchmarkPpmdCompression() {
        return ppmdCompressor.compress(text5);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkPpmdCompressionRatio() {
        byte[] compressedData = ppmdCompressor.compress(text5);
        return ((double) text5.getBytes().length) / compressedData.length;
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}