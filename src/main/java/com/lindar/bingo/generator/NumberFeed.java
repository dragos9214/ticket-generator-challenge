package com.lindar.bingo.generator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lindar.bingo.generator.RandomUtil.randomUInt;

public class NumberFeed {
    List<List<Integer>> columns;

    public NumberFeed() {
        List<Integer> columnFeed1 = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
        List<Integer> columnFeed2 = IntStream.rangeClosed(10, 19).boxed().collect(Collectors.toList());
        List<Integer> columnFeed3 = IntStream.rangeClosed(20, 29).boxed().collect(Collectors.toList());
        List<Integer> columnFeed4 = IntStream.rangeClosed(30, 39).boxed().collect(Collectors.toList());
        List<Integer> columnFeed5 = IntStream.rangeClosed(40, 49).boxed().collect(Collectors.toList());
        List<Integer> columnFeed6 = IntStream.rangeClosed(50, 59).boxed().collect(Collectors.toList());
        List<Integer> columnFeed7 = IntStream.rangeClosed(60, 69).boxed().collect(Collectors.toList());
        List<Integer> columnFeed8 = IntStream.rangeClosed(70, 79).boxed().collect(Collectors.toList());
        List<Integer> columnFeed9 = IntStream.rangeClosed(80, 90).boxed().collect(Collectors.toList());

        columns = Arrays.asList(columnFeed1, columnFeed2, columnFeed3,
                columnFeed4, columnFeed5, columnFeed6, columnFeed7, columnFeed8, columnFeed9);
    }

    public Optional<Integer> popColumnRandomValue(int columnIndex) {
        List<Integer> columnFeed = columns.get(columnIndex);
        if(columnFeed.size() == 0) {
            return Optional.empty();
        }
        int randomIndex = randomUInt(columnFeed.size() - 1);
        int randomValue = columnFeed.remove(randomIndex);
        return Optional.of(randomValue);
    }
}
