package com.litewaveinc.litewave.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jonathan on 10/10/15.
 */


public final class API {

    public static void getEvents(IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events", response);
    }

    public static void getEvent(String eventID, IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events/"+eventID, response);
    }

    private static void get(String url, IAPIResponse response) {
        API.request(url, "GET", new RequestParams(), response);
    }

    private static void post(String url, RequestParams params, IAPIResponse response) {
        API.request(url, "POST", params, response);
    }

    private static void put(String url, RequestParams params, IAPIResponse response) {
        API.request(url, "PUT", params, response);
    }

    private static void delete(String url, IAPIResponse response) {
        API.request(url, "DELETE", new RequestParams(), response);
    }

    private static void request(String url, String method, RequestParams params, final IAPIResponse apiResponse) {

        StringBuilder apiURL = new StringBuilder();
        apiURL.append("http://www.litewaveinc.com/api/");
        apiURL.append(url);

        ResponseHandlerInterface responseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                apiResponse.success(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                apiResponse.failure(errorResponse);
            }
        };

        AsyncHttpClient client = new AsyncHttpClient();
        switch (method) {
            case "GET":
                client.get(apiURL.toString(), params, responseHandler);
                break;
            case "POST":
                client.post(apiURL.toString(), params, responseHandler);
                break;
            case "PUT":
                client.put(apiURL.toString(), params, responseHandler);
                break;
            case "DELETE":
                client.delete(apiURL.toString(), responseHandler);
                break;

        }
    }
}
