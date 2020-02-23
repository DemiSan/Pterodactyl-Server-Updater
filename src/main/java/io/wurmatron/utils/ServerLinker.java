package io.wurmatron.utils;

import com.stanjg.ptero4j.entities.panel.admin.Server;
import io.wurmatron.Updater;
import io.wurmatron.ptredactyl.Ptredactyl;
import io.wurmatron.rest.RequestGenerator;
import io.wurmatron.rest.Rest;
import io.wurmatron.rest.json.ServerStatus;
import io.wurmatron.utils.storage.SFTP;
import io.wurmatron.utils.storage.ServerData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerLinker {

  // Server UUID -> Rest ID
  public static HashMap<String, String> linkUUIDToName(Ptredactyl petro, Rest rest) {
    HashMap<String, String> restLookup = new HashMap<>();
    if (RequestGenerator.Status.getServerStatus() != null) {
      for (ServerStatus status : RequestGenerator.Status.getServerStatus()) {
        String uuid = getUUIDFromList(petro, status.name);
        restLookup.put(uuid, status.name);
      }
      return restLookup;
    } else {
      System.out.println("Unable to find servers on rest database!");
      System.out.println(
          "Unable to continue, Check Rest database, (" + Updater.config.restBaseURL + "/status)");
      System.exit(-3);
    }
    return new HashMap<>();
  }

  private static String getUUIDFromList(Ptredactyl petro, String restServerID) {
    System.out.println("Match the following name on Rest with the one in the panel");
    System.out.println("Rest-Name: " + restServerID);
    Server[] servers = petro.getServerList();
    for (int index = 0; index < servers.length; index++) {
      System.out
          .println(index + ". " + servers[index].getName() + " (" + servers[index].getUuid() + ")");
    }
    String input = UserInput.askAndGetInput("Enter the number of the corresponding server: ");
    try {
      int id = Integer.parseInt(input);
      return servers[id].getUuid();
    } catch (NumberFormatException e) {
      System.out.println("Invalid ID, try again!");
      return getUUIDFromList(petro, restServerID);
    }
  }

  public static HashMap<String, SFTP> linkRestIDToSFTP() {
    HashMap<String, SFTP> sshLookup = new HashMap<>();
    for (ServerStatus status : RequestGenerator.Status.getServerStatus()) {
      sshLookup.put(status.name, collectSFTPLoginForRestID(status.name));
    }
    return sshLookup;
  }

  private static SFTP collectSFTPLoginForRestID(String restName) {
    System.out.println("Enter the SFTP login info for the given Rest-Name");
    System.out.println("Rest-Name: " + restName);
    String ip = UserInput.askAndGetInput("Enter the IP Address (with or without port): ");
    String port = "22"; // Default SFTP Port
    if (ip.contains(":")) { // Has Port
      String portStr = ip.substring(ip.indexOf(":"), ip.length() - 1);
      try {
        port = "" + Integer.parseInt(portStr);
      } catch (NumberFormatException e) {
        System.out.println("Unable to read Port from IP!");
        port = UserInput.askAndGetInput("Enter port: ");
      }
    } else {
      port = UserInput.askAndGetInput("Enter port: ");
    }
    String username = UserInput.askAndGetInput("Enter Username: ");
    String password = UserInput.askAndGetInput("Enter Password: ");
    return new SFTP(ip, port, username, password);
  }

  public static void addRestData(HashMap<String, String> data) {
    if (data != null) {
      List<ServerData> serverData = new ArrayList<>();
      for (String panelUUID : data.keySet()) {
        ServerData server = new ServerData(panelUUID, data.get(panelUUID));
        serverData.add(server);
      }
      Updater.config.servers = serverData;
      Updater.config.save();
    } else {
      linkUUIDToName(Updater.ptero, Updater.rest);
    }
  }

  public static void addSFTPData(HashMap<String, SFTP> data) {
    if (data != null) {
      if (!Updater.config.servers.isEmpty()) {
        for (ServerData serverData : Updater.config.servers) {
          for (String restName : data.keySet()) {
            if (serverData.restName.equals(restName)) {
              serverData.addSFTP(data.get(restName));
            }
          }
        }
        Updater.config.save();
      } else {
        System.out.println("Error! Needs data to continue! (Rest-Name -> Panel UUID)");
        addRestData(linkUUIDToName(Updater.ptero, Updater.rest));
      }
    } else {
      addSFTPData(linkRestIDToSFTP());
    }
  }
}
