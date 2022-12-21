package cn.ezeyc.edpbase.util.choose;



import cn.ezeyc.edpcommon.enums.BaseType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * @author wz
 */
public class ListToChooseUtil {

    /**
     * long 类型val
     * 将普通数据集合转为选项集合   默认 label=>label;id=>val   fields 第一个参数表示label,第二个表示id
     * @param list
     * @param fields
     * @return
     */

    public  static List<Options> toChoose(List list, BaseType baseType, String ...fields){
        List<Options> newList=null;
        if(list!=null&&list.size()>0){
            newList=new ArrayList<>();
            Class c=list.get(0).getClass();
            Field label =null;
            Field id=null;
            Options options=null;
            try {
                label = c.getDeclaredField("label");
                if(fields!=null&&fields.length>0){
                    label = c.getDeclaredField(fields[0]);
                }


                if(fields!=null&&fields.length>1){
                    id = c.getDeclaredField(fields[1]);
                }else{
                    id = c.getDeclaredField("id");
                }
            } catch (NoSuchFieldException e) {
                try {
                    if(fields!=null&&fields.length>1){
                        id= c.getSuperclass().getDeclaredField("id");
                    }else{
                        id= c.getSuperclass().getDeclaredField("id");
                    }
                } catch (NoSuchFieldException noSuchFieldException) {
                }
            }
            try {
                id.setAccessible(true);
                label.setAccessible(true);
                for(int x=0;x<list.size();x++){
                    options=new Options();
                    if(BaseType.LONG==baseType){
                        options.setVal(Long.valueOf(String.valueOf(id.get(list.get(x)))));
                    }else if(BaseType.INTEGER==baseType){
                        options.setVal(Integer.valueOf(String.valueOf(id.get(list.get(x)))));
                    }else if(BaseType.DOUBLE==baseType){
                        options.setVal(Double.valueOf(String.valueOf(id.get(list.get(x)))));
                    }else if(BaseType.FLOAT==baseType){
                        options.setVal(Float.valueOf(String.valueOf(id.get(list.get(x)))));
                    }else if(BaseType.STRING==baseType){
                        options.setVal(String.valueOf(id.get(list.get(x))));
                    }
                    options.setLabel(label.get(list.get(x)).toString());
                    newList.add(options);
                }
            }  catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return newList;
    }

}
