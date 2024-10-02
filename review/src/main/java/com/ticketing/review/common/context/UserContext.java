package com.ticketing.review.common.context;

public class UserContext {

  private static final ThreadLocal<Long> userId = new ThreadLocal<>();
  private static final ThreadLocal<String> userEmail = new ThreadLocal<>();
  private static final ThreadLocal<String> userRole = new ThreadLocal<>();

  public static Long getUserId() {
    return userId.get();
  }

  public static void setUserId(Long id) {
    userId.set(id);
  }

  public static String getUserEmail() {
    return userEmail.get();
  }

  public static void setUserEmail(String email) {
    userEmail.set(email);
  }

  public static String getUserRole() {
    return userRole.get();
  }

  public static void setUserRole(String role) {
    userRole.set(role);
  }

  public static void clear() {
    userId.remove();
    userEmail.remove();
    userRole.remove();
  }
}
