package org.geektimes.projects.user.enums;

public enum UserType { // 底层实际 public final class UserType extends java.lang.Enum

    NORMAL,
    VIP;

     UserType(){ // 枚举中构造器是 private

    }

    public static void main(String[] args) {
        UserType.VIP.ordinal();
    }
}
