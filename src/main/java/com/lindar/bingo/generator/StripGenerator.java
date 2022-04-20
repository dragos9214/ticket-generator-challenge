package com.lindar.bingo.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StripGenerator {
    public static List<Strip> asyncGenerateMultiple(int count) {
        return IntStream.range(0, count)
                .parallel()
                .mapToObj(StripGenerator::generateStrip)
                .collect(Collectors.toList());
    }

    public static List<Strip> syncGenerateMultiple(int count) {
        return IntStream.range(0, count)
                .mapToObj(StripGenerator::generateStrip)
                .collect(Collectors.toList());
    }

    public static Strip generateStrip(int stripId) {
        DistributedValueSet distributedValueSet = DistributedValueSet.generateRandomValueSet();
        return buildStrip(stripId, distributedValueSet);
    }

    private static Strip buildStrip(int stripId, DistributedValueSet valueSet) {
        List<Ticket> stripTickets = new ArrayList<>();

        for(int ticketNo = 0; ticketNo < 6; ticketNo++) {
            Ticket ticket = new Ticket(ticketNo);
            ticket.distributeValuesToFirstRow(valueSet);
            ticket.distributeValuesToSecondRow(valueSet);
            ticket.distributeValuesToThirdRow(valueSet);
            ticket.sortColumns();
            stripTickets.add(ticket);
        }

        return new Strip(stripTickets, stripId);
    }
}
