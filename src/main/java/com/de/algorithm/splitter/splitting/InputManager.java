package com.de.algorithm.splitter.splitting;

import com.de.algorithm.splitter.splitting.events.NewUserGroupEvent;
import com.de.algorithm.splitter.splitting.input.UserHandling;
import com.de.algorithm.splitter.splitting.sqlite.SQLiteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class InputManager implements ApplicationListener<NewUserGroupEvent> {

  @Autowired
  UserHandling userHandling;

  @Autowired
  SQLiteHandler sqLiteHandler;

  @Override
  public void onApplicationEvent(NewUserGroupEvent newUserGroup) {
    sqLiteHandler.connect();
    if(!newUserGroup.getUsergroup().stored) {
      sqLiteHandler.createTableForUserGroup(newUserGroup.getUsergroup());
    }
  }

  @Override
  public boolean supportsAsyncExecution() {
    return ApplicationListener.super.supportsAsyncExecution();
  }
}
