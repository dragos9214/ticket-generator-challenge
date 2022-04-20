package com.lindar.bingo.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lindar.bingo.generator.RandomUtil.*;

public class Ticket {
    public static final int TICKET_COUNT = 6;
    public static final int COLUMN_COUNT = 9;
    public static final int ROW_PER_TICKET_COUNT = 9;

    private static final int MAX_ROW_VALUES = 5;

    private final List<List<Integer>> ticketRows;
    private final List<Integer> rowSizes = new ArrayList<>(Collections.nCopies(ROW_PER_TICKET_COUNT, 0));
    private final int ticketPosition;

    public Ticket(int ticketPosition) {
        List<Integer> row1 = new ArrayList<>(Collections.nCopies(COLUMN_COUNT, 0));
        List<Integer> row2 = new ArrayList<>(Collections.nCopies(COLUMN_COUNT, 0));
        List<Integer> row3 = new ArrayList<>(Collections.nCopies(COLUMN_COUNT, 0));
        ticketRows = List.of(row1, row2, row3);

        this.ticketPosition = ticketPosition;
    }

    private boolean rowIsNotFull(int rowIndex) {
        return rowSizes.get(rowIndex) < MAX_ROW_VALUES;
    }

    private void sortColumn(int column) {
        sortTwoColumnValues(0, 1, column);
        sortTwoColumnValues(1, 2, column);
        sortTwoColumnValues(0, 1, column);
        sortTwoColumnValues(0, 2, column);
    }

    private void sortTwoColumnValues(int rowIndex1, int rowIndex2, int column) {
        final Integer firstValue = ticketRows.get(rowIndex1).get(column);
        final Integer secondValue = ticketRows.get(rowIndex2).get(column);
        if (firstValue != 0 && secondValue != 0 && firstValue > secondValue) {
            ticketRows.get(rowIndex1).set(column, secondValue);
            ticketRows.get(rowIndex2).set(column, firstValue);
        }
    }

    public void sortColumns() {
        for (int column = 0; column < COLUMN_COUNT; column++) {
            this.sortColumn(column);
        }
    }
    public void distributeValuesToFirstRow(DistributedValueSet valueSet) {
        distributeValuesToTicket(0, valueSet, 3);
    }
    public void distributeValuesToSecondRow(DistributedValueSet valueSet) {
        distributeValuesToTicket(1, valueSet, 2);
    }
    public void distributeValuesToThirdRow(DistributedValueSet valueSet) {
        distributeValuesToTicket(2, valueSet, 1);
    }

    private void distributeValuesToTicket(int rowIndex, DistributedValueSet valueSet, int sizeCheck) {
        for (int size = sizeCheck; size > 0; size--) {
            int increment = randomListElement(1, 2, 4, 5, 7, 8);
            int column = randomUInt(8);
            int countdown = 9;
            while (rowIsNotFull(rowIndex) && countdown > 0) {
                if (isTicketSpotAvailable(rowIndex, column) && valueSet.isValueAvailable(ticketPosition, column, size)) {
                    setTicketValue(rowIndex, column, valueSet.popValue(ticketPosition, column));
                }
                countdown--;
                column+=increment;
                if (column > 8) {
                    column -= 9;
                }
            }
        }
    }

    private boolean isTicketSpotAvailable(int row, int column) {
        final List<Integer> ticketRow = ticketRows.get(row);
        return (ticketRow.get(column) == 0);
    }

    private void setTicketValue(int row, int column, Integer value) {
        final List<Integer> ticketRow = ticketRows.get(row);
        if (ticketRow.get(column) == 0) {
            ticketRow.set(column, value);
            rowSizes.set(row, rowSizes.get(row) + 1);
        }
    }

    public List<List<Integer>> getTicketRows() {
        return ticketRows;
    }

    public void print() {
        System.out.print("_________________________________________________________________________\n");
        ticketRows.forEach(row -> {
            row.forEach(value -> {
                if (value > 0)
                    System.out.print("|\t" + value + "\t");
                else System.out.print("|\t\t");
            });
            System.out.print("|\n");
            System.out.print("-------------------------------------------------------------------------\n");
        });
    }
}
