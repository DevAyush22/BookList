package com.example.ayush.booklist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * A {@link BookAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link Book} objects.
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookAdapter.class.getName();

    /**
     * Context of the app
     */
    private Context mContext;

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param mContext of the app.
     * @param books    is the list of books, which is the data source of the adapter.
     */
    public BookAdapter(Context mContext, List<Book> books) {
        super(mContext, 0, books);
        this.mContext = mContext;
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of books.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.books_list_item, parent, false);

            // Creates a ViewHolder and store references to the children views
            // we want to bind data to
            holder = new ViewHolder();


            // Find the TextView in the books_list_item.xml layout with the ID title
            holder.title = (TextView) convertView.findViewById(R.id.title);

            // Find the TextView in the books_list_item.xml layout with the ID author_name
            holder.author = (TextView) convertView.findViewById(R.id.author_name);

            //Find the ImageView in the books_list_item.xml layout with the ID image
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            // store the holder with the view.
            convertView.setTag(holder);

        } else {

            // We've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            holder = (ViewHolder) convertView.getTag();

        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        if (currentBook != null) {

            // Display the title of current book in that TextView
            holder.title.setText(currentBook.getTitle());

            // Display the title of current author in that TextView
            holder.author.setText(currentBook.getAuthor());

            // Display the image of current book in that ImageView
            // by using Glide Library
            Glide.with(mContext).load(currentBook.getImage()).into(holder.image);

        }

        // Return the whole list item layout so that it can be shown in the ListView
        return convertView;
    }

    /**
     * ViewHolder class to hold exact set of views
     */
    static class ViewHolder {
        TextView title;
        TextView author;
        ImageView image;
    }

}
