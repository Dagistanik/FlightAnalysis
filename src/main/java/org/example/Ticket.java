package org.example;

public class Ticket {
    private String carrier;
    private String origin;
    private String origin_name;
    private String destination;
    private String destination_name;
    private String departure_date;
    private String departure_time;
    private String arrival_date;
    private String arrival_time;
    private int stops;
    private int price;

    public String getCarrier() {
        return carrier;
    }

    public String getOriginName() {
        return origin_name;
    }

    public String getDestinationName() {
        return destination_name;
    }

    public String getDepartureTime() {
        return departure_time;
    }

    public String getArrivalTime() {
        return arrival_time;
    }

    public int getPrice() {
        return price;
    }

    public String getDepartureDate() {
        return departure_date;
    }

    public String getArrivalDate() {
        return arrival_date;
    }
}