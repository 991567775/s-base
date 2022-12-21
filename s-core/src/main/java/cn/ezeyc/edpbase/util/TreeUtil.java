package cn.ezeyc.edpbase.util;


import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TreeUtil：
 *
 * @author: Administrator
 * @date: 2020年12月15日, 0015 17:56:31
 */
public class TreeUtil {

    private  static  String zero="0";
    private  static  String menuType="menuType";
    public static Map<String, Object> transBeanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(obj), Map.class);
        return map;
    }

    /**
     * 递归树
     * @param treeNodes
     * @param pid
     * @return
     */
    public static List<Map> getTreeNodes(List treeNodes, Long pid) {
        List<Map> result=new ArrayList();
        List<Long> defaultExp=new ArrayList<>();
        if(treeNodes!=null){
            for(Object o:treeNodes){
                Map m = transBeanToMap(o);
                if("".equals(m.get("pid"))||m.get("pid")==null||String.valueOf(pid).equals(m.get("pid").toString())){
                    result.add(findChildren(treeNodes,m,defaultExp));
                }
            }
        }
        //默认展开
        if(result.size()>0){
            result.get(0).put("default",defaultExp);
        }
        return  result;
    }
    /**
     //     * 递归查找子节点
     //     *
     //     * @param treeNodes
     //     * @return
     //     */
    private static Map findChildren( List treeNodes,Map map,List def) {
        List<Map> children=new ArrayList();

        for (Object o : treeNodes) {
            Map it = transBeanToMap(o);
            if (it.get("pid")!=null&&map.get("id").toString().equals(it.get("pid").toString())) {
                children.add(it);
                findChildren(treeNodes,it,def);
            }
        }
        if(children.size()>0&&zero.equals(String.valueOf(map.get(menuType)))){
            def.add(Long.valueOf(map.get("id").toString()));
        }
        map.put("children",children);
        return map;
    }

    /**
     * 递归树
     * @param treeNodes
     * @param pid
     * @return
     */
    public static List<Map> getMenuTreeNodes(List treeNodes, Long pid) {
        List<Map> result=new ArrayList();
        if(treeNodes!=null){
            for(Object o:treeNodes){
                Map m = transBeanToMap(o);
                if(m.get("pid")!=null&&String.valueOf(pid).equals(m.get("pid").toString())){
                    result.add(findMenuChildren(treeNodes, m));


                }
            }
        }
        return  result;
    }
    private static Map findMenuChildren( List treeNodes,Map map) {
        List<Map> children=new ArrayList();
        for (Object o : treeNodes) {
            Map it = transBeanToMap(o);
            if (it.get("pid")!=null&&map.get("id").toString().equals(it.get("pid").toString())) {
                //菜单专用
                if(map.get("path")!=null&&!"".equals(map.get("path"))){
                    if(map.get("path")!=null){
                        if(it.get("path")!=null){
                            it.put("path",map.get("path").toString()+it.get("path").toString());
                        }else{
                            it.put("path",map.get("path").toString());
                        }
                    }
                }
                children.add(it);
                findMenuChildren(treeNodes,it);
            }
        }
        map.put("children",children);
        return map;
    }

}
