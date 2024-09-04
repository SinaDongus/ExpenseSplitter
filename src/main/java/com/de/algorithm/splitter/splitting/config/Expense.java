package com.de.algorithm.splitter.splitting.config;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class Expense {

  public final String name;
  public final String title;
  public final List<String> member;
  public final double cash;

  private final Date date = new Date();

  public Expense(String name, String title, List<String> member, double cash) {

    this.name = name;
    this.title = title;
    this.member = member;
    this.cash = cash;
  }


}

