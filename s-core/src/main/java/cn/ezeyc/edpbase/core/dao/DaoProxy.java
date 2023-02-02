package cn.ezeyc.edpbase.core.dao;


import cn.ezeyc.edpcommon.pojo.ZdConst;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DaoProxy<T>  implements   InvocationHandler {

    private Class<T> interfaceType;
    public DaoProxy() {

    }
    public DaoProxy(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return  ZdConst.proxy;
    }
}