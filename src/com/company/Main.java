package com.company;

import com.company.CacheMapImpl;

public class Main {

    public static void main(String[] args) {
        CacheMapImpl<Integer,String> store = new CacheMapImpl<>();
        Clock.setTime(1000);
        store.setTimeToLive(1000);
        System.out.println(store.put(1, "Apple"));
        System.out.println(store.put(2, "Banana"));
        System.out.println(store.put(3, "Orange"));
        store.setTimeToLive(5000);
        System.out.println(store.put(2, "Grapes"));
        Clock.setTime(3000);
//        store.clearExpired();
        System.out.println("store[1] = " + store.get(1));
        System.out.println("size= "+store.size());
    }
}

