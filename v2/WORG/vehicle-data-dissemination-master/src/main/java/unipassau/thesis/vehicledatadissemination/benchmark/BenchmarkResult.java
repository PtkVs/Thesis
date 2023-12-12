package unipassau.thesis.vehicledatadissemination.benchmark;

public class BenchmarkResult implements CSVSerializable {
    private int id;
    private long nanoseconds;

    public BenchmarkResult(int id, long nanoseconds) {
        this.id = id;
        this.nanoseconds = nanoseconds;
    }

    @Override
    public String toCSV() {
        return id + ";" + nanoseconds;
    }
}

/*
1. Fields:-The class has two private fields:
                a)id: An integer representing the identifier of the benchmark result.
                b)nanoseconds: A long integer representing the time taken in nanoseconds for the benchmark.

2. Constructor:
-The class has a constructor that takes an id and a nanoseconds value as parameters. This constructor is likely used to initialize
  a BenchmarkResult object with the details of a specific benchmark run.

3. toCSV Method:
-The class implements the CSVSerializable interface, requiring the implementation of the toCSV method.
-The toCSV method converts the BenchmarkResult object into a CSV-formatted string.
-It concatenates the id and nanoseconds fields with a semicolon separator and returns the resulting string.

**Summary**
The BenchmarkResult class is a representation of the result of an individual benchmark run. It contains information about the benchmark,
such as an identifier (id) and the time taken in nanoseconds (nanoseconds). The toCSV method allows the object to be serialized into
a CSV format, which is useful for organizing and reporting benchmark results in a tabular format. This class is likely used in conjunction
 with the Benchmark class to accumulate and represent the results of multiple benchmark runs.

*/