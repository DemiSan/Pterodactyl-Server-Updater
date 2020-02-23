package io.wurmatron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Updater {

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

  public static void main(String[] args) {

  }
}
