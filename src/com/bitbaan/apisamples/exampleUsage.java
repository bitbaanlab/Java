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
        System.out.print("Please insert API server address [Default=https://apimalab.bitbaan.com]: ");

        Scanner sc=new Scanner(System.in);
        String serveraddress=sc.nextLine();
        if(serveraddress.equals(""))
            serveraddress="https://apimalab.bitbaan.com";
        System.out.print("Please insert identifier (username, phone no or email): ");
        String identifier=sc.nextLine();
        System.out.print("Please insert your password: ");
        String password=sc.nextLine();
        JavaLib malab = new JavaLib(serveraddress);
        JSONObject params1 = new JSONObject();
            params1.put("identifier", identifier);
            params1.put("password", password);
        JSONObject return_value = malab.call_with_json_input("user/login", params1);
        if(return_value.getBoolean("success"))
            System.out.println("You are logged in successfully.");
        else
        {
            System.out.println(malab.get_error(return_value));
            sc.close();
            return;
        }
        System.out.print("Please enter the path of file to scan: ");
        String file_path = sc.nextLine();
        File f = new File(file_path);
        String file_name = f.getName();
        String apikey = return_value.getString("apikey");
        JSONObject params2 = new JSONObject();
        params2.put("file_name", file_name);
        params2.put("apikey", apikey);
        return_value = malab.call_with_form_input("file/scan", params2, "file_data", file_path);
        if (return_value.getBoolean("success"))
        {
            //getting scan results:
            boolean is_finished = false;
            String file_hash = malab.get_sha256(file_path);
            while(!is_finished)
            {
                System.out.println("Waiting for getting results...");
                JSONObject params3 = new JSONObject();
                params3.put("hash", file_hash);
                params3.put("apikey", apikey);
                return_value = malab.call_with_json_input("file/scan/result/get", params3);
                if(!return_value.getBoolean("success")){
                    System.out.printf(malab.get_error(return_value));
                    return;
                }
                cls();
                JSONArray results_arr = return_value.getJSONObject("scan").getJSONArray("results");
                for(int i = 0; i<results_arr.length(); i++){
                    if(results_arr.getJSONObject(i).getString("result").equals("malware"))  // file is malware
                        System.out.printf("%s ==> %s\n", results_arr.getJSONObject(i).getString("av_name"), results_arr.getJSONObject(i).getString("malware_name"));
                    else if (results_arr.getJSONObject(i).getString("result").equals("clean"))  // file is clean
                        System.out.printf("%s ==> %s\n", results_arr.getJSONObject(i).getString("av_name"), "Clean");
                }
                is_finished = return_value.getJSONObject("scan").getBoolean("is_finished");
                sleep(2000);
            }
        }
        else
        {
            System.out.printf(malab.get_error(return_value));
            sc.close();
            return;
        }
        sc.close();
    }
}
