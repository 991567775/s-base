package cn.ezeyc.edpbase.util;

import cn.ezeyc.edpcommon.enums.BaseType;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@configuration
public class UserUtil {
    @autowired
    private RedisUtil redisUtil;

    /**
     * 获取全部用户
     * @return
     */
    public List<Map> getUserList (){
        Object user = redisUtil.getByString("user");
        if(user!=null){
            return (List<Map>) user;
        }
        return   null ;
    }
    /**
     * 获取某个用户
     * @return
     */
    public Map getUser (Long id){
        Object user = redisUtil.getByString("user");
        if(user==null){
            return  null;
        }
        Optional<Map> optionalMap =((List<Map>)user).stream().filter(  e ->id.equals( e.get("id")) ).findAny();
        if(optionalMap.isPresent()){
            return null;
        }
        return   optionalMap.get();
    }
    /**
     * 获取用户列值
     * @return
     */
    public List<Object> getUserField (String field, BaseType baseType){
        List<Map> user = (List<Map>) redisUtil.getByString("user");
        if(user.isEmpty()){
            return null;
        }
        if(baseType.getValue()==BaseType.STRING.getValue()){
            return user.stream().map(p -> String.valueOf(p.get(field))).collect(Collectors.toList());
        }else if(baseType.getValue()==BaseType.LONG.getValue()){
            return user.stream().map(p -> Long.valueOf(p.get(field).toString())).collect(Collectors.toList());
        }else if(baseType.getValue()==BaseType.FLOAT.getValue()){
            return user.stream().map(p -> Float.valueOf(p.get(field).toString())).collect(Collectors.toList());
        }else if(baseType.getValue()==BaseType.DOUBLE.getValue()){
            return user.stream().map(p -> Double.valueOf(p.get(field).toString())).collect(Collectors.toList());
        }else if(baseType.getValue()==BaseType.BOOLEAN.getValue()){
            return user.stream().map(p -> Boolean.valueOf(p.get(field).toString())).collect(Collectors.toList());
        }
        else if(baseType.getValue()==BaseType.INTEGER.getValue()){
            return user.stream().map(p -> Integer.valueOf(p.get(field).toString())).collect(Collectors.toList());
        }
        return  null;
    }
    /**
     * 获取用户列值
     * @return
     */
    public List<String> getUserFieldString (String field){
        List<Map> user = (List<Map>) redisUtil.getByString("user");
        if(user.isEmpty()){
            return null;
        }
        return user.stream().map(p -> p.get(field).toString()).collect(Collectors.toList());
    }

}
