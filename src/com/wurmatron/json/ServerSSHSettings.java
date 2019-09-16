package com.wurmatron.json;

import java.lang.reflect.Field;
import java.util.Scanner;

public class ServerSSHSettings {

  public String serverUUID;
  public String host;
  public int port;
  public String username;
  public String password;
  public String rootDir;

  private ServerSSHSettings() {
  }

  public ServerSSHSettings(String uuid, String host, int port, String username, String password, String rootDir) {
    this.serverUUID = uuid;
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.rootDir = rootDir;
  }

  public static ServerSSHSettings askForSettings(ServerSSHSettings config) {
    Scanner sc = new Scanner(System.in);
    System.out.println("SSH Config");
    if (config == null) {
      config = new ServerSSHSettings();
    }
    for (Field field : config.getClass().getDeclaredFields()) {
      System.out.print("Enter the value for '" + field.getName() + "': ");
      if (field.getName().equals("port")) {
        while (sc.hasNextLine()) {
          try {
            field.set(config, Integer.parseInt(sc.nextLine()));
            break;
          } catch (NumberFormatException e) {
            System.out.println("Invalid Number");
            System.out.print("Enter the value for '" + field.getName() + "': ");
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } else {
        try {
          field.set(config, sc.nextLine());
          System.out.print("\n");
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return config;
  }
}
