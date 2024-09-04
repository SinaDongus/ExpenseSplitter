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

  @Autowired
  private UserGroup userGroup;

  public void setupUserGroup(String shortId, List<String> participants) {
//    UserGroup usergroup = new UserGroup();

    userGroup.setGroupId(shortId);
    userGroup.setGroupMember(participants);
    NewUserGroupEvent newUserGroup = new NewUserGroupEvent(this, userGroup);
    applicationEventPublisher.publishEvent(newUserGroup);

  }

}
