package com.de.algorithm.splitter.splitting.events;

import com.de.algorithm.splitter.splitting.config.UserGroup;
import org.springframework.context.ApplicationEvent;

public class NewUserGroupEvent extends ApplicationEvent {
  private final UserGroup usergroup;

  public NewUserGroupEvent(Object source, UserGroup usergroup) {
    super(source);
    this.usergroup = usergroup;
  }

  public UserGroup getUsergroup() {
    return usergroup;
  }
}
