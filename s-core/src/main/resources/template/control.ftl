package ${package}.control;
import framework.annotation.cn.ezeyc.edpcommon.autowired;
import mvc.annotation.cn.ezeyc.edpcommon.get;
import mvc.annotation.cn.ezeyc.edpcommon.security;
import base.pojo.cn.ezeyc.edpbase.ControlBase;
import pojo.cn.ezeyc.edpcommon.ResultBody;
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
    * showdoc
    * @catalog ${name}/${remark}
    * @title 查询分页集合
    * @description
    * @method post
    * @url 地址/${tableName}/list
    * @header Authorization 必选 string  token认证
    * @param
    * @json_param  {}
    * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
    * @return_param code int 状态码
    * @return_param message string 提示信息
    * @return_param timestamp long 服务器时间戳
    * @return_param extra map 额外信息
    * @return_param data Object 数据
    * @remark 测试接口获取返回值并参考数据字典${table_name}
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
    * showdoc
    * @catalog ${name}/${remark}
    * @title 查询全部集合
    * @description
    * @method post
    * @url 地址/${tableName}/listAll
    * @header Authorization 必选 string  token认证
    * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
    * @return_param code int 状态码
    * @return_param message string 提示信息
    * @return_param timestamp long 服务器时间戳
    * @return_param extra map 额外信息
    * @return_param data Object 数据
    * @remark 测试接口获取返回值并参考数据字典${table_name}
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
    * showdoc
    * @catalog ${name}/${remark}
    * @title 根据id查询对象
    * @description
    * @method post
    * @url 地址/${tableName}/getById
    * @header Authorization 必选 string  token认证
    * @param id 必选 Long  主键
    * @json_param  {"id":""}
    * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
    * @return_param code int 状态码
    * @return_param message string 提示信息
    * @return_param timestamp long 服务器时间戳
    * @return_param extra map 额外信息
    * @return_param data Object 数据
    * @remark 测试接口获取返回值并参考数据字典${table_name}
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
    * showdoc
    * @catalog ${name}/${remark}
    * @title 删除
    * @description
    * @method get
    * @url 地址/${tableName}/del
    * @header Authorization 必选 string  token认证
    * @param ids 必选 String  主键集合【逗号分隔】
    * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
    * @return_param code int 状态码
    * @return_param message string 提示信息
    * @return_param timestamp long 服务器时间戳
    * @return_param extra map 额外信息
    * @return_param data Object 数据
    * @remark 测试接口获取返回值并参考数据字典${table_name}
    * @number 5
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
    * showdoc
    * @catalog ${name}/${remark}
    * @title 保存
    * @description
    * @method post
    * @url 地址/${tableName}/save
    * @header Authorization 必选 string  token认证
<#if column?exists>
    <#list column as model>
        <#if (model.columnType = 'varchar'||model.columnType = 'VARCHAR' || model.columnType = 'text'|| model.columnType = 'TEXT')>
    * @param ${model.changeColumnName} 可选 String  ${model.columnComment}
        </#if>
        <#if model.columnType = 'decimal'||model.columnType = 'DECIMAL' >
    * @param ${model.changeColumnName} 可选 decimal  ${model.columnComment}
        </#if>
        <#if model.columnType = 'timestamp'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datetime'||model.columnType = 'DATETIME' >
    * @param ${model.changeColumnName} 可选 timestamp  ${model.columnComment}
        </#if>
        <#if model.columnType = 'date'||model.columnType = 'DATE'>
    * @param ${model.changeColumnName} 可选 date  ${model.columnComment}
        </#if>
        <#if model.columnType = 'time'||model.columnType = 'TIME'>
    * @param ${model.changeColumnName} 可选 time  ${model.columnComment}
        </#if>
        <#if model.columnType = 'bigint'||model.columnType = 'BIGINT' >
    * @param ${model.changeColumnName} 可选 long  ${model.columnComment}
        </#if>
        <#if model.columnType = 'bit'||model.columnType = 'BIT'|| model.columnType = 'int'||model.columnType = 'INT'||  model.columnType = 'tinyint'||model.columnType = 'TINYINT' ||model.columnType = 'smallint'||model.columnType = 'SMALLINT' >
    * @param ${model.changeColumnName} 可选 int  ${model.columnComment}
        </#if>
    </#list>
</#if>
    * @json_param  {<#if column?exists><#list column as model>"${model.changeColumnName}":"",</#list></#if>}
    * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
    * @return_param code int 状态码
    * @return_param message string 提示信息
    * @return_param timestamp long 服务器时间戳
    * @return_param extra map 额外信息
    * @return_param data Object 数据
    * @remark 测试接口获取返回值并参考数据字典${table_name}
    * @number 5
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
