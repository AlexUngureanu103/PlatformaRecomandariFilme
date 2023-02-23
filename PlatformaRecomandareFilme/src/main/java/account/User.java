package account;

import Enums.RoleEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class User {
    protected String username;
    protected String nickname;
    protected String password;
    protected RoleEnum role;
    protected Integer age;

    public User() {
        username = "guest";
        nickname = "guest";
        password = "";
        age = 18;
        role = RoleEnum.guest;
    }

    public User(String username, String nickname, String password, String role, Integer age) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = RoleEnum.valueOf(role);
        this.age = age;
    }
}
