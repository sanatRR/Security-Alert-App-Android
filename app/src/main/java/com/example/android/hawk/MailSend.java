package com.example.android.hawk;

import android.util.Log;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Timestamp;

public class MailSend {
    static String HTMLContent="Temp",base64send;
    String timestamp=String.valueOf(new Timestamp(System.currentTimeMillis()));
    MailSend(String base64){

        HTMLContent="<p style=\"text-align: center;\"><span style=\"font-size: 24px;\"><strong><span style=\"color: rgb(255, 0, 0);\">Hawk Intrusion Alert</span></strong></span></p>\n" +
                "<p style=\"text-align: center;\"><u>A Wrong PIN Was Entered On Your Phone</u></p>\n" +
                "<p style=\"text-align: center;\">Device:"+SecurityService.deviceDetails+"</p>\n" +
                "<p style=\"text-align: center;\">Time Stamp:"+timestamp+"</p>\n" +
                "<p style=\"text-align: center;\">&nbsp;Intruders Image has been attached.</p>\n" +
                "<p style=\"text-align: center;\"><br></p>";
        base64send=base64;
        try {
            if(SecurityService.senderEmail!=null){
                SendMail(HTMLContent);
            }
        } catch (Exception e) {
            Log.d("Hawk.MailSend",e.getMessage());
        }
    }

    static void SendMail(String HTML) throws JSONException, MailjetSocketTimeoutException, MailjetException {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient("5dea737fd782aec79dafa7e07f525a05", "cf1d437affc2774233cf1957693dd39c", new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "sanatraorane@rediffmail.com")
                                        .put("Name", "HawkAdmin"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", SecurityService.senderEmail)
                                                .put("Name", "Client")))
                                .put(Emailv31.Message.SUBJECT, "Greetings from Hawk")
                                .put(Emailv31.Message.TEXTPART, "Incorrect Pin Alert")
                                .put(Emailv31.Message.HTMLPART, HTMLContent)
                                .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                        .put(new JSONObject()
                                                .put("ContentType", "image/bmp")
                                                .put("Filename", "Intruder.bmp")
                                                .put("Base64Content", base64send)))));
        response = client.post(request);
        Log.d("response", String.valueOf(response.getStatus()));
        Log.d("response", String.valueOf(response.getData()));
        Log.d("mailSent to",SecurityService.senderEmail);
    }
}
