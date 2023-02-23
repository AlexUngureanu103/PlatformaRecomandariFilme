package Enums;

public enum RoleEnum {
    admin(2),
    user(1),
    guest(0);

    private Integer value;

    RoleEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
