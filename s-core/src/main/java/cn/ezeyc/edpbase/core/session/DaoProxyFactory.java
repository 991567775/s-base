package cn.ezeyc.edpbase.core.session;


import cn.ezeyc.edpcommon.annotation.dao.*;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpbase.interfaces.SqlSession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.*;
import java.util.List;
/**
 * @author wz
 */
public class DaoProxyFactory<T> implements FactoryBean<T> {
    /**
     * 构建DefaultRepository需要使用的参数
     */
    private Class<T> interfaceType;
    @Autowired
    private SqlSession sqlSessionFactory;
    public DaoProxyFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }
    @Override
    public T getObject()  {
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType},
                new DaoProxy<>(interfaceType,sqlSessionFactory));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
    @Override
    public boolean isSingleton() {
        return true;
    }
    class DaoProxy<T>  implements InvocationHandler {
        private Class<T> interfaceType;
        private  SqlSession sqlSessionFactory;
        public DaoProxy(Class<T> interfaceType,SqlSession sqlSessionFactory) {
            this.interfaceType = interfaceType;
            this.sqlSessionFactory=sqlSessionFactory;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //原生方法调用toString等
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this,args);
            }
            //获取被代理类接口范型
            Class model= Class.forName(((ParameterizedType)  interfaceType.getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName());
            Class t=model;
            //获取方法返回类型
            Class returnType= method.getReturnType();
            if(method.isAnnotationPresent(select.class)) {
                //判断返回类型是否是list
                if ( returnType== List.class){//list集合类型
                    String typeName=null;
                    Type type = method.getGenericReturnType();
                    if (type instanceof ParameterizedType){
                        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        //因为list泛型只有一个值 所以直接取0下标
                        typeName = actualTypeArguments[0].getTypeName();
                    }
                    if(!ZdConst.T.equals(typeName)){//范型不为(实体)T
                        t=Class.forName(typeName);
                    }

                }else if( returnType== Object.class){//返回类型为实体类型
                    returnType=model;
                }
                return  sqlSessionFactory.select(returnType,model,t,method,args);
            }else  if(method.isAnnotationPresent(insert.class)){
                return  sqlSessionFactory.insert(model ,method,args);
            }else  if(method.isAnnotationPresent(update.class)){
                return  sqlSessionFactory.update(model ,method,args);
            }else  if(method.isAnnotationPresent(delete.class)){
                return  sqlSessionFactory.delete(model ,method,args);
            }else  if(method.isAnnotationPresent(sql.class)){
                return  sqlSessionFactory.executeSql(args,returnType,model);
            }
            return  ZdConst.proxy;
        }
    }
}
