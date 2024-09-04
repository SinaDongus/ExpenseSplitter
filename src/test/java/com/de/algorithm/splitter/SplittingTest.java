package com.de.algorithm.splitter;

import com.de.algorithm.splitter.splitting.InputManager;
import com.de.algorithm.splitter.splitting.config.IncludedMembers;
import com.de.algorithm.splitter.splitting.input.SplittingManager;
import com.de.algorithm.splitter.splitting.input.UserHandling;
import com.de.algorithm.splitter.splitting.sqlite.SQLiteHandler;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class SplittingTest {

  @Autowired
  private SplittingManager splittingManager;

  @Autowired
  private SQLiteHandler sqLiteHandler;

  @Autowired
  private InputManager inputManager;

  @Autowired
  private UserHandling userHandling;

  private String shortId = null;

  @Test
  void dataBaseConnectionTest() {
    sqLiteHandler.connect();
    final int SHORT_ID_LENGTH = 8;
    shortId = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
//    sqLiteHandler.createTableForUserGroup(shortId);
//    sqLiteHandler.insertDataforUserGroup(shortId, "data");
  }

  @Test
  void setupUserGroupTest() {
    List<String> p = Arrays.asList("P", "N", "U", "S");
//    inputManager.inputUserGroup(p);
    final int SHORT_ID_LENGTH = 8;
    this.shortId = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    userHandling.setupUserGroup(this.shortId, p);
  }


  @Test
  void inputExpenses() {
    List<String> p = Arrays.asList("Petra", "Nico", "Uli", "Sina");
    final int SHORT_ID_LENGTH = 8;
    this.shortId = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    userHandling.setupUserGroup(this.shortId, p);
    this.splittingManager.generateParticipantGroup(p);
    this.splittingManager.trackExpense(shortId, "Uli", "title1", IncludedMembers.ALL, 20.00);
    this.splittingManager.trackExpense(shortId, "Petra", "title2", IncludedMembers.ALL, 24.00);
    this.splittingManager.trackExpense(shortId, "Nico", "title3", IncludedMembers.ALL, 16.00);
    List<String> list = Arrays.asList("Petra", "Nico", "Sina");
    this.splittingManager.trackExpense(shortId, "Sina", "title4", list, 21.00);
    List<String> list2 = Arrays.asList("Uli", "Petra");
    this.splittingManager.trackExpense(shortId, "Uli", "title5", list2, 2.00);
    this.splittingManager.split(shortId);
//    this.splittingManager.sum(shortId);
  }


}
