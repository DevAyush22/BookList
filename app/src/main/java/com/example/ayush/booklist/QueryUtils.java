package com.example.ayush.booklist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeaturesFromJson(String bookJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON Response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Book} object.
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all info
                // for that book.
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // TITLE
                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // AUTHORS
                // Extract the value for the key called "authors"
                // Some books don't have an authors node, use try/catch to prevent null pointers
                JSONArray authorsArray = null;
                try {
                    authorsArray = volumeInfo.getJSONArray("authors");
                } catch (JSONException ignored) {
                }
                // Convert the authors to a string
                String bookAuthorsString = "";
                // If the author is empty, set it as "Unknown"
                if (authorsArray == null) {
                    bookAuthorsString = "Unknown";
                } else {
                    // Format the authors as "author1, author2, and author3"
                    int countAuthors = authorsArray.length();
                    for (int e = 0; e < countAuthors; e++) {
                        String author = authorsArray.getString(e);
                        if (bookAuthorsString.isEmpty()) {
                            bookAuthorsString = author;
                        } else if (e == countAuthors - 1) {
                            bookAuthorsString = bookAuthorsString + " and " + author;
                        } else {
                            bookAuthorsString = bookAuthorsString + ", " + author;
                        }
                    }
                }

                // IMAGE LINK
                // Extract the value for the key called "imageLinks"
                JSONObject imageLinks = null;
                try {
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                } catch (JSONException ignored) {
                }
                // Convert the image link to a string
                String smallThumbnail = "";
                if (imageLinks == null) {
                    smallThumbnail = "null";
                } else {
                    smallThumbnail = imageLinks.getString("smallThumbnail");
                }

                // BUY LINK URL
                // Extract the value for the key called "infoLink"
                String url = volumeInfo.getString("infoLink");

                // Create a new {@link Book} object with the title, bookAuthorsString, smallThumbnail,
                // and url from the JSON response.
                Book book = new Book(title, bookAuthorsString, smallThumbnail, url);
                // Add the new {@link Book} to the list of books.
                books.add(i, book);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Query the Google Books API and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeaturesFromJson(jsonResponse);
    }

}