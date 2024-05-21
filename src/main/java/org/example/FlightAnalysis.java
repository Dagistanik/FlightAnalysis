package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FlightAnalysis {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FlightAnalysis <path_to_tickets.json>");
            System.exit(1);
        }

        String filePath = args[0];
        List<Ticket> tickets = readTicketsFromFile(filePath);

        if (tickets == null) {
            System.err.println("Failed to read or parse the tickets file.");
            System.exit(1);
        }

        Map<String, Integer> minDurations = calculateMinDurations(tickets);
        double averagePrice = calculateAveragePrice(tickets);
        double medianPrice = calculateMedianPrice(tickets);

        System.out.println("Minimum flight durations by carrier:");
        for (Map.Entry<String, Integer> entry : minDurations.entrySet()) {
            System.out.printf("Carrier: %s, Min Duration: %d minutes%n", entry.getKey(), entry.getValue());
        }

        System.out.printf("Difference between average and median price: %.2f%n", averagePrice - medianPrice);
    }

    private static List<Ticket> readTicketsFromFile(String filePath) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            Gson gson = new Gson();
            Ticket[] ticketsArray = gson.fromJson(json, Ticket[].class);
            return Arrays.asList(ticketsArray);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Integer> calculateMinDurations(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getDeparture().equals("Vladivostok") && t.getArrival().equals("Tel-Aviv"))
                .collect(Collectors.groupingBy(
                        Ticket::getCarrier,
                        Collectors.minBy(Comparator.comparingInt(Ticket::getDuration))
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get().getDuration()
                ));
    }

    private static double calculateAveragePrice(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getDeparture().equals("Vladivostok") && t.getArrival().equals("Tel-Aviv"))
                .mapToInt(Ticket::getPrice)
                .average()
                .orElse(0);
    }

    private static double calculateMedianPrice(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .filter(t -> t.getDeparture().equals("Vladivostok") && t.getArrival().equals("Tel-Aviv"))
                .map(Ticket::getPrice)
                .sorted()
                .collect(Collectors.toList());

        int size = prices.size();
        if (size == 0) return 0;
        if (size % 2 == 1) {
            return prices.get(size / 2);
        } else {
            return (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        }
    }
}
