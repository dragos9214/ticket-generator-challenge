package com.lindar.bingo.generator;

public final class RandomUtil {
    public static int randomUInt(int upper) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(upper + 1);
    }

    public static int randomListElement(int... randomList) {
        int randomIncrement = randomUInt(randomList.length - 1);
        return randomList[randomIncrement];
    }
}
