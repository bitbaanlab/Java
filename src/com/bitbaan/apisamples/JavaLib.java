/*
        Simple class to interact with BitBaan MALab's API.
        https://malab.bitbaan.com
*/


package com.bitbaan.apisamples;

import org.json.*;
import okhttp3.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class JavaLib {

    static final String USER_AGENT = "BitBaan-API-Sample-Java";
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String server_address;
    private String api_key;
    private JSONObject unkownerror_json;

    JavaLib(String server_address, String api_key){
        this.server_address = server_address;
        this.api_key = api_key;
        this.unkownerror_json = new JSONObject();
        try{
            this.unkownerror_json.put("success", false);
            this.unkownerror_json.put("error_code",900);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    JavaLib(String server_address)
    {
        this(server_address, "");
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

    private JSONObject call_api_with_json_input(String api, JSONObject json_input){

        RequestBody body = RequestBody.create(JSON, json_input.toString());

        Request request = new Request.Builder()
                .url(server_address + "/" + api)
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

    private JSONObject call_api_with_form_input(String api, JSONObject json_input, String file_param_name, String file_path){
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
                    .url(server_address + "/" + api)
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

    JSONObject login(String email, String password)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("email",email);
            params.put("password", password);
            JSONObject retValue =  this.call_api_with_json_input("api/v1/user/login", params);
            if(retValue.getBoolean("success"))
                this.api_key = retValue.getString("apikey");
            return retValue;
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject scan(String file_path, String file_name) {return scan(file_path, file_name, false, "");}

    JSONObject scan(String file_path, String file_name, boolean is_private, String file_origin)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("filename",file_name);
            if(is_private)
                params.put("is_private", is_private);
            if(file_origin.length()!=0)
                params.put("file_origin", file_origin);
            return this.call_api_with_form_input("api/v1/scan", params , "filedata", file_path);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject rescan(String file_sha256)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("sha256",file_sha256);
            return this.call_api_with_json_input("api/v1/rescan", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }


    JSONObject results(String file_sha256, int scan_id)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("sha256",file_sha256);
            params.put("scan_id",scan_id);
            return this.call_api_with_json_input("api/v1/search/scan/results", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject search_by_hash(String hash){return search_by_hash(hash, 0, 0, 0, 0);}

    JSONObject search_by_hash(String hash, int ot, int ob, int page, int per_page)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("hash", hash);
            if(ot!=0)
                params.put("ot", ot);
            if(ob!=0)
                params.put("ob", ob);
            if(page!=0)
                params.put("page", page);
            if(per_page!=0)
                params.put("per_page", per_page);
            return this.call_api_with_json_input("api/v1/search/scan/hash", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject search_by_malware_name(String malware_name){return search_by_malware_name(malware_name, 0, 0, 0, 0);}

    JSONObject search_by_malware_name(String malware_name, int ot, int ob, int page, int per_page)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("malware_name", malware_name);
            if(ot!=0)
                params.put("ot", ot);
            if(ob!=0)
                params.put("ob", ob);
            if(page!=0)
                params.put("page", page);
            if(per_page!=0)
                params.put("per_page", per_page);
            return this.call_api_with_json_input("api/v1/search/scan/malware-name", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject download_file(String hash_value)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("hash",hash_value);
            return this.call_api_with_json_input("api/v1/file/download", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject get_comments(String sha256){return get_comments(sha256,0,0);}

    JSONObject get_comments(String sha256, int page, int per_page){
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("sha256",sha256);
            if(page!=0)
                params.put("page", page);
            if(per_page!=0)
                params.put("per_page", per_page);
            return this.call_api_with_json_input("api/v1/comment", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject add_comment(String sha256, String description)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("sha256",sha256);
            params.put("description",description);
            return this.call_api_with_json_input("api/v1/comment/add", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject edit_comment(int comment_id, String new_description)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("comment_id",comment_id);
            params.put("description",new_description);
            return this.call_api_with_json_input("api/v1/comment/edit", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject delete_comment(int comment_id)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("comment_id",comment_id);
            return this.call_api_with_json_input("api/v1/comment/delete", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject approve_comment(int comment_id)
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            params.put("comment_id",comment_id);
            return this.call_api_with_json_input("api/v1/comment/approve", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject get_captcha()
    {
        JSONObject params = new JSONObject();
        try{
            return this.call_api_with_json_input("api/v1/captcha", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }

    JSONObject get_av_list()
    {
        JSONObject params = new JSONObject();
        try{
            params.put("apikey",this.api_key);
            return this.call_api_with_json_input("api/v1/search/av_list", params);
        }catch (Exception e)
        {
            return unkownerror_json;
        }
    }


}
