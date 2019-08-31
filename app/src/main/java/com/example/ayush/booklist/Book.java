package com.example.ayush.booklist;

/**
 * A {@link Book} object contains information related to a single book.
 */
public class Book {

    /**
     * Author of book.
     */
    private String mAuthor;

    /**
     * Title of book.
     */
    private String mTitle;

    /**
     * Image of book.
     */
    private String mImage;

    /**
     * Buy link of the book.
     */
    private String mUrl;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param author is the author of the book
     * @param title  is the title of the book
     * @param image  is the image of the book
     * @param url    is the buy link of the book
     */
    public Book(String title, String author, String image, String url) {
        mTitle = title;
        mAuthor = author;
        mImage = image;
        mUrl = url;
    }

    /**
     * Returns the author of the book.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the image of the book.
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Returns the buy link to find more information about the book.
     */
    public String getUrl() {
        return mUrl;
    }

}
