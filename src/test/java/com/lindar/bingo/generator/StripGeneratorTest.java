package com.lindar.bingo.generator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StripGeneratorTest {

    private static List<Strip> generatedStrips;
    private static long performanceCheckMs;

    @BeforeAll
    public static void prepareTestExecution() {
        long startTime = System.nanoTime();
        generatedStrips = StripGenerator.asyncGenerateMultiple(10000);
        performanceCheckMs = (System.nanoTime() - startTime) / 1000000;

        System.out.println("Total generated strips: " + generatedStrips.size());
        System.out.println("Total execution time in millis, async execution: " + performanceCheckMs);
    }

    @Test
    @DisplayName("Check if 10.000 get generated under 1s")
    public void performanceValidation() {
        assertTrue(performanceCheckMs < 1000, "Almost there, failed to generate under 1000ms");
    }
    @Test
    @DisplayName("Check that rows have 5 values and 4 blanks each")
    public void testRowDistribution() {
        generatedStrips.forEach(strip -> {
            strip.getTickets().forEach(ticket -> {
                ticket.getTicketRows().forEach(row -> {
                    final long count = row.stream().filter(value -> value > 0).count();
                    assertEquals(5, count, "Failed to distribute 5 values per row");
                    assertEquals(4, row.size() - count, "Failed to distribute 4 blanks per row");
                });
            });
        });
    }

    @Test
    @DisplayName("Check that all columns have at least one value per ticket and they are order ascending.")
    public void testColumnDistributionAndOrder() {
        generatedStrips.forEach(strip -> {
            final List<Ticket> stripTickets = strip.getTickets();

            stripTickets.forEach(ticket -> {
                final List<List<Integer>> ticketMeta = ticket.getTicketRows();
                for (int column = 0; column < 9; column++) {
                    int sum = ticketMeta.get(0).get(column) +
                            ticketMeta.get(1).get(column) +
                            ticketMeta.get(2).get(column);

                    assertTrue(sum > 0, "3 blanks are not allowed.");

                    if (ticketMeta.get(2).get(column) != 0) {
                        boolean orderCheck = ticketMeta.get(2).get(column) > ticketMeta.get(1).get(column);
                        orderCheck &= ticketMeta.get(2).get(column) > ticketMeta.get(0).get(column);
                        assertTrue(orderCheck, "Ticket Column not respected.");
                    }
                    if (ticketMeta.get(1).get(column) != 0) {
                        boolean orderCheck = ticketMeta.get(1).get(column) > ticketMeta.get(0).get(column);
                        assertTrue(orderCheck, "Ticket Column not respected.");
                    }
                }
            });
        });
    }

    @Test
    @DisplayName("Check that a strip contains 6 tickets, the tickets contain all 90 values," +
            " 72 blanks and no duplicates or out of range values.")
    public void testCheckAll90AppearAndNoDuplicates() {
        generatedStrips.forEach(strip -> {
            final List<Ticket> stripTickets = strip.getTickets();
            assertEquals(6, stripTickets.size(), "Wrong ticket count.");

            final List<Integer> values = stripTickets.stream().flatMap(ticket -> {
                final List<List<Integer>> ticketRows = ticket.getTicketRows();
                assertEquals(3, ticketRows.size(), "Wrong number of rows in ticket");
                return ticketRows.stream().flatMap(row -> {
                    assertEquals(9, row.size(), "Wrong number of columns");
                    return row.stream().peek(value ->
                            assertTrue(value >= 0 && value <= 90, "Wrong value on ticket"));
                });
            }).collect(Collectors.toList());

            assertEquals(162, values.size(), "Value count mismatch.");
            IntStream.rangeClosed(1, 90).forEach(expectedValue -> {
                assertEquals(1,
                        values.stream().filter(value -> value.equals(expectedValue)).count(),
                        "Duplicate or no value found: " + expectedValue);
            });

            final long zeroCount = values.stream().filter(value -> value.equals(0)).count();
            assertEquals(72, zeroCount, "Wrong number of blanks found.");
        });
    }

    @Test
    @DisplayName("Check that columns have the appropriate range")
    public void testAppropriateColumnRange() {
        generatedStrips.forEach(strip -> {
            final List<Ticket> stripTickets = strip.getTickets();
            stripTickets.forEach(ticket -> {
                final List<List<Integer>> ticketRows = ticket.getTicketRows();
                ticketRows.forEach(row -> {
                    for(int column = 0; column < Ticket.COLUMN_COUNT; column++) {
                        if(row.get(column) != 0) {
                            assertTrue(row.get(column) >= (column * 10), "Column lower range limit not respected");
                            int rangeEnd = (column * 10) + 9 + (column / 8);
                            assertTrue(row.get(column) <= rangeEnd, "Column upper range limit not respected");
                        }
                    }
                });
            });
        });
    }
}
