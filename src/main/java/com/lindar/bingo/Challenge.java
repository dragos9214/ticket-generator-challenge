package com.lindar.bingo;

import com.lindar.bingo.generator.Strip;
import com.lindar.bingo.generator.StripGenerator;

import java.util.List;

public class Challenge {
    /**
     * @param args
     */
    public static final int DEFAULT_STRIP_COUNT = 10000;
    public static void main(String[] args) {
        int stripsCount = DEFAULT_STRIP_COUNT;

        if(args.length > 0) {
            try {
                final int argStrip = Integer.parseInt(args[0]);
                if(argStrip > 0) {
                    stripsCount = argStrip;
                } else {
                    System.out.println("Expected positive integer argument, got: " + args[0]);
                    System.out.println("Fallback to default value: " + DEFAULT_STRIP_COUNT);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Failed to parse program argument. Expected positive integer, got: " + args[0]);
                System.out.println("Fallback to default value: " + DEFAULT_STRIP_COUNT);
            }
        }

        final List<Strip> strips = StripGenerator.asyncGenerateMultiple(stripsCount);
        strips.forEach(Strip::print);
    }
}