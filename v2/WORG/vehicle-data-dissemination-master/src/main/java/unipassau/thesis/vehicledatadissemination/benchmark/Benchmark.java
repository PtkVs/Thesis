package unipassau.thesis.vehicledatadissemination.benchmark;

import java.util.LinkedList;
import java.util.List;

public class Benchmark implements CSVSerializable {
    private List<BenchmarkResult> results;

    public Benchmark() {
        this.results = new LinkedList<>();
    }

    public void addResult(BenchmarkResult result) {
        results.add(result);
    }

    public String toCSV() {
        StringBuilder sb = new StringBuilder("id;time\n");

        results.forEach(result -> {
            sb.append(result.toCSV());
            sb.append("\n");
        });

        return sb.toString();
    }
}
/*
Explanation:

1. results List:
-The class contains a private field results, which is a list of BenchmarkResult objects.
-These results likely represent the outcome of individual benchmark runs.

2. Constructor:
-The constructor initializes the results list as a new LinkedList when a Benchmark object is created.

3. addResult Method:
-The addResult method allows adding a BenchmarkResult to the results list.
-This method is likely used to accumulate benchmark results during the benchmarking process.

4. toCSV Method:
- The toCSV method generates a CSV representation of the benchmark results.
-It starts by appending a header line to the CSV string with the column names "id" and "time".
-It then iterates over the list of benchmark results, calling the toCSV method on each BenchmarkResult to get its CSV representation, and appends it to the CSV string.
-Each line in the CSV represents a benchmark result, and the "id" and "time" columns are separated by a semicolon.
-The resulting CSV string is then returned.

**Summary**:
The Benchmark class is a container for storing and serializing benchmark results. It keeps track of individual benchmark results
in a list and provides a method (toCSV) to convert these results into a CSV-formatted string. This kind of structure is often useful
for organizing and reporting the results of benchmarking experiments in a tabular format. The Benchmark class, along with its associated
BenchmarkResult class, is likely used in the benchmarking logic of the application to track and present performance metrics.
*/