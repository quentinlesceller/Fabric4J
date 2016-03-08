package tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import objects.exception.JSONException;
import objects.json.JSONObject;
import objects.json.JSONTokener;

// TODO: Auto-generated Javadoc
/**
 * The Class URLTools.
 * 
 * @author Quentin Le Sceller
 */
public class URLTools {

    /**
     * Instantiates a new URL tools.
     */
    public URLTools() {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> hostname.equals("127.0.0.1"));
    }

    /**
     * Gets the json.
     *
     * @param url
     *            the url
     * @return the json
     * @throws BlockchainException
     *             the blockchain exception
     */
    public JSONObject getJSON(URL url) {

        JSONObject json = null;

        URLConnection conn;

        InputStream is;

        try {
            conn = url.openConnection();
            is = conn.getInputStream();
            try {
                JSONTokener tokener = new JSONTokener(is);
                json = new JSONObject(tokener);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return json;

    }

    /**
     * Send post.
     *
     * @param url
     *            the url
     * @param parameters
     *            the parameters
     * @return the JSON object
     */
    public JSONObject sendPost(URL url, String parameters) {
        JSONObject output = null;
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
            try {
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                try {
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(parameters);
                    wr.flush();
                    wr.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    try {
                        output = new JSONObject(response.toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            } catch (ProtocolException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return output;

    }

    /**
     * Gets the httpsjson.
     *
     * @param url
     *            the url
     * @return the httpsjson
     * @throws BlockchainException
     *             the blockchain exception
     */
    public JSONObject getHTTPSJSON(URL url) {
        JSONObject json = null;

        HttpsURLConnection conn;

        InputStream is;

        try {
            conn = (HttpsURLConnection) url.openConnection();
            is = conn.getInputStream();
            try {
                JSONTokener tokener = new JSONTokener(is);
                json = new JSONObject(tokener);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return json;

    }

    /**
     * Send https post.
     *
     * @param url
     *            the url
     * @param parameters
     *            the parameters
     * @return the JSON object
     */
    public JSONObject sendHTTPSPost(URL url, String parameters) {
        JSONObject output = null;

        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection) url.openConnection();
            try {
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                try {
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(parameters);
                    wr.flush();
                    wr.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    try {
                        output = new JSONObject(response.toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            } catch (ProtocolException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        // Send post request

        return output;

    }

    /**
     * Send delete.
     *
     * @param url
     *            the url
     * 
     * @return the JSON object
     */
    public JSONObject sendDelete(URL url) {
        JSONObject output = null;
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
            try {
                con.setRequestMethod("DELETE");
                con.setDoOutput(true);

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    try {
                        output = new JSONObject(response.toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            } catch (ProtocolException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return output;

    }

    /**
     * Send https delete.
     *
     * @param url
     *            the url
     * @return the JSON object
     */
    public JSONObject sendHTTPSDelete(URL url) {
        JSONObject output = null;
        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection) url.openConnection();
            try {
                con.setRequestMethod("DELETE");
                con.setDoOutput(true);

                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    try {
                        output = new JSONObject(response.toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            } catch (ProtocolException e) {

                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return output;

    }
}
