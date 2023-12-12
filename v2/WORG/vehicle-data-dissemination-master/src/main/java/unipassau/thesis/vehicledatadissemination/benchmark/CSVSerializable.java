package unipassau.thesis.vehicledatadissemination.benchmark;

public interface CSVSerializable {
    public String toCSV();
}

/*
1.Interface Declaration:
-The CSVSerializable interface declares a single method named toCSV with a return type of String.

2.toCSV Method:
-The interface requires implementing classes to provide an implementation for the toCSV method.
-The purpose of this method is to convert an object into a CSV-formatted string.

3.Purpose:
-The CSVSerializable interface is likely designed to be implemented by classes whose instances need to be represented in a CSV format.
-Classes that implement this interface will provide their own logic in the toCSV method to define how their instances should be serialized
  into a CSV string.

4.Usage:
-This interface is used to establish a common contract for serialization to CSV within the context of the benchmarking functionality
 (as seen in the Benchmark and BenchmarkResult classes).

**Summary**:
The CSVSerializable interface defines a method, toCSV, that implementing classes must override. This interface serves as a marker interface
indicating that classes implementing it can be serialized to CSV. It provides a standardized way to convert objects into CSV strings,
ensuring a consistent approach for serialization within the context of the benchmarking functionality.
*/