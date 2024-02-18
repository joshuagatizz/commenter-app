package com.commenter.service;

import com.commenter.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUser(), DatabaseConfig.getPassword());
  }

  public List<Map<String, Object>> executeQuery(String query, Object... params) {
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

      setParameters(statement, params);
      try (ResultSet resultSet = statement.executeQuery()) {
        List<Map<String, Object>> resultData = new ArrayList<>();
        while (resultSet.next()) {
          Map<String, Object> row = new HashMap<>();
          for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            String columnName = resultSet.getMetaData().getColumnName(i);
            Object value = resultSet.getObject(i);
            row.put(columnName, value);
          }
          resultData.add(row);
        }
        return resultData;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to execute query", e);
    }
  }

  public int executeUpdate(String query, Object... params) {
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

      setParameters(statement, params);

      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to execute update", e);
    }
  }

  private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      statement.setObject(i + 1, params[i]);
    }
  }
}
