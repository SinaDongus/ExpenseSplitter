package com.de.algorithm.splitter.splitting.input;

import com.de.algorithm.splitter.splitting.config.UserGroup;
import com.de.algorithm.splitter.splitting.events.NewUserGroupEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserHandling {
  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  public String setupUserGroup(String shortId, List<String> participants) {
    UserGroup usergroup = new UserGroup();

    usergroup.setGroupId(shortId);
    usergroup.setGroupMember(participants);
    NewUserGroupEvent newUserGroup = new NewUserGroupEvent(this, usergroup);
    applicationEventPublisher.publishEvent(newUserGroup);
    return usergroup.groupId;
  }

}
