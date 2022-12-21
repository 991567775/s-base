package cn.ezeyc.edpbase.codegenerator;

import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpbase.util.StringUtil;
import freemarker.template.Template;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CodeGenerator：
 *
 * @author Administrator
 * @date  2020年11月30日, 0030 17:27:55
 */
@configuration
public class CodeGenerator {

    List<Column> columnClassList = new ArrayList<>();
    public void generate(CodeCreate codeCreate,Boolean model,Boolean dao,Boolean service,Boolean control,Boolean uiList,Boolean uiEdit,Boolean treeList) {
        try {
            Connection connection = codeCreate.getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getColumns(connection.getCatalog(),"%",
                    codeCreate.getTableName(),"%");
            codeCreate.setResultSet(resultSet);
            //生成Model文件
            if(model){
                generateModelFile(codeCreate);
            }
            //生成Dao文件
            if(dao){
                generateDaoFile(codeCreate);
            }
            //生成服务层接口文件
            if(service){
                generateServiceFile(codeCreate);
                //生成服务实现层文件
                generateServiceImplFile(codeCreate);
            }
            if(control){
                //生成Controller层文件
                generateControlFile(codeCreate);
            }
            boolean isList=uiList||uiEdit;
            if(treeList&&(isList)){
                throw new ExRuntimeException("treeList跟uiList或uiEdit不可同时为true,树状页面，跟普通页不可同时生成");
            }
            if(uiList){
                //生成ui-list
                generateListFile(codeCreate);
            }
            if(uiEdit){
                //生成ui-edit
                generateEditFile(codeCreate);
            }
            if(treeList){
                //生成ui-tree
                generateTreeFile(codeCreate);
            }



        } catch (Exception e) {
            throw new ExRuntimeException(e.getMessage());
        }
    }

