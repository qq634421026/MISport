package online.aroenzhang.misport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author aroenzhang
 * @date 2017/10/16
 */

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //线程池
    //ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
    //    new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());
    //singleThreadPool.execute(new Runnable() {
    //  @Override public void run() {
    //    Thread.currentThread().getName();
    //  }
    //});
    //singleThreadPool.shutdown();
  }
}
