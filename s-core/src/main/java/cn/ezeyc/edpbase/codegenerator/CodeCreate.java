package cn.ezeyc.edpbase.codegenerator;


import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpbase.util.StringUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * CodeCreate
 * @author wz
 */
public class CodeCreate {
    /**
     * 组名
     */
    private String groupId;
    /**
     * 模块id
     */
    private String artifactId;
    /**
     * 模块名称
     */
    private String artifactName;
    /**
     * 包路径名称
     */
    private String packagePath;
    /**
     * 包路径名称
     */
    private String author;
    /**
     * 数据库
     */
    private DataSource dataSource;
    /**
     * 数据库名称
     */
    private String tableName;
    /**
     * 生成路径
     */
    private String rootPath;

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * 结果集
     */
    private ResultSet resultSet;
    /**
     * 业务说明
     */
    private  String remark;
    /**
     * 加载模板
     */
    private  String template;
    /**
     *所属分层
     */
    private  String packageType;

    public String getRootPath() {
        if(StringUtils.isNotEmpty(rootPath)){
            return  ZdConst.outJavaWithRootPath(rootPath,artifactId.replace(".","/")) + ZdConst.slanting+groupId.replace(".","/")+ ZdConst.slanting+ StringUtil.rmLine(artifactId.replace(".","/"))+ ZdConst.slanting;
        }
        return ZdConst.outJavaPath(artifactId.replace(".","/")) + ZdConst.slanting+groupId.replace(".","/")+ ZdConst.slanting+ StringUtil.rmLine(artifactId.replace(".","/"))+ ZdConst.slanting;
    }
    public String getPackagePath() {
        return groupId+ ZdConst.dot+ StringUtil.rmLine(artifactId);
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
}
