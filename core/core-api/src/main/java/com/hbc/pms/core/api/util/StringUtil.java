package com.hbc.pms.core.api.util;

import static com.hbc.pms.core.api.constaint.RegexConstraints.PLC_TAG_EXPRESSION;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

  public static boolean isIncorrectPLCAddressFormat(String address) {
    Pattern pattern = Pattern.compile(PLC_TAG_EXPRESSION);
    // Copied from S7Tag.java

    Matcher matcher = pattern.matcher(address);
    return !matcher.matches();
  }

  public static String buildCronFromSeconds(int seconds) {
    int minutes = seconds / 60;
    seconds = seconds % 60;

    return MessageFormat.format("*/{0} *{1} * * * *", seconds, minutes != 0 ? "/" + minutes : "");
  }

  public static boolean isStringEncoded(String stringToCheck) {
    Pattern bcryptPattern = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");
    return bcryptPattern.matcher(stringToCheck).matches();
  }
}
