package edu.ualr.networking.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import edu.ualr.networking.NetworkApp;
import edu.ualr.networking.db.BooksDAO;
import edu.ualr.networking.db.BooksDB;
import edu.ualr.networking.model.Book;
import edu.ualr.networking.network.WebServiceManager;
import edu.ualr.networking.network.WebAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by irconde on 2019-10-25.
 */

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    private MutableLiveData<List<Book>> bookListObservable = new MutableLiveData<>();
    private WebAPI bookAPI;
    // TODO 05. We add a new DAO member to manage data in the database

    public Repository() {
        bookAPI = WebServiceManager.getService();
        // TODO 06. We get a reference to the DAO
    }

    public void fetchData() {
        // TODO 08. Get books stored in the database
        getBooksFromWeb();
    }

    public MutableLiveData<List<Book>> getBookListObservable() {
        return bookListObservable;
    }

    private void getBooksFromWeb(){
        bookAPI.getBooksFromWeb().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful()) {
                    setBooksListObservableData(response.body());
                    // TODO 09. Modify the getBooksFromWeb method. When we get a response from the server we'll add the new received books to the database
                } else {
                    switch (response.code()) {
                        case 404:
                            Log.d(TAG, "not found");
                            break;
                        case 500:
                            Log.d(TAG, "not logged in or server broken");
                            break;
                        default:
                            Log.d(TAG, "unknown error");
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    // TODO 10. Define a method to insert new books in the database using the corresponding method of the defined DAO
    private void addBooksToDB(List<Book> books) {

    }

    // TODO 07. Define a method to get the list of books stored in the database using the corresponding method of the defined DAO
    private void loadAllBooksFromDB() {

    }

    /**
     * This method changes the observable's LiveData data without changing the status
     * @param bookList the data that need to be updated
     */
    private void setBooksListObservableData(List<Book> bookList) {
        bookListObservable.setValue(bookList);
    }

    private void addBooksToListObservableData(List<Book> bookList) {
        List<Book> books = bookListObservable.getValue();
        books.addAll(bookList);
        bookListObservable.setValue(books);
    }

    // TODO 11. Modify the method saveData. We want to store new book in the database once it has been stored in the remote database
    public void saveData(Book book) {
        bookAPI.saveBookInWeb(book).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful()) {
                    addBooksToListObservableData(response.body());
                } else {
                    switch (response.code()) {
                        case 404:
                            Log.d(TAG, "not found");
                            break;
                        case 500:
                            Log.d(TAG, "not logged in or server broken");
                            break;
                        default:
                            Log.d(TAG, "unknown error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) { }
        });
    }
}
