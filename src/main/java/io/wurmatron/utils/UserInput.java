package io.wurmatron.utils;

import static io.wurmatron.Updater.SCANNER;

public class UserInput {

  public static String askAndGetInput(String message) {
    System.out.print(message);
    return SCANNER.nextLine();
  }

}
