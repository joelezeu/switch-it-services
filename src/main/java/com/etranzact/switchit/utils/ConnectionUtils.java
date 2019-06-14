/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etranzact.switchit.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author joel.eze
 */

public class ConnectionUtils {

    public String sendPost(String xmlPayload, String serviceUrl) {

        long start = System.currentTimeMillis();
        String response = null;
        HttpURLConnection con = null;
        try {
            URL obj = new URL(serviceUrl);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/xml");

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(xmlPayload);
                wr.flush();
            }
            int responseCode = con.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + serviceUrl);
            //System.out.println("REQUEST : " + xmlPayload);
            System.out.println("RESPONSE CODE : " + responseCode);
            BufferedReader in;
            if ((responseCode == 200) || (responseCode == 201)) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            response = in.readLine();
            try {
                in.close();
            } catch (IOException e) {
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        System.out.println("RESPONSE TIME " + (System.currentTimeMillis() - start) + "ms");
        //System.out.println(response);
        return response;
    }
}
