package com.assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Contestant {
    String name;
    int totalPoints;

    public Contestant(Character name) {
        this.name = String.valueOf(name);
        this.totalPoints = 0;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }
}

class Region {
    String name;
    List<Character> contestants;
    Map<Integer, String> voters;

    public Region(String name, String contestants) {
        this.name = name;
        this.contestants = new ArrayList<>();
        for (char c : contestants.toCharArray()) {
            this.contestants.add(c);
        }
        this.voters = new HashMap<>();
    }
}

public class ElectionSystem {
    private static final int FIRST_PREF_POINTS = 3;
    private static final int SECOND_PREF_POINTS = 2;
    private static final int THIRD_PREF_POINTS = 1;

    private static Map<Character, Contestant> contestants = new HashMap<>();
    private static List<Region> regions = new ArrayList<>();

    public static void main(String[] args) {
        readInput("C:\\Users\\kotar\\JAVA_PRACTICE\\workspace\\election-system\\src\\main\\resources\\voting.dat");
        countVotes();
        displayResults();
    }

    private static void readInput(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("/") && !line.equals("//")) {
                    String[] parts = line.split("/");
                    regions.add(new Region(parts[0], parts[1]));
                    continue;
                }
                if (line.equals("&&")) {
                    break;
                }
                 if (line.endsWith("//")) {
                    continue;
                }
                else {
                    String[] parts = line.split(" ");
                    regions.get(regions.size() - 1).voters.put(Integer.parseInt(parts[0]), parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void countVotes() {
        for (Region region : regions) {
            for (Map.Entry<Integer, String> entry : region.voters.entrySet()) {
                String[] preferences = entry.getValue().split("");
                for (int i = 0; i < preferences.length; i++) {
                    char candidate = preferences[i].charAt(0);
                    if (!region.contestants.contains(candidate) || i >= 3) {
                        // Invalid vote
                        break;
                    }
                    int points = 0;
                    switch (i) {
                        case 0:
                            points = FIRST_PREF_POINTS;
                            break;
                        case 1:
                            points = SECOND_PREF_POINTS;
                            break;
                        case 2:
                            points = THIRD_PREF_POINTS;
                            break;
                    }
                    contestants.computeIfAbsent(candidate, Contestant::new).addPoints(points);
                }
            }
        }
    }

    private static void displayResults() {
        Contestant chiefOfficer = null;
        Contestant regionalHead = null;
        int maxPoints = 0;

        // Find chief officer
        for (Contestant contestant : contestants.values()) {
            if (contestant.totalPoints > maxPoints) {
                maxPoints = contestant.totalPoints;
                chiefOfficer = contestant;
            }
        }

        System.out.println("Chief Officer: " + chiefOfficer.name + " - " + chiefOfficer.totalPoints + " points");

        // Find regional heads
        for (Region region : regions) {
            maxPoints = 0;
            for (char contestant : region.contestants) {
                Contestant contestant1 = contestants.get(contestant);
                if (contestant1!=null && contestant1.totalPoints > maxPoints) {
                    maxPoints = contestant1.totalPoints;
                    regionalHead = contestant1;
                }
            }
            System.out.println("Region: " + region.name);
            System.out.println("Regional Head: " + regionalHead.name + " - " + regionalHead.totalPoints + " points");
            System.out.println("Invalid Votes: " + countInvalidVotes(region));
        }
    }

    private static int countInvalidVotes(Region region) {
        int invalidVotes = 0;
        for (Map.Entry<Integer, String> entry : region.voters.entrySet()) {
            String[] preferences = entry.getValue().split("");
            if (preferences.length == 0 || preferences.length > 3) {
                invalidVotes++;
                continue;
            }
            for (String preference : preferences) {
                char candidate = preference.charAt(0);
                if (!region.contestants.contains(candidate)) {
                    invalidVotes++;
                    break;
                }
            }
        }
        return invalidVotes;
    }
}