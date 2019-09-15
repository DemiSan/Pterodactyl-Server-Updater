package com.wurmatron.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

  public static final String[] readFile(File file) {
    if (file.exists()) {
      try {
        return Files.readAllLines(file.toPath()).toArray(new String[0]);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Unable to load '" + file.getAbsolutePath() + "'");
    }
    return new String[0];
  }

}
