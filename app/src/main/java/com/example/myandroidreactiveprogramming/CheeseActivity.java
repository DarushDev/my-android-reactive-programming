package com.example.myandroidreactiveprogramming;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class CheeseActivity extends BaseSearchActivity {

    // 1
    private Observable<String> createButtonClickObservable() {

        // 2
        return Observable.create(new ObservableOnSubscribe<String>() {

            // 3
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                // 4
                mSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 5
                        emitter.onNext(mQueryEditText.getText().toString());
                    }
                });

                // 6
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        // 7
                        mSearchButton.setOnClickListener(null);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Observable<String> searchTextObservable = createButtonClickObservable();
        Observable<String> searchTextObservable = createTextChangeObservable();

        searchTextObservable
                // 1
                .observeOn(AndroidSchedulers.mainThread())
                // 2
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        showProgressBar();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String query) {
                        return mCheeseSearchEngine.search(query);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> result) {
                        // 3
                        hideProgressBar();
                        showResult(result);
                    }
                });
    }

    //1
    private Observable<String> createTextChangeObservable() {
        //2
        Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                //3
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}

                    //4
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emitter.onNext(s.toString());
                    }
                };

                //5
                mQueryEditText.addTextChangedListener(watcher);

                //6
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mQueryEditText.removeTextChangedListener(watcher);
                    }
                });
            }
        });

        // 7
        //return textChangeObservable;
        return textChangeObservable
            .filter(new Predicate<String>() {
                @Override
                public boolean test(String query) throws Exception {
                    return query.length() >= 2;
                }
            });
    }
}
