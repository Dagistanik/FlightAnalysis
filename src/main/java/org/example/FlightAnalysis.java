package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
            Map<String, List<Ticket>> ticketsMap = gson.fromJson(json, new TypeToken<Map<String, List<Ticket>>>() {
            }.getType());
            return ticketsMap.get("tickets");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Integer> calculateMinDurations(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getOriginName().equals("Владивосток") && t.getDestinationName().equals("Тель-Авив"))
                .collect(Collectors.groupingBy(
                        Ticket::getCarrier,
                        Collectors.minBy(Comparator.comparingInt(t -> calculateDuration(t.getDepartureTime(), t.getArrivalTime())))
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateDuration(entry.getValue().get().getDepartureTime(), entry.getValue().get().getArrivalTime())
                ));
    }

    private static int calculateDuration(String departureTime, String arrivalTime) {
        String[] departureSplit = departureTime.split(":");
        String[] arrivalSplit = arrivalTime.split(":");

        int departureMinutes = Integer.parseInt(departureSplit[0]) * 60 + Integer.parseInt(departureSplit[1]);
        int arrivalMinutes = Integer.parseInt(arrivalSplit[0]) * 60 + Integer.parseInt(arrivalSplit[1]);

        if (arrivalMinutes < departureMinutes) {
            arrivalMinutes += 24 * 60;
        }

        return arrivalMinutes - departureMinutes;
    }

    private static double calculateAveragePrice(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> t.getOriginName().equals("Владивосток") && t.getDestinationName().equals("Тель-Авив"))
                .mapToInt(Ticket::getPrice)
                .average()
                .orElse(0);
    }

    private static double calculateMedianPrice(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .filter(t -> t.getOriginName().equals("Владивосток") && t.getDestinationName().equals("Тель-Авив"))
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