package com.de.algorithm.splitter.splitting.sqlite;

import com.de.algorithm.splitter.splitting.config.Expense;
import com.de.algorithm.splitter.splitting.config.UserGroup;
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

  public void createTableForUserGroup(UserGroup u) {
    if(sqLiteConnector.getConnectionStatus()) {
      sqLiteHandlerAssistant.createUserGroupTable(u);
    }
  }


  public void insertDataForUserGroup(String groupId, Expense expense){
    if(sqLiteConnector.getConnectionStatus() ) {
      sqLiteHandlerAssistant.insertData(groupId, expense);
    }
  }

}