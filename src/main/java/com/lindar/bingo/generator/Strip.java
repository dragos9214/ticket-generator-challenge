package com.lindar.bingo.generator;

import java.util.Collections;
import java.util.List;

public class Strip {
    private final List<Ticket> tickets;
    private final int id;

    public Strip(List<Ticket> tickets, int id) {
        this.tickets = tickets;
        this.id = id;
    }

    public void print() {
        System.out.println("\n>>>\tStrip no. " + id + "\t <<<");
        tickets.forEach(Ticket::print);
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }
}
