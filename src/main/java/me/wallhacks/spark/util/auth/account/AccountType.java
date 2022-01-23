package me.wallhacks.spark.util.auth.account;

public enum AccountType {
    MICROSOFT("MIRCOSOFT"),
    MOJANG("MOJANG"),
    CRACKED("CRACKED");

    private final String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
