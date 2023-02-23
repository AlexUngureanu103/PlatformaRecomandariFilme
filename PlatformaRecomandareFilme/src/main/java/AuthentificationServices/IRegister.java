package AuthentificationServices;

public interface IRegister {

    void register();

    boolean checkUserExistent(String username);
}
