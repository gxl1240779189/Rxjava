---
title: Rxjava操作步骤
tags: 操作步骤,总结
grammar_cjkRuby: true
---


### 1.准备

在android studio的build.gradle中添加Rxjava和Rxandroid的第三方库依赖

    compile 'io.reactivex:rxjava:1.0.14'
    compile 'io.reactivex:rxandroid:1.0.1'

然后suyc一下工程就添加进来了

### 2.开始创建一个打印Hello world的测试代码

Rxjava中比较重要的两个概念：Observable和Subscriber，这两个分别是被观察者和订阅者，Rxjava使用了观察者模式的设计思路，Observable用来发出事件序列，Subscriber用来处理事件序列。好，现在我们来看一下代码：

     /**
         * 打印helloworld，使用被观察者和订阅者
         * 被观察者发出事件信息
         * 订阅者处理事件
         */
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world");
                subscriber.onCompleted();
            }
        });

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i("subscriber", "complete");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.i("subscriber", s);
            }
        };
        observable.subscribe(subscriber);

上面的代码我们可以通过一个图来理解一下
![enter description here][1]


这是典型的观察者模式，Observable是被观察者，Subscribe是观察者，观察者将自身的引用传递给被观察者，将Observable有消息事件时，则通知Subscribe进行处理。
Subscribe中主要有三个关键函数：
 1. onNext(params)：此函数用来发送真正的消息事件，如上面代码中的OnNext("hello world")，就是向订阅者传递了一个字符串事件。
 2. onCompleted():消息事件完成执行，与subscriber.onCompleted()对应
 3. onError():执行出错，执行


还需要注意的一点是，假如Observable没有订阅者，即没有执行observable.subscribe(subscriber);给Observable指定subscriber，那么上面的onNext消息将不会被发送，会被阻塞。
这一点从源码中可以看出:

 
    public Subscription subscribe(Subscriber subscriber) {
    subscriber.onStart();
    onSubscribe.call(subscriber);
    return subscriber;
}

上面的函数看着就是给Observable指定subscriver的函数，从这里我们可以看出:

 1. 只有订阅的时候，才执行onSubscribe.call(subscriber);这就是我们上文发送消息事件的函数，假如没有订阅者，此消息将永远执行不了。
 2. 这里面还获取了Subscriber的引用，用来通知订阅者消息事件，所以不用的时候，一定要unsubscribe()，不然会引起内存泄露。

### 3.感觉上面都是入门的皮毛，来get一些高级的用法

#### 1.map的用法，用来做一对一的类型转化。先上代码：

    /**
         * 使用map来进行对象的转化，个人理解map的用法相当于适配器，将输入的结果转化成想要得到的结果
         */

        Observable.just("hello").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s+" wrold";
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("test",s);
            }
        });



上面的功能就是讲输入的hello变成hello world输出，做了格式的转化。用一个图形象的表示一下:
![enter description here][2]


#### 2.flatMap的用法，用来将一对多的类型转化:
假如现在有一个主人Master，他养了好多宠物，猫啊，狗啊，鸡啊，鸭啊等等，现在需要打印出每一个人样的宠物
，现在这就是我强行虚构出来的一对多的关系了。我们来看一下使用flatmap解决的代码示例:

    Pet.java
    public class Pet {
    private String Petname;

    public Pet(String petname) {
        Petname = petname;
    }

    public String getPetname() {
        return Petname;
    }

    public void setPetname(String petname) {
        Petname = petname;
    }
    }
    Master.java
    public class Master {
    private String name;
    private List<Pet> petList=new ArrayList<Pet>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pet> getPetList() {
        return petList;
    }

    public void setPetList(List<Pet> petList) {
        this.petList = petList;
    }
    }

    /**
         * 使用flatmap来进行一对多的对象转化
         */

        List<Master> masterList=new ArrayList<Master>();
        for(int i=0;i<3;i++)
        {
            Master master=new Master();
            master.setName("master"+i);
            for(int j=0;j<3;j++)
            {
                master.getPetList().add(new Pet("pet"+j));
            }
            masterList.add(master);
        }
        for (Master item:
             masterList) {
            Log.i("item",item.getName());
        }

        Observable.from(masterList).flatMap(new Func1<Master, Observable<Pet>>() {
            @Override
            public Observable<Pet> call(Master master) {
                return Observable.from(master.getPetList());
            }
        }).subscribe(new Subscriber<Pet>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Pet pet) {
                Log.i("PetName",pet.getPetname());
            }
        });


上面的代码中最关键的代码就是:

      Observable.from(masterList).flatMap(new Func1<Master, Observable<Pet>>() {
            @Override
            public Observable<Pet> call(Master master) {
                return Observable.from(master.getPetList());
            }
        }).
从上面的代码可以看出， flatMap() 和 map() 有一个相同点：它也是把传入的参数转化之后返回另一个对象。但需要注意，和 map() 不同的是， flatMap() 中返回的是个 Observable 对象，并且这个 Observable 对象并不是被直接发送到了 Subscriber 的回调方法中。

flatMap() 的原理是这样的：
1. 使用传入的事件对象创建一个 Observable 对象；
2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件；
3. 每一个创建出来的 Observable 发送的事件，都被汇入同一个 Observable ，而这个 Observable 负责将这些事件统一交给 Subscriber 的回调方法。这三个步骤，把事件拆成了两级，通过一组新创建的 Observable 将初始的对象『铺平』之后通过统一路径分发了下去。而这个『铺平』就是 flatMap() 所谓的 flat。


![enter description here][3]


  [1]: ./images/yemian.png "yemian.png"
  [2]: ./images/zhuanhua.png "zhuanhua.png"
  [3]: ./images/flatmap.png "flatmap.png"

#### 3.Scheduler线程的自由切换

你可以使用subscribeOn()指定被观察者代码运行的线程，使用observeOn() 执行时的当前 Observable 所对应的 Subscriber 。

    
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
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io());
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

上面的代码就是在Observable中获取图片bitmap，在Subscriber中加载bitmap到Imageview中。





