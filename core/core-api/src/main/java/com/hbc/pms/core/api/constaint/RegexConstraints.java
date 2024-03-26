package com.hbc.pms.core.api.constaint;

public class RegexConstraints {
  public static final String MAIL_EXPRESSION =
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

  public static final String PLC_TAG_EXPRESSION = "^%DB(\\d{1,5}):(\\d{1,7})(.([0-7]))?:([a-zA-Z_]+)(\\[(\\d+)])?";
}
