package cn.ezeyc.edpbase.util;

import cn.ezeyc.edpbase.idgenerator.SnowflakeIdGenerator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * @author wz
 */
public class FileUtil {

    /**
     * 远程文件上传
     * @param url
     * @param uploadFilePath
     * @param types
     * @return
     * @throws IOException
     */
    public static String remoteUpload(String url,String uploadFilePath,String types)
            throws IOException {
        URL objectUrl = new URL(url);
        //利用HttpURLConnection对象,我们可以从网络中获取网页数据.
        HttpURLConnection conn = (HttpURLConnection) objectUrl.openConnection();
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        //转file
        File newFile = new File(uploadFilePath+ SnowflakeIdGenerator.nextId()+ types);
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(newFile);
        while ((index = inputStream.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        inputStream.close();
        downloadFile.close();
        return newFile.getPath().replaceAll("\\\\","/").replace(uploadFilePath,"/upload");
    }

    /**
     * 文件上传
     * @param file
     * @param uploadFilePath
     * @return
     */
    public static String upload(File file,String uploadFilePath){
        String date="/"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))+"/";
        File path = new File(uploadFilePath+date);
        if(!path.exists()){
            path.mkdirs();
        }
        //当前日期


        File newFile = new File(uploadFilePath+ date+ SnowflakeIdGenerator.nextId()+ StringUtil.getFileType(file));
        try {
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            //创建输入流对象
            FileInputStream fis = new FileInputStream(file);
            //创建输出流对象
            FileOutputStream fos = new FileOutputStream(newFile);
            //创建搬运工具
            byte[] data = new byte[1024*8];
            //创建长度
            int len = 0;
            //循环读取数据
            while((len = fis.read(data))!=-1)
            {
                fos.write(data,0,len);
            }
            //释放资源
            fis.close();
            //释放资源
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //移除临时文件
            MultipartFileToFile.delteTempFile(file);
        }
        return newFile.getPath().replaceAll("\\\\","/").replace(uploadFilePath,"/upload");
    }

    public static void deleteFile(String uploadFilePath,String[] path){
        if(path!=null&&path.length>0){
            File file=null;
            for(String p:path){
                file= new File(uploadFilePath+p.replace("/upload",""));
                if(file.exists()){
                    file.delete();
                }
            }
        }

    }


}
