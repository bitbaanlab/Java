package com.bitbaan.apisamples;

import java.util.Scanner;

public class exampleUsage {
    public static void main(String[] args) {
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
    }
}
