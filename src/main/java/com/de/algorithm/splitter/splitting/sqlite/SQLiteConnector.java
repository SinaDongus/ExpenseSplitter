package com.de.algorithm.splitter.splitting.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SQLiteConnector {
  private Connection connection;
  private boolean connected = false;

  public void startConnection(String databaseName) {
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
      connected = true;
      log.debug("Connection to database with name {} successfully established", databaseName);
    } catch (SQLException e) {
      log.error("An error occurred during database connection establishing process: {}", e.getMessage());
    }
  }

    public void closeConnection() {
    try {
      if (connection != null) {
        connection.close();
        connected = false;
        log.debug("Connection closed.");
      }
    } catch (SQLException e) {
      log.error("An error occurred during database connection closing process: {}", e.getMessage());
    }
  }

  public boolean getConnectionStatus() {
    return this.connected;
  }

  public Connection getConnection() {
    return this.connection;
  }

}
