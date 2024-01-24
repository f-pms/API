package com.hbc.pms.core.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String[] splitAddress(String address) {
//        Pattern addressPattern = Pattern.compile("^%DB(\\d+):(\\d+):(REAL|INT|BOOL)$", Pattern.CASE_INSENSITIVE);
//        Matcher scAddress = addressPattern.matcher(address);

//        if (!scAddress.find()) {
//            throw new ValidationException(Collections.singletonList(new ErrorMessage("Invalid PLC address")));
//        }
        return address.split(":");
    }
}
