package com.de.algorithm.splitter.splitting;

import com.de.algorithm.splitter.splitting.sqlite.SQLiteConnector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SumUp {

  private final Map<String, Double> finalCalculation = new HashMap<>();
  private final Map<String, Double> finalToPayMap = new HashMap<>();
  private final Map<String, Double> finalToGetMap = new HashMap<>();
  private final Map<String, Double> result = new HashMap<>();
  @Autowired
  private SQLiteConnector sqLiteConnector;

  public void sum(String groupId) throws SQLException {
    if (!this.sqLiteConnector.getConnectionStatus()) {
      sqLiteConnector.startConnection("db.db");
    }
    Statement stmt = this.sqLiteConnector.getConnection().createStatement();

    List<String> columns = new ArrayList<>();
    String s = "select * from " + groupId + " LIMIT 0";
    ResultSet rs = stmt.executeQuery(s);
    ResultSetMetaData mrs = rs.getMetaData();
    for (int i = 5; i <= mrs.getColumnCount(); i++) {
      columns.add(mrs.getColumnLabel(i));
    }
    log.info("Column names: {}", columns);

    for (String name : columns) {
      Double finalToPay = 0.0;
      log.info("Name test: {}", name);
      String getCashPerPerson = "Select " + name + " from " + groupId + ";";
      ResultSet result = stmt.executeQuery(getCashPerPerson);
      while(result.next()) {
        finalToPay += result.getDouble(name);
      }
      finalCalculation.put(name, finalToPay);
      log.info("Final to pay check: {}", finalCalculation);
    }
  }

  public void balance() {

    setUpFinalPayAndReceiveMaps();
    checkBeforeBalance();

    for (Entry<String, Double> pay : this.finalToPayMap.entrySet()) {
      if (pay.getValue() > 0.0) {
        for (Entry<String, Double> get : this.finalToGetMap.entrySet()) {
          if (get.getValue() > 0.0 && pay.getValue() > 0.0) {
            fillMap(pay, get);
          }
        }
      }
    }
    log.info("Result: {}", this.result);
  }

  private void fillMap(Entry<String, Double> pay, Entry<String, Double> get) {
    String resultString = pay.getKey() + " -> " + get.getKey();
    Double resultPay;
    if (pay.getValue() > get.getValue()) {
      resultPay = get.getValue();
      this.finalToPayMap.replace(pay.getKey(), pay.getValue(), pay.getValue() - get.getValue());
      this.finalToGetMap.replace(get.getKey(), get.getValue(), 0.0);

    } else {
      resultPay = pay.getValue();
      this.finalToGetMap.replace(get.getKey(), get.getValue(), get.getValue() - pay.getValue());
      this.finalToPayMap.replace(pay.getKey(), pay.getValue(), 0.0);
    }
    this.result.put(resultString, resultPay);
  }


  private void setUpFinalPayAndReceiveMaps() {
    for (Entry<String, Double> entry : this.finalCalculation.entrySet()) {
      if (entry.getValue() < 0) {
        this.finalToPayMap.put(entry.getKey(), Math.abs(entry.getValue()));
      } else {
        this.finalToGetMap.put(entry.getKey(), entry.getValue());
      }
    }
    finalToGetMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .forEach(System.out::println);
    finalToPayMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .forEach(System.out::println);
  }

  private void checkBeforeBalance() {

    for (Entry<String, Double> pay : this.finalToPayMap.entrySet()) {
      for (Entry<String, Double> get : this.finalToGetMap.entrySet()) {
        if (pay.getValue().equals(get.getValue())) {
          String resultString = pay.getKey() + " -> " + get.getKey();
          this.result.put(resultString, pay.getValue());
          this.finalToGetMap.replace(get.getKey(), get.getValue(), 0.0);
          this.finalToPayMap.replace(get.getKey(), get.getValue(), 0.0);
        }
      }
    }
  }


}
