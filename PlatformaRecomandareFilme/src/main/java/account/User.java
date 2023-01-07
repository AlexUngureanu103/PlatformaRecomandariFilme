package account;

import Enums.RoleEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public abstract class User {
    protected String username;
    protected String nickname;
    protected String password;
    protected RoleEnum role;
    protected Integer age;
}
