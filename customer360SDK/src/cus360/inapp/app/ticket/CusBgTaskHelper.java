package cus360.inapp.app.ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import cus360.inapp.base.HomerLibs.HomerFileCache;

public class CusBgTaskHelper {
	ExecutorService mExecutorService;

	public CusBgTaskHelper(Context context) {
		mExecutorService = Executors.newFixedThreadPool(5);
	}

	public void addToQueue(Runnable mRunnable) {
		mExecutorService.submit(mRunnable);
	}

}
