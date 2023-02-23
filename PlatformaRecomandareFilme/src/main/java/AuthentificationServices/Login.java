package AuthentificationServices;

import Enums.RoleEnum;
import account.Account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Login implements ILogin {
    File accountsData;

    public Login(File accountsData) {
        this.accountsData = accountsData;
    }

    public Account login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter you username : ");
        String username = scanner.nextLine();

        System.out.print("Enter your password : ");
        String password = scanner.nextLine();
        Account auxiliaryAccount = checkCredentials(username, password);
        if (auxiliaryAccount.getRole() == RoleEnum.guest) {
            System.out.println("Access denied ! , but u can continue as a guest");
        } else {
            System.out.println("Access granted !");
        }
        return auxiliaryAccount;
    }

    public Account checkCredentials(String username, String password) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(accountsData));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    bufferedReader.close();
                    return new Account(parts[0], parts[2], parts[1], parts[4], Integer.parseInt(parts[3]));
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Account();
    }

    public Account logout() {
        return new Account();
    }
}
