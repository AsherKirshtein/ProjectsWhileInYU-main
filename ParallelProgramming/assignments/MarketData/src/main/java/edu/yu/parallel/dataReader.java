package edu.yu.parallel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class dataReader 
{
    private final String fileName;
    private final Function<String, List<String>> csvLineParser = line -> Arrays.asList(line.split(","));
    private double high;
    private final Function<List<String>, otherData> inputToRecord = input -> {

        var date = input.get(0);
        this.high = Double.valueOf(input.get(1));
        var low = Double.valueOf(input.get(2));
        var open = Double.valueOf(input.get(3));
        var close = Double.valueOf(input.get(4));
        var volume = Double.valueOf(input.get(5));
        var adjClose = Double.valueOf(input.get(6));

        return new otherData(date, high, low,open, close, volume, adjClose);
    };

    public dataReader(String symbolFile) {
        this.fileName = "/data/" + symbolFile;
    }

    public double getHigh() {
        return this.high;
    }

    public Stream<otherData> stream() {
        return fromFile().skip(1).map(csvLineParser).map(inputToRecord);
    }

    private Stream<String> fromFile() {
        InputStream csvFileStream = getClass().getResourceAsStream(this.fileName);
        return new BufferedReader(new InputStreamReader(csvFileStream)).lines();
    }    
}
