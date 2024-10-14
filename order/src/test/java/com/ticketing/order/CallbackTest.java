package com.ticketing.order;

import org.junit.jupiter.api.Test;

public class CallbackTest {

    @Test
    void testSync() {
        var result = sync();
        System.out.println(result);
    }

    @Test
    void testAsync() {
        async(new Callback());
        async(new Callback());
    }

    boolean sync() {
        return true;
    }

    void async(Callback callback) {
        callback.invoke();
    }

    public static class Callback {

        public void invoke() {
            System.out.println("invoke callback");
        }
    }

    // async Call back 찾아보기
}
