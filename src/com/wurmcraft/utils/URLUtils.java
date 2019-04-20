package com.wurmcraft.utils;

import com.wurmcraft.ServerUpdater;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class URLUtils {

  public static final String USER_AGENT = "serverUpdater";

  public static <T extends Object> T get(String url, Class<T> type) {
    if (url != null && !url.isEmpty()) {
      return ServerUpdater.gson.fromJson(toString(url), type);
    }
    return null;
  }

  public static String toString(String url) {
    if (url != null && !url.isEmpty()) {
      try {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine + "\n");
        }
        in.close();
        return response.toString();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static void download(String sourceURL, String saveDirectory) {
    try {
      URL url = new URL(sourceURL);
      String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1);
      File targetPath = new File(saveDirectory + File.separator + fileName);
      if (!targetPath.getParentFile().exists()) {
        targetPath.getParentFile().mkdirs();
      }
      if (!targetPath.exists()) {
        targetPath.createNewFile();
      }
      Files.copy(url.openStream(), targetPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
