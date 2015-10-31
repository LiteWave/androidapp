package com.litewaveinc.litewave.services;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import com.litewaveinc.litewave.R;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jonathan on 10/10/15.
 */


public final class API {

    static protected Context appContext;

    public static void init(Context context) {
        API.appContext = context;
    }

    public static void getClient(String clientID, IAPIResponse response) {
        API.get("clients/" + clientID, response);
    }

    public static void getEvents(String clientID, IAPIResponse response) {
        API.get("clients/" + clientID + "/events", response);
    }

    public static void getEvent(String clientID, String eventID, IAPIResponse response) {
        API.get("clients/" + clientID + "/events/" + eventID, response);
    }

    public static void getLevels(String stadiumID, IAPIResponse response) {
        API.get("stadiums/" + stadiumID + "/levels", response);
    }

    public static void getSeats(String stadiumID, String level, IAPIResponse response) {
        API.get("stadiums/" + stadiumID + "/levels/" + level, response);
    }
    // postUserLocation is used to post the seat information and return the user location id
    public static void postUserLocation(String eventid, RequestParams params, IAPIResponse response) {
        // http://127.0.0.1:3000/api/events/{eventid}/user_locations:
        API.post("events/" + eventid + "/user_locations", params, response);
    }

    public static void joinEvent(String userLocationId, IAPIResponse response)
    {
        // http://127.0.0.1:3000/api/user_locations/{UserLocationIdFromAbove}/event_joins
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
        if (appContext == null) {
            Log.d("Debug", "Need to call init first.");
            throw new UnsupportedOperationException();
        }

        StringBuilder apiURL = new StringBuilder();
        apiURL.append(appContext.getString(R.string.apiURL));
        apiURL.append(url);

        ResponseHandlerInterface responseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                apiResponse.success(response);
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
