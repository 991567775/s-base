package ${package}.control;
import cn.ezeyc.edpbase.pojo.base.ControlBase;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.mvc.get;
import cn.ezeyc.edpcommon.annotation.mvc.security;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import ${package}.model.${tableName};
import ${package}.service.${tableName}Service;
/**
* 描述：${remark}控制层
* @author ${author}
* @date ${date}
*/
public class ${tableName}Control extends ControlBase{
    @autowired
    private ${tableName}Service service;
    /**
    * 分页查询
    * @args pageNo 可选   第几页
    * @args pageSize 可选   每页数
    */
    @security("${table_name? replace('_',':')}:list")
    public ResultBody list(${tableName} a){
        try {
            return ResultBody.success(service.list(a));
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }
    }
    /**
    * 查询全部
    */
    @security("${table_name? replace('_',':')}:list")
    public ResultBody listAll(${tableName}  a){
        try {
            return ResultBody.success(service.listAll(a));
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }

    }
    /**
    * 根据id查询对象
    * @args id 必选 主键
    */
    @security("${table_name? replace('_',':')}:view")
    public  ResultBody getById( Long id){
        try {
            return    ResultBody.success(service.getById(id));
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }
    }
    /**
    * 删除
    * @args id 必选   主键
    */
    @get
    @security("${table_name? replace('_',':')}:del")
    public ResultBody del( String ids){
        try {
            return  ResultBody.success(service.deleteById(ids));
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }

    }
    /**
    * 保存
    <#if column?exists>
        <#list column as model>
    * @args ${model.changeColumnName} 可选   ${model.columnComment}
        </#list>
    </#if>
    */
    @security("${table_name? replace('_',':')}:save")
    public  ResultBody save(${tableName} entity){
        try {
            return ResultBody.success(service.save(entity));
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }
    }
}
