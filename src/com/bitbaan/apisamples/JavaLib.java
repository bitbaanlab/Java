/*
        Simple class to interact with BitBaan MALab's API.
        https://malab.bitbaan.com
*/


package com.bitbaan.apisamples;

import okhttp3.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class JavaLib {

    private static final String USER_AGENT = "BitBaan-API-Sample-Java";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String server_address;
    private JSONObject unkownerror_json;

    JavaLib(String server_address){
        this.server_address = server_address;
        this.unkownerror_json = new JSONObject();
        try{
            this.unkownerror_json.put("success", false);
            this.unkownerror_json.put("error_code",900);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String get_sha256(String file_path)
    {
        try {
            FileInputStream fis = new FileInputStream(new File(file_path));
            String out_file_sha256 = org.apache.commons.codec.digest.DigestUtils.sha256Hex(fis);
            fis.close();
            return out_file_sha256;
        } catch (IOException e) {
            return "";
        }
    }

    public String get_error(JSONObject return_value) throws JSONException
    {
        String error = "";
        if (return_value.has("error_code"))
            error += ("Error code: "+ return_value.getString("error_code") + "\n");
        if (return_value.has("error_desc"))
            error += ("Error description: " + return_value.getString("error_desc") + "\n");
        if (return_value.has("error_details_code"))
            error += ("Error details code: " + return_value.getString("error_details_code") + "\n");
        if (return_value.has("error_details_desc"))
            error += ("Error details description: " + return_value.getString("error_details_desc") + "\n");
        if (return_value.has("status_code")) {
            error += ("Status code: " + return_value.getString("status_code") + "\n");
            if (return_value.getString("status_code").equals("422") && return_value.has("error")) {
                Iterator<String> keys = return_value.getJSONObject("error").keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    error += ("Validation in: " + key + ", " + return_value.getJSONObject("error").get(key));
                }
            }
        }
        return error;
    }

    public JSONObject call_with_json_input(String api, JSONObject json_input){

        RequestBody body = RequestBody.create(JSON, json_input.toString());

        Request request = new Request.Builder()
                .url(server_address + "/malab/v1/" + api)
                .addHeader("User-Agent", USER_AGENT)
                .post(body)
                .build();

        try{
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }catch(Exception e)
        {
            return unkownerror_json;
        }

    }

    public JSONObject call_with_form_input(String api, JSONObject json_input, String file_param_name, String file_path){
        MultipartBody.Builder multipart = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + file_param_name + "\"" + ";filename=\"file\"" ),
                        RequestBody.create(null, new File(file_path)));

        try{
            Iterator<String> keys = json_input.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                multipart.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, json_input.getString(key)));
            }

            RequestBody body = multipart.build();
            Request request = new Request.Builder()
                    .url(server_address + "/malab/v1/" + api)
                    .addHeader("User-Agent", USER_AGENT)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch(Exception e)
        {
            return unkownerror_json;
        }
    }
}
