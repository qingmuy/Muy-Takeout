package com.qingmuy.utils;

/**
 * Created withIntelliJ IDEA.
 *
 * @author: qingmuy
 * @date:2024/10/16
 * @time:18:40
 * @description : do some thing
 */
public interface ILock {

    boolean tryLock(Long timeoutSec);

    void unlock();
}
