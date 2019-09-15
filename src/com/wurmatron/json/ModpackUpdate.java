package com.wurmatron.json;

public class ModpackUpdate {

  public Type updateType;
  public String updateURL;

  public ModpackUpdate(Type updateType, String updateURL) {
    this.updateType = updateType;
    this.updateURL = updateURL;
  }

  public enum Type {
    TWITCH;
  }
}
