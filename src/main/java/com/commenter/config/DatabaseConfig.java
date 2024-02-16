package com.commenter.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
  private static final Properties properties = new Properties();

  static {
    try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load application properties", e);
    }
  }

  public static String getUrl() {
    return properties.getProperty("database.url");
  }

  public static String getUser() {
    return properties.getProperty("database.user");
  }

  public static String getPassword() {
    return properties.getProperty("database.password");
  }
}
