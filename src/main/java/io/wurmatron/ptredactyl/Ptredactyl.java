package io.wurmatron.ptredactyl;

import com.stanjg.ptero4j.PteroAdminAPI;
import com.stanjg.ptero4j.PteroUserAPI;
import io.wurmatron.Updater;
import io.wurmatron.utils.UserInput;

public class Ptredactyl {

  public static PteroAdminAPI adminAPI;
  public static PteroUserAPI userAPI;


  public Ptredactyl(String baseURL, String userAPIKey, String adminAPIKey) {
    if (baseURL != null && !baseURL.isEmpty() && userAPIKey != null && !userAPIKey.isEmpty()
        && adminAPIKey != null && !adminAPIKey.isEmpty()) {
      try {
        userAPI = new PteroUserAPI(baseURL, userAPIKey);
        adminAPI = new PteroAdminAPI(baseURL, adminAPIKey);
      } catch (Exception e) {
        System.out.println("Invalid API Token's");
        String[] apiKeys = collectTokens();
        userAPIKey = apiKeys[0];
        adminAPIKey = apiKeys[1];
        Updater.config.userAuthKey = userAPIKey;
        Updater.config.adminAuthKey = adminAPIKey;
        Updater.config.save();
      }
    } else {
      System.out.println("Pterodactyl settings have not been set or are invalid!");
      baseURL = UserInput.askAndGetInput("Enter the panel URL: ");
      String[] apiKeys = collectTokens();
      userAPIKey = apiKeys[0];
      adminAPIKey = apiKeys[1];
      Updater.config.baseURL = baseURL;
      Updater.config.userAuthKey = userAPIKey;
      Updater.config.adminAuthKey = adminAPIKey;
      Updater.config.save();
    }
  }

  // 0 = User API Key
  // 1 = Admin API Key
  public String[] collectTokens() {
    return new String[]{
        UserInput.askAndGetInput("Enter the Pterodactyl User API Key: "),
        UserInput.askAndGetInput("Enter the Pterodactyl Admin API Key: ")};
  }
}
