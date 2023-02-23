package AuthentificationServices;

import account.Account;

public interface ILogin {
    Account login();

    Account checkCredentials(String username, String password);
}
