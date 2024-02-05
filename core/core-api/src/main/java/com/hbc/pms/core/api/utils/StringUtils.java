package com.hbc.pms.core.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

  public static boolean isIncorrectPLCAddressFormat(String address) {
    Pattern pattern =
        Pattern.compile("^%DB(\\d{1,5}):(\\d{1,7})(.([0-7]))?:([a-zA-Z_]+)(\\[(\\d+)])?");
    // Copied from S7Tag.java

    Matcher matcher = pattern.matcher(address);
    return !matcher.matches();
  }
}
