package com.wistron.javacodebase.common.utils;

public class StringProcessUtils {

  /**
   * 過濾特殊符號 僅會保留英文、數字、空白、底線、減號
   *
   * @param str 輸入字串 e.g. "abc123", "abc@123"
   * @return 過濾後字串 e.g. "abc123" -> "abc123", "abc@123" -> "abc123"
   */
  public static String filterSpecialSymbols(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.replaceAll("[^a-zA-Z0-9\\s_-]", "");
  }
}
