package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Assistant {
    SuggestionEngine suggestionEngine;
    WeatherAPIConnector weatherAPIConnector;

    Scanner scanner = new Scanner(System.in);

    Location home;
    Location work;
    Location village;
    Location beach;
    Location mountains;
    List<Location> locations = new ArrayList<>();

    public Assistant(SuggestionEngine suggestionEngine, WeatherAPIConnector weatherAPIConnector) {
        this.suggestionEngine = suggestionEngine;
        this.weatherAPIConnector = weatherAPIConnector;

        locations.add(new Location("Gdańsk", 54.343836, 18.635963));
        locations.add(new Location("Gdynia", 54.493763, 18.539557));
        locations.add(new Location("Warszawa", 52.309306, 20.965105));
        locations.add(new Location("Wrocław", 51.177036, 16.956819));
        locations.add(new Location("Zakopane", 49.31736, 19.977194));

        home = locations.get(0);
        work = locations.get(1);
        village = locations.get(2);
        beach = locations.get(3);
        mountains = locations.get(4);
    }

    public void addLocations() {

        System.out.println("Which location do you want to add? (home, work, village, beach, mountains)");
        String decision = scanner.nextLine();
        System.out.println(decision);
        if(this.locations == null){
            this.locations = new ArrayList<>();
        }
        Location newLocation;
        if (decision.equals("work")) {

            newLocation = new Location("Gdańsk", 54.343836, 18.635963);
            this.work = newLocation;

        } if (decision.equals("home")) {
            newLocation = new Location("Wrocław", 51.177036, 16.956819 );
            this.home = newLocation;

        } if (decision.equals("village")){

            newLocation = new Location("Warszawa", 54.493763, 18.539557 );
            this.village = newLocation;

        } if (decision.equals("beach")){

            newLocation = new Location("Gdynia", 54.493763, 18.539557 );
            this.beach = newLocation;
        } if (decision.equals("mountains")){
            newLocation = new Location("Zakopane", 49.31736, 19.977194 );
            this.mountains = newLocation;

        } else {
            System.out.println( "Invalid value - no more possible locations");
            return;
        }
        if(this.locations != null){
            this.locations.add(newLocation);
        }

    }

    public void whatWearNow() {
        Location chosenLocation = getLocationFromUser();

        try {
            WeatherCondition weatherCondition = weatherAPIConnector.getCurrentWeather(chosenLocation);

            printWeatherCondition(weatherCondition);
            printSuggestedClothing(suggestionEngine.getSuggestion(weatherCondition));
        } catch (IOException e) {
            System.out.println("Weather API connection error!");
        }
    }

    public void whatWearTomorrow() {
        try {
            WeatherCondition weatherAtHome = weatherAPIConnector.getTomorrowWeather(home, WeatherAPIConnector.TimeOfDay.MORN);
            WeatherCondition weatherAtWork = weatherAPIConnector.getTomorrowWeather(work, WeatherAPIConnector.TimeOfDay.EVE);

            printWeatherConditionSpan(weatherAtHome, weatherAtWork, "in the morning at home", "in the evening at work");
            printSuggestedClothing(suggestionEngine.getSuggestionForSpan(weatherAtHome, weatherAtWork));
        } catch (IOException e) {
            System.out.println("Weather API connection error!");
        }
    }

    public void whatWearSomewhere() {
        Location chosenLocation = getLocationFromUser();

        int hour = getHourFromUser();

        try {
            WeatherCondition weatherCondition = weatherAPIConnector.getSpecifiedWeather(chosenLocation, hour);

            printWeatherCondition(weatherCondition);
            printSuggestedClothing(suggestionEngine.getSuggestion(weatherCondition));
        } catch (IOException e) {
            System.out.println("Weather API connection error!");
        }
    }

    public void planTrip() {
        System.out.println("Not working yet");
    }

    private int getHourFromUser() {
        System.out.println("Please put in the time in HH:mm format:");
        String input = scanner.nextLine();

        String hourPart = input.split(":")[0];

        return Integer.parseInt(hourPart);
    }

    private Location getLocationFromUser() {
        printLocations();

        int locationNumber = Integer.parseInt(scanner.nextLine());
        return locations.get(locationNumber - 1);
    }

    private void printLocations() {
        System.out.println("-----Could you tell me please here are you?");
        System.out.println("1. Home: " + home.name);
        System.out.println("2. Work: " + work.name);
        System.out.println("3. Location: " + locations.get(2).name);
        System.out.println("4. Location: " + locations.get(3).name);
        System.out.println("5. Location: " + locations.get(4).name);
    }

    private void printWeatherConditionSpan(WeatherCondition startWeatherCondition, WeatherCondition endWeatherCondition, String startConditionDescription, String endConditionDescription) {
        System.out.println("--Weather condition " + startConditionDescription + ":");
        printWeatherCondition(startWeatherCondition);
        System.out.println("--Weather condition " + endConditionDescription + ":");
        printWeatherCondition(endWeatherCondition);
    }

    private void printWeatherCondition(WeatherCondition weatherCondition) {
        System.out.println("-----Weather conditions: ");
        System.out.println("Feels like: " + weatherCondition.getFeelsLike() + "°C");
        System.out.println("Temp: " + weatherCondition.getTemp() + "°C");
        System.out.println("UV Index: " + weatherCondition.getUvIndex());
        System.out.println("Wind speed: " + weatherCondition.getWindSpeed() + "km/h");

        if (weatherCondition.isSunShining()) {
            System.out.println("Clear sky!");
        }
        if (weatherCondition.isRaining()) {
            System.out.println("Rain!");
        }
        if (weatherCondition.isSnowing()) {
            System.out.println("Snow!");
        }
    }

    private void printSuggestedClothing(List<Clothing> suggestion) {
        System.out.println("-----You can wear:");
        for (Clothing clothing : suggestion) {
            System.out.println(clothing.getName());
        }
    }
}
