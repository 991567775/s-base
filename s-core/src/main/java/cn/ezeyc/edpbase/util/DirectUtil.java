package cn.ezeyc.edpbase.util;

import cn.ezeyc.edpbase.util.choose.Options;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpbase.util.collections.LinkedMultiValueMap;

import java.util.List;
/**
 * @author wz
 */
@configuration
public class DirectUtil {
    @autowired
    private static RedisUtil redisUtil;
    /**
     * 获取某个字典集合
     * @param key
     * @return
     */
    public static  List<Options> getDirect (String key){
        LinkedMultiValueMap<String, Options> map= (LinkedMultiValueMap<String, Options>) redisUtil.getByString("direct");
        if(map!=null&&map.size()>0){
          return   map.getValues(key);
        }
        return null;
    }

    /**
     * 根据id获取某个字典的字典项
     * @param key
     * @return
     */
    public  static Options getDirectItem (String key,String val){
        LinkedMultiValueMap<String, Options> map= (LinkedMultiValueMap<String, Options>) redisUtil.getByString("direct");
        if(map!=null&&map.size()>0){
            final List<Options> values = map.getValues(key);
            for(Options o:values){
                if(val.equals(o.getVal())){
                    return o;
                }
            }
        }
        return null;
    }
    /**
     * 根据名称获取某个字典的字典项
     * @param key
     * @return
     */
    public static  Options getDirectItemByName (String key,String name){
        LinkedMultiValueMap<String, Options> map= (LinkedMultiValueMap<String, Options>) redisUtil.getByString("direct");
        if(map!=null&&map.size()>0){
            final List<Options> values = map.getValues(key);
            for(Options o:values){
                if(name.equals(o.getLabel())){
                    return o;
                }
            }
        }
        return null;
    }
}
