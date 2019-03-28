package com.bitbaan.apisamples;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

public class exampleUsage {

    private static void sleep(int dwMilliseconds)
    {
        try{
            Thread.sleep(dwMilliseconds);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void cls() {
        try{
            final String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

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
        {
            //getting scan results:
            boolean is_finished = false;
            String file_hash = malab.get_sha256(file_path);
            int scan_id = returnValue.getInt("scan_id");
            while(!is_finished)
            {
                System.out.println("Waiting for getting results...");
                returnValue = malab.results(file_hash, scan_id);
                if(!returnValue.getBoolean("success")){
                    System.out.printf("error code %d occurred.\n", returnValue.getInt("error_code"));
                    return;
                }
                cls();
                JSONArray results_arr = returnValue.getJSONArray("results");
                for(int i = 0;i<results_arr.length();i++){
                    if(results_arr.getJSONObject(i).getInt("result_state") == 32)  // file is malware
                        System.out.printf("%s ==> %s\n", results_arr.getJSONObject(i).getString("av_name"), results_arr.getJSONObject(i).getString("virus_name"));
                    else if (results_arr.getJSONObject(i).getInt("result_state") == 33)  // file is clean
                        System.out.printf("%s ==> %s\n", results_arr.getJSONObject(i).getString("av_name"), "Clean");
                }
                is_finished = returnValue.getBoolean("is_finished");
                sleep(2000);
            }
        }
        else
        {
            System.out.printf("error code %d occurred.\n", returnValue.getInt("error_code"));
            sc.close();
            return;
        }
        sc.close();
    }
}
