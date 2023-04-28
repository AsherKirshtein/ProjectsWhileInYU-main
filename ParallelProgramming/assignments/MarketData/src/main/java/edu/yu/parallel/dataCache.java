package edu.yu.parallel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class dataCache {
    private List<otherData> symbolList;

    public dataCache(dataReader reader) {
        this.symbolList = reader.stream()
                //.filter(data -> data.close() < (data.high() + data.low()) / 2)
                .collect(Collectors.toList());
    }

    public Stream<otherData> stream() {
        return symbolList.stream();
    }
}
