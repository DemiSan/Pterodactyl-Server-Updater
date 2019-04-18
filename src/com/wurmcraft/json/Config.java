package com.wurmcraft.json;

public class Config {

  public String API_KEY;
  public String MODPACK_INFO_FILE;

  public Config(String API_KEY, String MODPACK_INFO_FILE) {
    this.API_KEY = API_KEY;
    this.MODPACK_INFO_FILE = MODPACK_INFO_FILE;
  }
}
