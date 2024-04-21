package com.hbc.pms.core.api.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageConstant {
  public static final String BAD_CREDENTIALS_EXCEPTION = "Tên đăng nhập hoặc mật khẩu không hợp lệ";
  public static final String ALARM_CONDITION_NOT_FOUND =
      "Không thể tìm thấy điều kiện cảnh báo với id: ";
  public static final String ALARM_ACTION_NOT_FOUND =
      "Không thể tìm thấy phương thức cảnh báo với id: ";
  public static final String EXISTED_ALARM_ACTION_TYPE =
      "Loại phương thước cảnh báo này đã tồn tại";
  public static final String CANNOT_CHANGE_ALARM_ACTION_TYPE =
      "Không thể thay đổi phương thức cảnh báo này";
  public static final String CANNOT_CHANGE_ALARM_CONDITION_TYPE =
      "Không thể thay đổi điều kiện cảnh báo này";
  public static final String BLUEPRINT_NOT_FOUND = "Blueprint không tồn tại với id: ";
  public static final String EXISTED_EMAIL = "Email đã tồn tại";
  public static final String EXISTED_USERNAME = "Tên đăng nhập đã tồn tại";
  public static final String ONLY_UPDATE_YOUR_OWN_USER =
      "Bạn chị có thể cập nhật thông tin cá nhân của mình";
  public static final String BOTH_NEW_AND_OLD_PASS_MUST_BE_PRESENT_OR_ABSENT =
      "Mật khẩu mới và cũ phải đồng thời được nhập hoặc để trống";
  public static final String CURRENT_PASS_IS_NOT_CORRECT = "Sai mật khẩu hiện tại";
  public static final String YOU_CAN_NOT_DELETE_YOUR_OWN = "Bạn không thể xóa tài khoản chính mình";
  public static final String EXISTED_ALARM_SENSOR_CONFIGURATION =
      "Đã tồn tại thiết lập cảm biến cảnh báo với id: ";
  public static final String DOWNLOAD_EXCEL_FILES_FAILDED = "Tải báo cáo thất bại";
}
