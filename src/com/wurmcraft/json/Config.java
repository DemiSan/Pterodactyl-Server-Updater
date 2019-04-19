package com.wurmcraft.json;

import java.util.HashMap;

public class Config {

  // Panel
  public String panelURL;
  public String adminApiKey;
  public String userApiKey;
  public String modpackUpdateInfo;
  public HashMap<String, String[]> serverLoginInfo;

  public Config(String panelURL, String adminApiKey, String userApiKey,
      String modpackUpdateInfo) {
    this.panelURL = panelURL;
    this.adminApiKey = adminApiKey;
    this.userApiKey = userApiKey;
    this.modpackUpdateInfo = modpackUpdateInfo;
    this.serverLoginInfo = new HashMap<>();
  }
}