    private void generateTreeFile(CodeCreate codeCreate) throws Exception {
        final String path = System.getProperty("user.dir")+ File.separator+"edp-ui/src/page/bg/module/"+
                codeCreate.getArtifactId().replace("edp-","")+ ZdConst.slanting+
                StringUtil.toLowerCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()));
        codeCreate.setTemplate("tree.ftl");
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        f=new File(f.getPath()+"/list.vue");
        Map<String,Object> dataMap = new HashMap<>(9);
        generateFileByTemplate(codeCreate,f,dataMap);
    }

    private void generateEditFile(CodeCreate codeCreate) throws Exception{
        final String path = System.getProperty("user.dir")+ File.separator+"edp-ui/src/page/bg/module/"+
                codeCreate.getArtifactId().replace("edp-","")+ ZdConst.slanting+
                StringUtil.toLowerCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()));
        codeCreate.setTemplate("edit.ftl");
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        f=new File(f.getPath()+"/edit.vue");
        Map<String,Object> dataMap = new HashMap<>(9);
        generateFileByTemplate(codeCreate,f,dataMap);
    }

    private void generateListFile(CodeCreate codeCreate)throws Exception {
        final String path = System.getProperty("user.dir")+ File.separator+"edp-ui/src/page/bg/module/"+
                codeCreate.getArtifactId().replace("edp-","")+ ZdConst.slanting+
                StringUtil.toLowerCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()));
        codeCreate.setTemplate("list.ftl");
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }

        f=new File(f.getPath()+"/list.vue");
        Map<String,Object> dataMap = new HashMap<>(9);
        generateFileByTemplate(codeCreate,f,dataMap);

    }

    private void generateModelFile(CodeCreate codeCreate) throws Exception{
        final String path = codeCreate.getRootPath()+ ZdConst.package_model+ ZdConst.slanting+ StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()))+ ZdConst.suffix_java;
        File f = new File(path);
        //重置
        columnClassList=new ArrayList<>();
        Column columnClass ;
        ResultSet resultSet=codeCreate.getResultSet();
        while(resultSet.next()){
            //id等通用字段略过
            if("id".equals(resultSet.getString("COLUMN_NAME"))
                    ||"create_user".equals(resultSet.getString("COLUMN_NAME"))
                    ||"update_user".equals(resultSet.getString("COLUMN_NAME"))
                    ||"create_date".equals(resultSet.getString("COLUMN_NAME"))
                    ||"update_date".equals(resultSet.getString("COLUMN_NAME"))
                    ||"data_code".equals(resultSet.getString("COLUMN_NAME"))
                    ||"remove".equals(resultSet.getString("COLUMN_NAME"))) {
                continue;
            }
            columnClass = new Column();
            //获取字段名称
            columnClass.setColumnName(resultSet.getString("COLUMN_NAME"));
            //获取字段类型
            columnClass.setColumnType(resultSet.getString("TYPE_NAME"));
            //转换字段名称，如 sys_name 变成 SysName
            columnClass.setChangeColumnName(StringUtil.lineToHump(resultSet.getString("COLUMN_NAME")));
            //字段在数据库的注释
            columnClass.setColumnComment(resultSet.getString("REMARKS"));
            columnClassList.add(columnClass);
        }
        Map<String,Object> dataMap = new HashMap<>(11);
        dataMap.put("column",columnClassList);
        codeCreate.setTemplate("model.ftl");
        generateFileByTemplate(codeCreate,f,dataMap);

    }
    private void generateDaoFile(CodeCreate codeCreate) throws Exception{
        final String path = codeCreate.getRootPath()+ ZdConst.package_dao+ ZdConst.slanting+ StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()))+ ZdConst.end_with_Dao+ ZdConst.suffix_java;
        codeCreate.setTemplate("dao.ftl");
        File f = new File(path);
        Map<String,Object> dataMap = new HashMap<>(9);
        generateFileByTemplate(codeCreate,f,dataMap);
    }
    private void generateServiceFile(CodeCreate codeCreate) throws Exception{
        final String path = codeCreate.getRootPath()+ ZdConst.package_service+ ZdConst.slanting+ StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()))+ ZdConst.end_with_service+ ZdConst.suffix_java;
        codeCreate.setTemplate("service.ftl");
        File f = new File(path);
        Map<String,Object> dataMap = new HashMap<>(9);
          generateFileByTemplate(codeCreate,f,dataMap);
    }
    private void generateServiceImplFile(CodeCreate codeCreate) throws Exception{
        final String path = codeCreate.getRootPath()+ ZdConst.package_service_impl+ ZdConst.slanting+ StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()))+ ZdConst.end_with_service_impl+ ZdConst.suffix_java;
        codeCreate.setTemplate("impl.ftl");
        File f = new File(path);
        Map<String,Object> dataMap = new HashMap<>(9);
        generateFileByTemplate(codeCreate,f,dataMap);
    }
    private void generateControlFile(CodeCreate codeCreate) throws Exception{
        final String path = codeCreate.getRootPath()+ ZdConst.package_control+ ZdConst.slanting+ StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName()))+ ZdConst.end_with_control+ ZdConst.suffix_java;
        codeCreate.setTemplate("control.ftl");
        File f = new File(path);
        Map<String,Object> dataMap = new HashMap<>(11);
        dataMap.put("column",columnClassList);
        generateFileByTemplate(codeCreate,f,dataMap);
    }

    private void generateFileByTemplate(CodeCreate codeCreate,File file,Map<String,Object> dataMap) throws Exception{
        Template template = FreeMarkerTemplateUtils.getTemplate(codeCreate.getTemplate());
        int i = file.getPath().lastIndexOf("/");
        if(i==-1){
            i=file.getPath().lastIndexOf("\\");
        }
        File f= new File(file.getPath().substring(0,i));;
        if(!f.exists()){
            f.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        dataMap.put("table_name",codeCreate.getTableName());
        dataMap.put("tableName", StringUtil.toUpperCaseFirstOne(StringUtil.lineToHump(codeCreate.getTableName())));
        if(codeCreate.getAuthor()!=null&&!"".equals(codeCreate.getAuthor())){
            dataMap.put("author",codeCreate.getAuthor());
        }else{
            dataMap.put("author", ZdConst.AUTHOR);
        }
        dataMap.put("date",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        dataMap.put("package",codeCreate.getPackagePath());
        if(codeCreate.getRemark()!=null&&!"".equals(codeCreate.getRemark())){
            dataMap.put("remark",codeCreate.getRemark());
        }else{
            dataMap.put("remark","暂无");
        }
        if(codeCreate.getArtifactName()!=null&&!"".equals(codeCreate.getArtifactName())){
            dataMap.put("name",codeCreate.getArtifactName());
        }else{
            dataMap.put("name","未归属");
        }
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"),10240);
        template.process(dataMap,out);
    }



}
