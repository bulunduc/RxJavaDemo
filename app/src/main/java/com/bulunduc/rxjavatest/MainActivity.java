package com.bulunduc.rxjavatest;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RxJava";
    private Observable<String> mObservable2;
    private TextView mTextView;
    private SearchView mSearchView;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchView = findViewById(R.id.searchView);
        mTextView = findViewById(R.id.tvGreeting);

        mObservable2 = Observable.create(emitter -> {
            SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!emitter.isDisposed()) { //если еще не отписались
                        emitter.onNext(s); //отправляем текущее состояние
                        return true;
                    }
                    return false;
                }
            };
            mSearchView.setOnQueryTextListener(listener);
        });

        mObservable2.debounce(600, TimeUnit.MILLISECONDS)
                .filter(text -> {
                    if (text.isEmpty()) {
                        return false;
                    } else {
                        return true;
                    }
                })
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.d(TAG, s);
                    mTextView.setText(s);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
