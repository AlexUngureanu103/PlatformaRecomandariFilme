package AuthentificationServices;

import java.io.*;
import java.util.Scanner;

public class Register implements IRegister {
    File accountsData;

    public Register(File accountsData) {
        this.accountsData = accountsData;
    }

    public void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Register :");
        System.out.print("Enter an username : ");
        String username = scanner.nextLine();
        if (checkUserExistent(username)) {
            System.out.println("Username already existent . Please try again");
            return;
        }

        System.out.print("Enter a nickname : ");
        String nickname = scanner.nextLine();

        System.out.print("Enter your age : ");
        Integer age = scanner.nextInt();
        if (age < 0 || age > 150) {
            System.out.println("Invalid age . Please try again");
            return;
        }
        scanner.nextLine();

        System.out.print("Enter a password : ");
        String password = scanner.nextLine();

        System.out.print("Confirm password : ");
        String confirmPassword = scanner.nextLine();

        if (password.equals(confirmPassword)) {
            try {
                System.out.println("Registration successful! , Your username is :" + username + " and your password is :" + password);
                FileWriter fileWriter = new FileWriter(accountsData, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(username + ":" + password + ":" + nickname + ":" + age + ":" + "user");
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Password do not match . Please try  again.");
        }

    }

    public boolean checkUserExistent(String username) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(accountsData));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    bufferedReader.close();
                    return true;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
