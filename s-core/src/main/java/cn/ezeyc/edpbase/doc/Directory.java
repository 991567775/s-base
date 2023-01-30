package cn.ezeyc.edpbase.doc;

import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.param.EnumInfoParam;
import cn.torna.sdk.param.EnumItemParam;
import cn.torna.sdk.request.EnumBatchPushRequest;
import cn.torna.sdk.response.EnumPushResponse;
import com.alibaba.fastjson2.JSON;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典推送
 * @author by wz
 * @date 2022/10/21.
 */
public class Directory {
    /**
     * 配置
     */
    private static DocConfig config; // 推送客户端
    /**
     * 客户端
     */
    private  static OpenClient client = null;
    HikariDataSource dataSource = new HikariDataSource();
    /**
     * 创建文档
     */
    public  void create(){
        config = getJson();
        //数据链接
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(config.getSqlUrl());
        dataSource.setUsername(config.getUser());
        dataSource.setPassword(config.getPwd());
        //初始化api
        client=new OpenClient(config.getOpenUrl());
        //推送
        try {
            testEnumPush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 推送字典
     */
    private void testEnumPush() throws  SQLException {
        EnumBatchPushRequest request = new EnumBatchPushRequest(config.getAppToken());
        request.setEnums(getEnumInfos());
        // 发送请求
        EnumPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("=================请求成功=====================");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    /**
     * 获取json配置
     * @return
     */
    private DocConfig getJson()  {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("doc.json");
        String jsontext = null;
        try {
            jsontext = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 转为实体类
        return JSON.parseObject(jsontext, DocConfig.class);
    }


    private List<EnumInfoParam> getEnumInfos() throws SQLException {
        List<EnumInfoParam> list=new ArrayList<>();

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select d.`label`as name,d.code,v.`label`,v.val FROM sys_direct_value v LEFT JOIN sys_direct d on v.pid=d.id ");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<EnumItemParam> itemParams=null;
        //主
        EnumInfoParam enumInfoParam = null;
        String name="";
        while(resultSet.next()){
           if(!name.equals(resultSet.getString("name"))){
               enumInfoParam = new EnumInfoParam();
               itemParams=new ArrayList<>();
               enumInfoParam.setName(resultSet.getString("name"));
               enumInfoParam.setDescription(resultSet.getString("code"));
           }
            //子
            EnumItemParam itemParam = new EnumItemParam();
            itemParam.setValue(resultSet.getString("val"));
            itemParam.setDescription(resultSet.getString("label"));
            itemParam.setName(resultSet.getString("label"));
            itemParam.setType(getType(resultSet.getString("val")));
            itemParams.add(itemParam);
            if(!name.equals(resultSet.getString("name"))){
                enumInfoParam.setItems(itemParams);
                list.add(enumInfoParam);
            }
            name= resultSet.getString("name");
        }
        connection.close();
        return list;
    }

    public static String getType(String o){
        if("true".equals(o)||"false".equals(o)){
            return "Boolean";
        } else if (isNumeric(o)) {
            return "Number";
        }
        return "String";
    }
    public static boolean isNumeric(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }
}
