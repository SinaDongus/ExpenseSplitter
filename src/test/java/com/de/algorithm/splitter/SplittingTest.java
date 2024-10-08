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


  @Test
  void setupUserGroupTest() {
    List<String> p = Arrays.asList("P", "N", "U", "S");
//    inputManager.inputUserGroup(p);
    final int SHORT_ID_LENGTH = 8;
    String id = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    userHandling.setupUserGroup(id, p);
  }


  @Test
  void allAroundTest() {
    List<String> p = Arrays.asList("P", "N", "U", "S");
    final int SHORT_ID_LENGTH = 8;
    String shortId = RandomStringUtils.randomAlphanumeric(SHORT_ID_LENGTH);
    this.splittingManager.setupUserGroup(shortId, p);
    log.info("used id is_ {}", shortId);
//    this.splittingManager.generateParticipantGroup(p);
    this.splittingManager.trackExpense(shortId, "U", "title1", IncludedMembers.ALL, 20.00);
    this.splittingManager.trackExpense(shortId, "P", "title2", IncludedMembers.ALL, 24.00);
    this.splittingManager.trackExpense(shortId, "N", "title3", IncludedMembers.ALL, 16.00);
    List<String> list = Arrays.asList("P", "N", "S");
    this.splittingManager.trackExpense(shortId, "S", "title4", list, 21.00);
    List<String> list2 = Arrays.asList("U", "P");
    this.splittingManager.trackExpense(shortId, "U", "title5", list2, 2.00);
    this.splittingManager.split(shortId);
//    this.splittingManager.sum(shortId);
  }


}
