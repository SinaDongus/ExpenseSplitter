package com.de.algorithm.splitter.splitting.sqlite;

import com.de.algorithm.splitter.splitting.config.Expense;
import com.de.algorithm.splitter.splitting.config.UserGroup;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SQLiteHandler {

  @Autowired
  private SQLiteConnector sqLiteConnector;
  @Autowired
  private SQLiteHandlerAssistant sqLiteHandlerAssistant;


  public void connect() {
    sqLiteConnector.startConnection("db.db");
    if (sqLiteConnector.getConnectionStatus()) {
      log.info("DB connection status: {}", sqLiteConnector.getConnectionStatus());
    }
  }

  public void disconnect() {
    log.info("Connection is getting closed...");
    sqLiteConnector.closeConnection();
  }

  public void createTableForUserGroup(UserGroup u) {
    connect();
    if(sqLiteConnector.getConnectionStatus()) {
      sqLiteHandlerAssistant.createExpenseTable(u);
    }
    disconnect();
  }


  public void insertDataForUserGroup(String groupId, Expense expense){
    connect();
    if(sqLiteConnector.getConnectionStatus() ) {
      sqLiteHandlerAssistant.insertExpenseData(groupId, expense);
    }
    disconnect();
  }

  public List<String> getUserGroup(String id) {
    connect();
    return sqLiteHandlerAssistant.getMemberListForId(id);
  }

  public void addUserGroupToDatabase(UserGroup userGroup) {
    connect();
    if(!this.sqLiteConnector.getConnectionStatus()) {
      connect();
    }
    this.sqLiteHandlerAssistant.addUserGroupToDatabase(userGroup);
    disconnect();
  }

}