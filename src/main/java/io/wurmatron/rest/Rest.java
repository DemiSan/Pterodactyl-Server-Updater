package io.wurmatron.rest;

import io.wurmatron.Updater;
import io.wurmatron.rest.json.Validation;
import io.wurmatron.utils.ServerLinker;
import io.wurmatron.utils.UserInput;

public class Rest {

  public static String baseURL;
  public static String authKey;

  public String restAPIVersion;

  public Rest(String baseURL, String authKey) {
    if (baseURL != null && !baseURL.isEmpty() && authKey != null && !authKey.isEmpty()) {
      Validation validation = RequestGenerator.Status.getValidation();
      if (validation != null) {
        restAPIVersion = validation.version;
        System.out.println("Connected to rest, Rest API v" + restAPIVersion);
      } else {
        System.out.println("Failed to validate with rest API!");
        configRestSettings();
      }
    } else {
      configRestSettings();
    }
    if (Updater.config.servers.isEmpty()) {
      ServerLinker.addRestData(ServerLinker.linkUUIDToName(Updater.ptero, Updater.rest));
    }
  }

  private void configRestSettings() {
    System.out.println("Rest settings have not been set or are invalid!");
    baseURL = UserInput.askAndGetInput("Enter the rest URL: ").replaceAll(" ", "");
    authKey = UserInput.askAndGetInput("Enter the rest user login (username:password): ");
    Updater.config.restBaseURL = baseURL;
    Updater.config.restAuthToken = authKey;
    Updater.config.save();
  }
}
