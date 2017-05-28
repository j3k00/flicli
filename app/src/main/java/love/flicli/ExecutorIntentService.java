package love.flicli;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.util.concurrent.ExecutorService;

/**
 * Created by tommaso on 28/05/17.
 */

public abstract class ExecutorIntentService extends Service {
    private boolean mRedelivery;
    private ExecutorService exec;
    private final Handler EDT = new Handler(Looper.getMainLooper());
    private int runningTasks;

    public ExecutorIntentService(String name) {}

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exec = mkExecutorService();
    }

    protected abstract ExecutorService mkExecutorService();

    @Override @UiThread
    public void onStart(@Nullable Intent intent, int startId) {
        runningTasks++;

        exec.execute(() -> {
            onHandleIntent(intent);
            EDT.post(this::endOfTask);
        });
    }

    @UiThread
    private void endOfTask() {
        if (--runningTasks == 0)
            stopSelf();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        exec.shutdown();
    }

    @Override
    public @Nullable
    IBinder onBind(Intent intent) {
        return null;
    }

    @WorkerThread
    protected abstract void onHandleIntent(@Nullable Intent intent);
}
