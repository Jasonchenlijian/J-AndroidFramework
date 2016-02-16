package com.clj.jaf.http.core;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.msebera.android.httpclient.Header;

public class JsonHttpResponseHandler extends TextHttpResponseHandler {
    private static final String TAG = JsonHttpResponseHandler.class.getSimpleName();

    public JsonHttpResponseHandler() {
        super("UTF-8");
    }

    public JsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
    }

    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
    }

    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
    }

    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
    }

    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
    }

    public void onSuccess(int statusCode, Header[] headers, String responseString) {
    }

    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {
        if (statusCode != 204) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final Object ex = JsonHttpResponseHandler.this.parseResponse(responseBytes);
                        JsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                if (ex instanceof JSONObject) {
                                    JsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONObject) ex);
                                } else if (ex instanceof JSONArray) {
                                    JsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONArray) ex);
                                } else if (ex instanceof String) {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, (String) ((String) ex), (Throwable) (new JSONException("Response cannot be parsed as JSON data")));
                                } else {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, (Throwable) (new JSONException("Unexpected response type " + ex.getClass().getName())), (JSONObject) null);
                                }

                            }
                        });
                    } catch (final JSONException var2) {
                        JsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                JsonHttpResponseHandler.this.onFailure(statusCode, headers, (Throwable) var2, (JSONObject) null);
                            }
                        });
                    }

                }
            };
            if (!this.getUseSynchronousMode()) {
                (new Thread(parser)).start();
            } else {
                parser.run();
            }
        } else {
            this.onSuccess(statusCode, headers, new JSONObject());
        }

    }

    public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable) {
        if (responseBytes != null) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final Object ex = JsonHttpResponseHandler.this.parseResponse(responseBytes);
                        JsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                if (ex instanceof JSONObject) {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, (JSONObject) ex);
                                } else if (ex instanceof JSONArray) {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, (JSONArray) ex);
                                } else if (ex instanceof String) {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, (String) ex, throwable);
                                } else {
                                    JsonHttpResponseHandler.this.onFailure(statusCode, headers, (Throwable) (new JSONException("Unexpected response type " + ex.getClass().getName())), (JSONObject) null);
                                }

                            }
                        });
                    } catch (final JSONException var2) {
                        JsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                JsonHttpResponseHandler.this.onFailure(statusCode, headers, (Throwable) var2, (JSONObject) null);
                            }
                        });
                    }

                }
            };
            if (!this.getUseSynchronousMode()) {
                (new Thread(parser)).start();
            } else {
                parser.run();
            }
        } else {
            Log.v(TAG, "response body is null, calling onFailure(Throwable, JSONObject)");
            this.onFailure(statusCode, headers, (Throwable) throwable, (JSONObject) null);
        }

    }

    protected Object parseResponse(byte[] responseBody) throws JSONException {
        if (responseBody == null) {
            return null;
        } else {
            Object result = null;
            String jsonString = getResponseString(responseBody, this.getCharset());
            if (jsonString != null) {
                jsonString = jsonString.trim();
                if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                    result = (new JSONTokener(jsonString)).nextValue();
                }
            }

            if (result == null) {
                result = jsonString;
            }

            return result;
        }
    }
}
