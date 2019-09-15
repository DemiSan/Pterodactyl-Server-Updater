package com.wurmatron.json;

import java.lang.reflect.Field;
import java.util.Scanner;

public class ServerSFTPSettings {

  public String serverUUID;
  public String url;
  public int port;
  public String username;
  public String password;
  public String rootDir;

  public ServerSFTPSettings() {
  }

  public ServerSFTPSettings(String serverUUID, String url, int port, String username, String password, String rootDir) {
    this.serverUUID = serverUUID;
    this.url = url;
    this.port = port;
    this.username = username;
    this.password = password;
    this.rootDir = rootDir;
  }

  public static ServerSFTPSettings askForSettings(ServerSFTPSettings settings) {
    Scanner sc = new Scanner(System.in);
    for (Field field : settings.getClass().getDeclaredFields()) {
      System.out.print("Enter the value for '" + field.getName() + "': ");
      try {
        if (field.getName().equalsIgnoreCase("port")) {
          while (sc.hasNext()) {
            try {
              field.set(settings, Integer.parseInt(sc.nextLine()));
              break;
            } catch (NumberFormatException e) {
              System.out.println("Invalid Number");
              System.out.print("Enter the value for '" + field.getName() + "': ");
            }
          }
        } else {
          field.set(settings, sc.nextLine().replaceAll(" ", ""));
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      System.out.print("\n");
    }
    return settings;
  }
}
