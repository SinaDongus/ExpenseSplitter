package com.de.algorithm.splitter.splitting.sqlite;

import com.de.algorithm.splitter.splitting.config.Expense;
import com.de.algorithm.splitter.splitting.config.UserGroup;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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

  public void insertExpenseData(String groupId, Expense expense) {
    log.debug("Start inserting expense data for id '{}'", groupId);
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
      log.info("first String is: {}", firstPartFinal);
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
      log.info("second string is : {}", secondPartFinal);
      String sqlStatement = firstPartFinal.concat(secondPartFinal);
      log.info("Final sql Statement: {}", sqlStatement);

      stmt.executeUpdate(sqlStatement);

      log.info("Member names: {}", expense.member);

      stmt.close();
    } catch (SQLException e) {
      log.error("Inserting data for userGroup {} was not possible: {}", groupId, e.getMessage());
    }
  }


  public boolean createExpenseTable(UserGroup u) {
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
        return true;
      } else {
        log.error("No connection to database found - creating table is not possible");
      }
    } catch (SQLException sqlException) {
      log.error("An error occurred: {}", sqlException.getMessage());
    }
    return false;
  }

  private void fillUpColumns(UserGroup u) throws SQLException {
    for (String s : u.getGroupMember()) {
      String addMember = "ALTER TABLE " + u.groupId + " ADD COLUMN " + s + " REAL";
      log.info("Create alter part: {}", addMember);
      this.databaseMember.add(s);
      this.stmt.executeUpdate(addMember);
    }
  }

  public void addUserGroupToDatabase(UserGroup userGroup) {
    try {
      if (connector.getConnectionStatus()) {
        if (!checkTableExists("userGroups")) {
          createTable();
        }
//        this.stmt = connector.getConnection().createStatement();
//        String inputUserGroup =
//            "insert into userGroups (ID,MEMBER) values ('" + userGroup.groupId + "', '"
//                + userGroup.groupMember + "')";
//        log.debug("Input String: {}", inputUserGroup);
//        this.stmt.executeQuery(inputUserGroup);
        String inputUserGroup = "insert into userGroups (ID, MEMBER) values (?, ?)";
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement(inputUserGroup);
        preparedStatement.setString(1, userGroup.groupId);
        preparedStatement.setObject(2, userGroup.groupMember);

        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      log.error("Creating table for userGroup is not possible: {}", e.getMessage());
    }
  }


  private void createTable() {
    if (connector.getConnectionStatus() && !checkTableExists("userGroups")) {
      try  {
        String input = "create table userGroups (ID TEXT PRIMARY KEY NOT NULL, MEMBER TEXT)";
        try {
          if(connector.getConnectionStatus()) {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement(input);
            preparedStatement.executeUpdate();
            this.tableCreated = true;
          }
        } catch (SQLException e) {
          log.error("Table creation not possible: {}", e.getMessage());
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  String query = "CREATE TABLE userGroups (ID TEXT PRIMARY KEY NOT NULL, MEMBER TEXT)";



  public boolean checkTableExists(String tableName) {
    boolean tableExists = false;
    try {
      DatabaseMetaData metaData = connector.getConnection().getMetaData();
      ResultSet tables = metaData.getTables(null, null, tableName, null);

      while (tables.next()) {
        String table = tables.getString("TABLE_NAME");
        if (table.equalsIgnoreCase(tableName)) {
          tableExists = true;
          break;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tableExists;
  }

  public List<String> getMemberListForId(String id) {
    List<String> memberList = null;

    String getMember = "SELECT MEMBER FROM userGroups WHERE ID = ?";

    try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(getMember)) {

      preparedStatement.setString(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      String member = null;
      while (resultSet.next()) {
        member = resultSet.getString("MEMBER");
//        memberList.add(member);
      }
      assert member != null;
      member = member.replace("[", "");
      member = member.replace("]", "");
      member = member.replace(" ", "");
      memberList = new ArrayList<String>(Arrays.asList(member.split(",")));

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return memberList;
  }
}

