package ${package}.model;

import dao.annotation.cn.ezeyc.edpcommon.col;
import dao.annotation.cn.ezeyc.edpcommon.pojo;
import pojo.cn.ezeyc.edpcommon.ModelBase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;

/**
* 描述：${remark}
* @author ${author}
* @date ${date}
*/
@pojo("${table_name}")
public class ${tableName} extends ModelBase<${tableName}> {

<#if column?exists>
    <#list column as model>

    <#if (model.columnType = 'varchar'||model.columnType = 'VARCHAR' || model.columnType = 'text'|| model.columnType = 'TEXT')>
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private String ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'decimal'||model.columnType = 'DECIMAL' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private BigDecimal ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'timestamp'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datetime'||model.columnType = 'DATETIME' >
    /**
    *${model.columnComment}
     */
    @col("${model.columnName}")
    private  LocalDateTime ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'date'||model.columnType = 'DATE'>
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private  LocalDate ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'time'||model.columnType = 'TIME'>
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private  LocalTime ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'bigint'||model.columnType = 'BIGINT' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private Long ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'int'||model.columnType = 'INT' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private Integer ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'smallint'||model.columnType = 'SMALLINT' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private Integer ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'tinyint'||model.columnType = 'TINYINT' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private Boolean ${model.changeColumnName};
    </#if>
    <#if model.columnType = 'bit'||model.columnType = 'BIT' >
    /**
    *${model.columnComment}
    */
    @col("${model.columnName}")
    private Boolean ${model.changeColumnName};
    </#if>
    </#list>
</#if>

<#if column?exists>
    <#list column as model>
    <#if (model.columnType = 'varchar'||model.columnType = 'VARCHAR'  || model.columnType = 'text'|| model.columnType = 'TEXT')>
    public String get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(String ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'decimal'||model.columnType = 'DECIMAL' >
    public BigDecimal get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(BigDecimal ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'timestamp'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datetime'||model.columnType = 'DATETIME' >
    public LocalDateTime get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(LocalDateTime ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'date'||model.columnType = 'DATE'>
    public LocalDate get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(LocalDate ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'time'||model.columnType = 'TIME'>
    public LocalTime get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(LocalTime ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'bigint'||model.columnType = 'BIGINT' >
    public Long get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(Long ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'int'||model.columnType = 'INT' >
    public Integer get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(Integer ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'smallint'||model.columnType = 'SMALLINT' >
     public Integer get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(Integer ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    <#if model.columnType = 'tinyint'||model.columnType = 'TINYINT' >
    public Boolean get${model.changeColumnName?cap_first}() {
        return this.${model.changeColumnName};
    }
    public void set${model.changeColumnName?cap_first}(Boolean ${model.changeColumnName}) {
        this.${model.changeColumnName} = ${model.changeColumnName};
    }
    </#if>
    </#list>
</#if>
}
