package cn.ezeyc.edpcommon.pojo;

import cn.ezeyc.edpcommon.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigPojo {
    private Map<String, Object> data;



    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public  Object getValue(String key){
        return getValue(key,null);
    }
    public  Object getValue(String key,Class f){
        Map<String, Object> val=data;
        if(StringUtils.isNotEmpty(key)){
            if(key.contains(".")){
                String[] keys=key.split("\\.");
                if(keys.length>0){
                    for(String s :keys){
                        Object o = val.get(s);
                        if(o instanceof Map<?,?>){
                            val= (Map<String, Object>)o ;
                        }else if(o instanceof List<?>){
                            String regEx="[^0-9]+";
                            Pattern pattern = Pattern.compile(regEx);
                            String[] cs = pattern.split(key);
                            if(cs.length>0){
                                return  ((List<?>) o).get(Integer.parseInt(key.replaceAll("[^0-9]","")));
                            }else{
                                if(f!=null){
                                    List d=new ArrayList<>();
                                    for(int x=0;x<((List<?>) o).size();x++){
                                        Object o1 = BeanUtil.mapToObject((Map<String, Object>) ((List<?>) o).get(x), f);
                                        d.add(o1);
                                    }
                                    return d;

                                }
                                return o;
                            }


                        }
                    }
                    return  val;
                }
            }else{
                return  data.get(key);
            }
        }
        return  null;
    }
}
