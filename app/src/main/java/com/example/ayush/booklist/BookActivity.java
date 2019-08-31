package com.example.ayush.booklist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookActivity.class.getName();

    /**
     * URL to fetch books data from the Google Books APIs
     */
    private String googleBooksQueryUrl;

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * ImageView that is displayed when the list is empty
     */
    private ImageView mEmptyStateImageView;

    /**
     * Progress bar to show progress
     */
    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Hide the progress bar
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes a list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to play store
        // to show more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(playStoreIntent);

            }
        });

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        // Hook up the mEmptyStateTextView as the empty view of the ListView.
        bookListView.setEmptyView(mEmptyStateTextView);

        mEmptyStateImageView = (ImageView) findViewById(R.id.empty_image_view);
        // Hook up the mEmptyStateImageView as the empty view of the ListView.
        bookListView.setEmptyView(mEmptyStateImageView);

        // Get a reference to the ConnectivityManager to check state of network connectivity.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network.
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();

        // If there is a network connection, fetch data
        if (isConnected) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, BookActivity.this);
            mEmptyStateImageView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);

        } else {

            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection image
            mEmptyStateImageView.setVisibility(View.VISIBLE);
            mEmptyStateImageView.setImageResource(R.drawable.ic_no_internet_connection);

            // Update empty state with no connection error message
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }

        // Search button to perform search
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);

        /**Setting an onClickListener to the search button,
         * Appending the search term to the url,
         * Initiating the loader.
         */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapter.clear();
                loadingIndicator.setVisibility(View.VISIBLE);

                // EditText field to get search query
                EditText queryField = (EditText) findViewById(R.id.search_view);
                // Find the user's query
                String query = queryField.getText().toString();
                googleBooksQueryUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query;

                // Get a reference to the ConnectivityManager to check state of network connectivity.
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network.
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null &&
                        networkInfo.isConnectedOrConnecting();

                // If there is a network connection, fetch data
                if (isConnected) {

                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getLoaderManager();

                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                    mEmptyStateImageView.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);

                } else {

                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    // Update empty state with no connection image
                    mEmptyStateImageView.setVisibility(View.VISIBLE);
                    mEmptyStateImageView.setImageResource(R.drawable.ic_no_internet_connection);

                    // Update empty state with no connection error message
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);

                }
            }
        });

    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(BookActivity.this, googleBooksQueryUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Update empty state with no books found image
        mEmptyStateImageView.setVisibility(View.VISIBLE);
        mEmptyStateImageView.setImageResource(R.drawable.ic_no_book_found);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(R.string.no_books_found);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            // Hide empty TextView and ImageView because the data has been loaded
            mEmptyStateImageView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}
