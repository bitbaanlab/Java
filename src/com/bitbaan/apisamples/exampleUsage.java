package com.bitbaan.apisamples;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class exampleUsage {

    public static void main(String[] args) throws JSONException {
        System.out.println(" ____  _ _   ____                      __  __    _    _          _     ");
        System.out.println("| __ )(_) |_| __ )  __ _  __ _ _ __   |  \\/  |  / \\  | |    __ _| |__  ");
        System.out.println("|  _ \\| | __|  _ \\ / _` |/ _` | '_ \\  | |\\/| | / _ \\ | |   / _` | '_ \\ ");
        System.out.println("| |_) | | |_| |_) | (_| | (_| | | | | | |  | |/ ___ \\| |__| (_| | |_) |");
        System.out.println("|____/|_|\\__|____/ \\__,_|\\__,_|_| |_| |_|  |_/_/   \\_\\_____\\__,_|_.__/ \n");
        System.out.print("Please insert API server address [Default=https://malab.bitbaan.com]: ");

        Scanner sc=new Scanner(System.in);
        String serveraddress=sc.nextLine();
        if(serveraddress.equals(""))
            serveraddress="https://malab.bitbaan.com";
        System.out.print("Please insert email address: ");
        String email=sc.nextLine();
        System.out.print("Please insert your password: ");
        String password=sc.nextLine();
        JavaLib malab = new JavaLib(serveraddress);
        JSONObject returnValue = malab.login(email, password);
        if(returnValue.getBoolean("success"))
            System.out.println("You are logged in successfully.");
        else
        {
            System.out.printf("error code %d occurred.\n", returnValue.getInt("error_code"));
            sc.close();
            return;
        }
        System.out.print("Please enter the path of file to scan: ");
        String file_path = sc.nextLine();
        File f = new File(file_path);
        returnValue = malab.scan(file_path, f.getName());
        if (returnValue.getBoolean("success"))
            System.out.println("Scan completed successfully.");
        else
        {
            System.out.printf("error code %d occurred.\n", returnValue.getInt("error_code"));
            sc.close();
            return;
        }
        sc.close();
    }
}
