package com.wurmcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.json.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ServerUpdater {

  public static Config config;
  public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public static void main(String[] args) {
    loadConfig();
  }

  private static void loadConfig() {
    try {
      config = gson.fromJson(new FileReader(new File("settings.json")), Config.class);
    } catch (FileNotFoundException e) {
      System.out.println("Error finding, settings.json");
      config = createNewConfig();
    }
  }

  private static Config createNewConfig() {
    System.out.println("Enter the panel Admin API Key: ");
    Scanner sc = new Scanner(System.in);
    String apiKey = sc.nextLine();
    System.out.println("Enter the file used for modpack updates (local to each server): ");
    String updateScript = sc.nextLine();
    return new Config(apiKey, updateScript);
  }
}
