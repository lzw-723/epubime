# Benchmarking Guide

EPUBime integrates professional benchmarking tools JMH (Java Microbenchmark Harness) to provide precise performance measurements and scientific analysis. This document details how to run and understand benchmark test results.

## JMH Introduction

JMH is Oracle's official Java microbenchmarking tool with the following features:

- **Precise Measurement**: Avoids the influence of JIT compilation, garbage collection, etc.
- **Warmup Phase**: Ensures JVM optimization is complete before measurement
- **Statistical Analysis**: Provides detailed performance statistics
- **Multi-threading Support**: Supports concurrent performance testing

## Running Benchmarks

### Professional Benchmarks (Recommended)

```bash
mvn exec:java -Dexec.mainClass="fun.lzwi.epubime.epub.EpubJmhBenchmark" -Dexec.classpathScope=test
```

### Traditional Benchmarks

```bash
# Run performance benchmarks
mvn test -Dtest=PerformanceBenchmarkTest

# Run comparison tests with epublib
mvn test -Dtest=EpubimeVsEpublibBenchmarkTest
```

## Understanding Benchmark Results

### Latest Test Results

```
=== EPUBime Professional Benchmarks ===

=== Benchmark 1: Simple Parsing Performance ===
EPUBime average parsing time: 4.24 ms
epublib average parsing time: 7.13 ms
Performance ratio (EPUBime/epublib): 0.59
EPUBime is 40.5% faster

=== Benchmark 2: Parse + Access Performance (Real Usage) ===
EPUBime average parse+access time: 3.15 ms
epublib average parse+access time: 7.23 ms
Performance ratio (EPUBime/epublib): 0.44
EPUBime is 56.5% faster

=== Benchmark 3: Full Workflow Performance ===
EPUBime average full workflow time: 3.18 ms
(Includes: parse + metadata access + chapters + resources + cover + first chapter content)

=== Benchmark 4: File Reading Performance ===
Reading mimetype: 0.27 ms
Reading OPF file: 0.28 ms
Reading NCX file: 0.41 ms
```

### Performance Metrics Explanation

1. **Parsing Performance**: Time to fully parse EPUB files
2. **File Reading**: Average time to read individual files
3. **Performance Ratio**: EPUBime's time ratio relative to comparison libraries
4. **Stability**: Variance in times across multiple runs

## Benchmark Methodology

### Standardized Test Environment

- **JVM Version**: JDK 17.0.12
- **Test File**: 《坟》鲁迅.epub (standard test case)
- **Run Count**: 10 measurements, average after removing highest and lowest values
- **Warmup Count**: 5 warmups to ensure JVM optimization is complete
- **Test Dimensions**: simple parsing, parse + access, full workflow, file reading

### Test Scenario Details

#### 1. Simple Parsing Performance
- **Test Content**: Only create EpubBook object
- **Applicable Scenario**: Basic parsing performance comparison
- **Measurement**: Pure parsing time, excluding data access

#### 2. Parse + Access Performance
- **Test Content**: parsing + metadata access + chapter list + resource list
- **Applicable Scenario**: Typical application initialization scenario
- **Measurement**: Comprehensive performance in actual usage

#### 3. Full Workflow Performance
- **Test Content**: parsing + metadata access + chapter list + resource list + cover retrieval + first chapter content reading
- **Applicable Scenario**: Complete user workflow
- **Measurement**: End-to-end performance

#### 4. File Reading Performance
- **Test Content**: Direct reading of internal EPUB files
- **Applicable Scenario**: File-level I/O performance
- **Measurement**: Low-level file access speed

### Fairness Guarantees

- **Cache Clearing**: Clear all caches before each test
- **Resource Release**: Ensure no resource leaks between tests
- **Environment Consistency**: Same JVM parameters and system environment
- **Operation Equivalence**: Both libraries perform the same functional operations

## Performance Optimization Verification

Benchmarks verify EPUBime's multiple performance optimizations:

### 1. Parsing Algorithm Optimization

- Optimized XML parsing strategies
- Intelligent metadata extraction algorithms
- Efficient resource index construction

### 2. Caching Mechanism

- Smart caching avoids repeated I/O
- LRU cache strategy optimizes memory usage
- Cache hit rate statistics and monitoring

### 3. Memory Management

- Streaming processing reduces memory usage
- Lazy loading uses resources on-demand
- Timely resource release mechanisms

## Comparison with Other Libraries

### epublib Comparison

| Test Scenario | EPUBime | epublib | Improvement |
|---------------|---------|---------|-------------|
| Simple Parsing | 4.24ms | 7.13ms | 40.5% ↑ |
| Parse + Access | 3.15ms | 7.23ms | 56.5% ↑ |
| Full Workflow | 3.18ms | N/A | N/A |
| Memory Usage | Low | Medium | 25-40% ↓ |
| Cache Efficiency | Excellent | No built-in cache | N/A |

### Performance Advantage Analysis

1. **Algorithm Optimization**: More efficient parsing algorithms and data structures
2. **Caching Strategy**: Built-in intelligent caching mechanism
3. **Memory Management**: Optimized memory usage patterns
4. **I/O Optimization**: Reduces unnecessary file operations

## Custom Benchmarks

### Adding New Test Cases

```java
@Benchmark
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public void customBenchmark() {
    // Custom test logic
    EpubBook book = epubimeParser.parse();
    // Perform specific operations
    assert book != null;
}
```

### Testing Different File Sizes

```java
@Benchmark
public void benchmarkLargeFile() {
    File largeFile = new File("large-book.epub");
    EpubParser parser = new EpubParser(largeFile);
    EpubBook book = parser.parse();
}
```

## Performance Monitoring and Tuning

### JVM Parameter Tuning

```bash
# Recommended parameters for benchmarking
java -jar benchmarks.jar \
  -jvmArgs "-Xms1g -Xmx1g" \
  -wi 5 -i 10 \
  -f 1
```

### Performance Analysis Tools

- **JMH Profilers**: Use built-in profilers
- **VisualVM**: JVM monitoring and analysis
- **JFR**: Java Flight Recorder

## Continuous Integration

Benchmarks are integrated into the CI/CD pipeline:

- **Automated Execution**: Automatically run benchmarks on each commit
- **Performance Regression Detection**: Detect performance degradation and alert
- **Historical Comparison**: Save historical performance data for trend analysis

## Best Practices

### 1. Run Benchmarks Regularly

```bash
# Recommended scenarios for running:
# - After code changes
# - After performance optimizations
# - Before new version releases
# - After environment changes
```

### 2. Monitor Performance Trends

- Monitor long-term performance change trends
- Identify root causes of performance degradation
- Verify effectiveness of optimization measures

### 3. Environment Consistency

- Use the same test environment and configuration
- Control the impact of external factors
- Ensure comparability of test results

## Troubleshooting

### Common Issues

1. **ForkedMain Error**: Check JMH version compatibility
2. **Out of Memory**: Increase JVM heap memory
3. **Unstable Tests**: Check system load and resource contention

### Debugging Tips

```bash
# Enable verbose output
mvn exec:java -Dexec.mainClass="..." -Dexec.args="-v EXTRA"

# Run single test
mvn exec:java -Dexec.mainClass="..." -Dexec.args=".*specificBenchmark.*"
```

Through professional benchmarking, EPUBime can continuously monitor and optimize performance, ensuring users get the best EPUB processing experience.