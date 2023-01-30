package ${package}.service.impl;
import cn.ezeyc.edpbase.pojo.session.Query;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.annotation.dao.cache;
import cn.ezeyc.edpcommon.annotation.dao.clearCache;
import cn.ezeyc.edpcommon.annotation.framework.tx;
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