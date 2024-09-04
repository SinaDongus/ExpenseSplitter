package com.de.algorithm.splitter.splitting.sqlite;

import com.de.algorithm.splitter.splitting.config.Expense;
import com.de.algorithm.splitter.splitting.config.UserGroup;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SQLiteHandlerAssistant {

  @Autowired
  private SQLiteConnector connector;

  private Statement stmt = null;
  private boolean tableCreated = false;

  private List<String> databaseMember = new ArrayList<>();

  public boolean getCreated() {
    return this.tableCreated;
  }

  public void insertData(String groupId, Expense expense) {

    try {
      stmt = connector.getConnection().createStatement();

      String name = expense.getName();
      String title = expense.getTitle();
      String cash = String.valueOf(expense.getCash());

      Map<String, Double> nameAndCash = new HashMap<>();

      double partialCash = expense.cash / expense.member.size();

      for (String o : this.databaseMember) {
        nameAndCash.put(o, 0.0);
      }
      for (String n : expense.member) {
        if (n.equals(expense.name)) {
          Double toGet = expense.cash - partialCash;
          nameAndCash.put(n, toGet);
        } else {
          nameAndCash.put(n, -partialCash);
        }
      }

      String firstPart = "insert into " + groupId + " (ID,NAME,TITLE,CASH,";
      for (String s : databaseMember) {
        firstPart = firstPart.concat(s + ",");
      }
      String firstPartFinal = firstPart.substring(0, firstPart.length() - 1);
      firstPartFinal = firstPartFinal.concat(")");
      log.info("first: {}", firstPartFinal);
      final int SHORT_ID_LENGTH = 8;
      String randomId = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
      String secondPart =
          " values ('" + randomId + "','" + name + "','" + title + "'," + cash + ",";
      for (String member : databaseMember) {
        for (Entry<String, Double> s : nameAndCash.entrySet()) {
          if (s.getKey().equals(member)) {
            secondPart = secondPart.concat(s.getValue() + ",");
          }
        }
      }

      String secondPartFinal = secondPart.substring(0, secondPart.length() - 1);
      secondPartFinal = secondPartFinal.concat(");");
      log.info("second: {}", secondPartFinal);
      String sqlStatement = firstPartFinal.concat(secondPartFinal);
      log.info("Final sql Statement: {}", sqlStatement);

      stmt.executeUpdate(sqlStatement);

      log.info("Member names: {}", expense.member);

      stmt.close();
    } catch (SQLException e) {
      log.error("Inserting data for userGroup {} was not possible: {}", groupId, e.getMessage());
    }


  }


  public void createUserGroupTable(UserGroup u) {
    try {
      if (connector.getConnectionStatus()) {
        this.stmt = connector.getConnection().createStatement();
        String firstPart = "CREATE TABLE " + u.groupId;
        String create = firstPart +
            "(ID INT PRIMARY KEY NOT NULL," +
            "NAME TEXT, " +
            "TITLE TEXT, " +
            "CASH REAL)";
        log.info("Create first part: {}", create);
        this.stmt.executeUpdate(create);
        fillUpColumns(u);
//        tableCreated = true;
      } else {
        log.error("No connection to database found - creating table is not possible");
      }
    } catch (SQLException sqlException) {
      log.error("An error occurred: {}", sqlException.getMessage());
    }
  }

  private void fillUpColumns(UserGroup u) throws SQLException {
    for (String s : u.getGroupMember()) {
      String addMember = "ALTER TABLE " + u.groupId + " ADD COLUMN " + s + " REAL";
      log.info("Create alter part: {}", addMember);
      this.databaseMember.add(s);
      this.stmt.executeUpdate(addMember);
    }
  }

}
