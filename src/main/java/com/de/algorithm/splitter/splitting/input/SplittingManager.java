package com.de.algorithm.splitter.splitting.input;

import com.de.algorithm.splitter.splitting.SumUp;
import com.de.algorithm.splitter.splitting.config.Expense;
import com.de.algorithm.splitter.splitting.config.IncludedMembers;
import com.de.algorithm.splitter.splitting.config.UserGroup;
import com.de.algorithm.splitter.splitting.events.NewUserGroupEvent;
import com.de.algorithm.splitter.splitting.sqlite.SQLiteHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SplittingManager {

  @Autowired
  private SumUp sumup;

  @Autowired
  private SQLiteHandler sqLiteHandler;


//  private List<String> officalMemberList = new ArrayList<>();

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  private UserGroup userGroup;

  public void setupUserGroup(String shortId, List<String> participants) {
    userGroup.setGroupId(shortId);
    userGroup.setGroupMember(participants);
    sqLiteHandler.addUserGroupToDatabase(userGroup);
    NewUserGroupEvent newUserGroup = new NewUserGroupEvent(this, userGroup);
    applicationEventPublisher.publishEvent(newUserGroup);

  }


  public void trackExpense(String id, String name, String title, IncludedMembers member,
      double cash) {
//    get usergroup by id
    List<String> groupMember = sqLiteHandler.getUserGroup(id);
    List<String> expenseParticipants = new ArrayList<>();
    if (member.equals(IncludedMembers.ALL)) {
//      expenseParticipants = this.officalMemberList;
      expenseParticipants = groupMember;
    } else if (member.equals(IncludedMembers.JUST_ME)) {
      expenseParticipants.add(name);
    }
    Expense e = buildExpense(name, title, expenseParticipants, cash);

    sqLiteHandler.insertDataForUserGroup(id, e);

  }

  public void trackExpense(String id, String name, String title, List<String> expenseParticipants,
      double cash) {
    Expense e = buildExpense(name, title, expenseParticipants, cash);
    sqLiteHandler.insertDataForUserGroup(id, e);

    log.info("done");
  }

  private Expense buildExpense(String name, String title, List<String> expenseParticipants,
      double cash) {
    return new Expense(name, title, expenseParticipants, cash);
  }

//  public void generateParticipantGroup(List<String> p) {
//    this.officalMemberList = p;
//  }

  public void split(String id) {
    try {
      this.sumup.sum(id);
    } catch (SQLException e) {
      log.error("An error occurred during splitting: {}", e.getMessage());
    }
    this.sumup.balance();
  }
}
