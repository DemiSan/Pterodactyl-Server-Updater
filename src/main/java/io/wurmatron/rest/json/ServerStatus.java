package io.wurmatron.rest.json;

public class ServerStatus {

  public String name;
  public String status;
  public double time;
  public String[] players;
  public double lastUpdate;
  public String version;

  public ServerStatus(
      String name,
      String status,
      double time,
      String[] players,
      double lastUpdate,
      String version) {
    this.name = name;
    this.status = status;
    this.time = time;
    this.players = players;
    this.lastUpdate = lastUpdate;
    this.version = version;
  }

  public ServerStatus() {
    this.name = "Invalid";
  }
}