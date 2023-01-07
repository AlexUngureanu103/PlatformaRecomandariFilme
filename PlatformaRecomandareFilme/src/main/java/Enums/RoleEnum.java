package Enums;

public enum RoleEnum {
    Administrator("admin"),
    Moderator("mod"),
    RegularUser("user");

    private String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
