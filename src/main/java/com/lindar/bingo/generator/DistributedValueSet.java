package com.lindar.bingo.generator;

import java.util.*;

import static com.lindar.bingo.generator.RandomUtil.randomUInt;
import static com.lindar.bingo.generator.Ticket.COLUMN_COUNT;
import static com.lindar.bingo.generator.Ticket.TICKET_COUNT;

public class DistributedValueSet {
    public final List<List<List<Integer>>> ticketValueSets;
    public final List<Integer> ticketCounts = new ArrayList<>(Collections.nCopies(6, 0));

    private DistributedValueSet() {
        List<List<Integer>> ticket1Values = new ArrayList<>();
        List<List<Integer>> ticket2Values = new ArrayList<>();
        List<List<Integer>> ticket3Values = new ArrayList<>();
        List<List<Integer>> ticket4Values = new ArrayList<>();
        List<List<Integer>> ticket5Values = new ArrayList<>();
        List<List<Integer>> ticket6Values = new ArrayList<>();

        for (int column = 0; column < COLUMN_COUNT; column++) {
            ticket1Values.add(new ArrayList<>());
            ticket2Values.add(new ArrayList<>());
            ticket3Values.add(new ArrayList<>());
            ticket4Values.add(new ArrayList<>());
            ticket5Values.add(new ArrayList<>());
            ticket6Values.add(new ArrayList<>());
        }

        ticketValueSets = Arrays.asList(ticket1Values, ticket2Values, ticket3Values,
                ticket4Values, ticket5Values, ticket6Values);
    }


    private void addValueToTicketColumn(Integer value, int ticketIndex, int columnIndex) {
        ticketValueSets.get(ticketIndex).get(columnIndex).add(value);
        ticketCounts.set(ticketIndex, ticketCounts.get(ticketIndex) + 1);
    }

    private void addColumnValueToRandomLimitedTicket(Integer value, int columnIndex, int valueLimit) {
        int randomTicket = randomUInt(TICKET_COUNT - 1);
        boolean cornerCaseReached = false;
        while (ticketCounts.get(randomTicket) == 15 ||
                ticketValueSets.get(randomTicket).get(columnIndex).size() == valueLimit) {
            randomTicket++;
            if (randomTicket == 6) {
                if (cornerCaseReached) {
                    treatCornerCase(valueLimit, value);
                    return;
                }
                cornerCaseReached = true;
                randomTicket = 0;
            }
        }
        ticketValueSets.get(randomTicket).get(columnIndex).add(value);
        ticketCounts.set(randomTicket, ticketCounts.get(randomTicket) + 1);
    }

    // Corner case occurs when allocating the last values of the last column, the biggest.
    // Figures out what ticket is not full, and moves a value from columns 1 - 8 from a full ticket to it.
    // This way it makes space for the last value.
    private void treatCornerCase(int valueLimit, Integer value) {
        int emptiestTicket = 0;
        for (int ticketIndex = 0; ticketIndex < TICKET_COUNT; ticketIndex++) {
            if (ticketCounts.get(emptiestTicket) > ticketCounts.get(ticketIndex)) {
                emptiestTicket = ticketIndex;
            }
        }
        int emptiestColumn = 0;
        for (int columnIndex = 0; columnIndex < COLUMN_COUNT - 1; columnIndex++) {
            if (ticketValueSets.get(emptiestTicket).get(emptiestColumn).size() >
                    ticketValueSets.get(emptiestTicket).get(columnIndex).size()) {
                emptiestColumn = columnIndex;
            }
        }
        for (int i = 0; i < TICKET_COUNT; i++) {
            if (i != emptiestTicket &&
                    ticketValueSets.get(i).get(8).size() < valueLimit &&
                    ticketValueSets.get(i).get(emptiestColumn).size() > 1) {
                final Integer move = ticketValueSets.get(i).get(emptiestColumn).remove(0);
                ticketValueSets.get(emptiestTicket).get(emptiestColumn).add(move);
                ticketCounts.set(emptiestTicket, ticketCounts.get(emptiestTicket) + 1);
                ticketValueSets.get(i).get(8).add(value);
                break;
            }
        }
    }

    private void addColumnValueToRandomOneValueTicket(Integer value, int columnIndex) {
        addColumnValueToRandomLimitedTicket(value, columnIndex, 2);
    }

    private void addColumnValueToRandomAvailableTicket(Integer value, int columnIndex) {
        addColumnValueToRandomLimitedTicket(value, columnIndex, 3);
    }

    private void sortTicketColumns() {
        ticketValueSets.forEach(ticket -> ticket.forEach(Collections::sort));
    }

    public static DistributedValueSet generateRandomValueSet() {
        NumberFeed numberFeed = new NumberFeed();
        DistributedValueSet valueSet = new DistributedValueSet();

        // assigning randomly one number to each column of all tickets
        // 54 assigned. 36 values remaining in the number feed
        for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
            for (int ticketIndex = 0; ticketIndex < TICKET_COUNT; ticketIndex++) {
                final Integer value = numberFeed.popColumnRandomValue(columnIndex).get();
                valueSet.addValueToTicketColumn(value, ticketIndex, columnIndex);
            }
        }

        // assign randomly one number from the last column to random ticket
        // 55 assigned. 35 values remaining in the number feed
        final Integer columnRandomValue = numberFeed.popColumnRandomValue(8).get();
        final int randomTicket = RandomUtil.randomUInt(5);
        valueSet.addValueToTicketColumn(columnRandomValue, randomTicket, 8);

        // 3 passes populating one value ticket-columns
        // 27 values assigned. 8 values remaining in the number feed
        for (int pass = 0; pass < 3; pass++) {
            for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
                final Optional<Integer> randomValue = numberFeed.popColumnRandomValue(columnIndex);
                if (randomValue.isPresent()) {
                    valueSet.addColumnValueToRandomOneValueTicket(randomValue.get(), columnIndex);
                }
            }
        }

        //populate one or two value ticket columns with the remaining 8 values
        for (int columnIndex = 0; columnIndex < COLUMN_COUNT; columnIndex++) {
            final Optional<Integer> randomValue = numberFeed.popColumnRandomValue(columnIndex);
            if (randomValue.isPresent()) {
                valueSet.addColumnValueToRandomAvailableTicket(randomValue.get(), columnIndex);
            }
        }

        valueSet.sortTicketColumns();
        return valueSet;
    }

    public Integer popValue(int ticketPosition, int column) {
        final Integer value = ticketValueSets.get(ticketPosition).get(column).remove(0);
        ticketCounts.set(ticketPosition, ticketCounts.get(ticketPosition) - 1);
        return value;
    }

    public boolean isValueAvailable(int ticketPosition, int column, int size) {
        return ticketValueSets.get(ticketPosition).get(column).size() == size;
    }

    public void print() {
        ticketValueSets.forEach(ticket -> {
            ticket.forEach(column -> {
                System.out.print("[");
                column.forEach(value -> {
                    System.out.print(value);
                    System.out.print(" ");
                });
                System.out.print("]\t");
            });
            System.out.print("\n");
        });
    }
}
