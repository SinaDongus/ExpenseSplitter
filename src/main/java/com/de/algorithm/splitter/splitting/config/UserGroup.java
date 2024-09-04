package com.de.algorithm.splitter.splitting.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class UserGroup {

  public List<String> groupMember = new ArrayList<>();

  public String groupId;
  public boolean stored = false;

  public List<String> getGroupMember() {
    return this.groupMember;
  }

}
