package com.example.administrator.rxjavaexample;

import android.annotation.TargetApi;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Model.Master;
import Model.Pet;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        final ImageView imageview= (ImageView) findViewById(R.id.imageview);
//        /**
//         * 打印helloworld，使用被观察者和订阅者
//         * 被观察者发出事件信息
//         * 订阅者处理事件
//         */
//        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                subscriber.onNext("hello world");
//                subscriber.onCompleted();
//            }
//        });
//
//        Subscriber<String> subscriber = new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//                Log.i("subscriber", "complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//                Log.i("subscriber", s);
//            }
//        };
//        observable.subscribe(subscriber);
//        Observable<String> observable=Observable.just("hello world","hello china");
//        Action1<String> action1=new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.i("Action",s);
//            }
//        };
//        observable.subscribe(action1);

        /**
         * 加载图片显示到Imageview中
         */
        Observable<Bitmap> observable1=Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.welcombackground);
                subscriber.onNext(bitmap);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        observable1.subscribe(new Observer<Bitmap>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Bitmap drawable) {
                imageview.setImageBitmap(drawable);
            }
        });

//        /**
//         * 使用map来进行对象的转化，个人理解map的用法相当于适配器，将输入的结果转化成想要得到的结果
//         */
//
//        Observable.just("hello").map(new Func1<String, String>() {
//            @Override
//            public String call(String s) {
//                return s+" wrold";
//            }
//        }).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.i("test",s);
//            }
//        });


//        /**
//         * 使用flatmap来进行一对多的对象转化
//         */
//
//        List<Master> masterList=new ArrayList<Master>();
//        for(int i=0;i<3;i++)
//        {
//            Master master=new Master();
//            master.setName("master"+i);
//            for(int j=0;j<3;j++)
//            {
//                master.getPetList().add(new Pet("pet"+j));
//            }
//            masterList.add(master);
//        }
//        for (Master item:
//                masterList) {
//            Log.i("item",item.getName());
//        }
//
//        Observable.from(masterList).flatMap(new Func1<Master, Observable<Pet>>() {
//            @Override
//            public Observable<Pet> call(Master master) {
//                return Observable.from(master.getPetList());
//            }
//        }).subscribe(new Subscriber<Pet>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Pet pet) {
//                Log.i("PetName",pet.getPetname());
//            }
//        });


    }
}
