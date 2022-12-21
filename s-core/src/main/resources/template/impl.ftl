package ${package}.service.impl;
import dao.annotation.cn.ezeyc.edpcommon.cache;
import dao.annotation.cn.ezeyc.edpcommon.clearCache;
import framework.annotation.cn.ezeyc.edpcommon.autowired;
import pojo.cn.ezeyc.edpcommon.Page;
import session.pojo.cn.ezeyc.edpbase.Query;
import framework.annotation.cn.ezeyc.edpcommon.tx;

import java.util.List;
import ${package}.model.${tableName};
import ${package}.dao.${tableName}Dao;
import ${package}.service.${tableName}Service;
/**
* 描述：${remark} 服务实现层
* @author ${author}
* @date ${date}
*/
public class ${tableName}ServiceImpl   implements ${tableName}Service {
    @autowired
    private ${tableName}Dao dao;

    @Override
    public Page list(${tableName} o) {
        Query query=new Query();
        return dao.select(o,query);
    }

    @Override
    public List<${tableName}> listAll(${tableName} o) {
        Query query=new Query();
        return dao.list(query);
    }

    @Override
    public ${tableName} getById(Long id) {
        return dao.getById(id);
    }

    @Override
    @tx
    public ${tableName} save(${tableName} o) {
        Query query=new Query();
        if(o.getId()!=null){
            return   dao.updateById(o)>0?o:null;
        }else{
            return  dao.insert(o);
        }
    }

    @Override
    public Integer deleteById(String ids) {
        return dao.deleteByIds(ids);
    }

    @Override
    public Integer delete(${tableName} o) {
        Query query=new Query();
        return dao.delete(query);
    }
}