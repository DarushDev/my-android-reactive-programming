package com.example.myandroidreactiveprogramming;

import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

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

}
