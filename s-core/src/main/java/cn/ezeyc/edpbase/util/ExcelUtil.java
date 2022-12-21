package cn.ezeyc.edpbase.util;

import cn.ezeyc.edpbase.core.utils.reflect;
import cn.ezeyc.edpbase.util.choose.Options;
import cn.ezeyc.edpcommon.annotation.excel.Excel;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.util.ClassUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zewang
 */
public class ExcelUtil {
    /**
     * PC导出表格
     * @param list 数据集合
     */
    public static void pcExportExcel(List list){
        pcExportExcel(list,null,null);
    }

    /**
     * PC导出表格
     * @param list 数据集合
     * @param title 标题
     */
    public static void pcExportExcel(List list,String title){
        pcExportExcel(list,title,null);
    }

    /**
     * PC导出表格
     * @param list 数据集合
     * @param title 标题
     * @param fileName 文件名称
     */
    public static void pcExportExcel(List list,String title,String fileName){
        export(list,null,title,fileName,true);
    }
    /**
     * 非pc导出表格
     * @param list 数据集合
     */
    public static void exportExcel(List list,String path){
        exportExcel(list,path,null);
    }

    /**
     * 非pc导出表格
     * @param list 数据集合
     * @param title 标题
     */
    public static void exportExcel(List list,String path,String title){
        exportExcel(list,path,title,null);
    }

    /**
     * 非PC导出表格  报错本地提供下载
     * @param list 数据集合
     * @param title 标题
     * @param fileName 文件名称
     */
    public static void exportExcel(List list,String path,String title,String fileName){
        export(list,path,title,fileName,false);
    }
    /**
     * 导出表格
     * @param list 数据集合
     * @param title 标题
     * @param fileName 文件名称
     */
    private static void export(List list,String path,String title,String fileName,Boolean pc){
        Workbook work = new XSSFWorkbook();
        Sheet sheet = work.createSheet();
        Field[] fields = ClassUtil.getAllFields(list.get(0).getClass(),Excel.class);
        Integer rowInt = 0;
        //设置标题
        if(title!=null&&!"".equals(title)){
            //合并标题
            CellRangeAddress cellAddresses = new CellRangeAddress(0, 0, 0, fields.length);
            sheet.addMergedRegion(cellAddresses);
            //创建大标题行
            Row titles = sheet.createRow(rowInt++);
            Cell cells = titles.createCell(0);
            cells.setCellValue(title);
//                    cells.setCellStyle(cellStyletitle);
            RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
        }

        if(fields.length>0){
            //列名
            Row row = sheet.createRow(rowInt++);
            int c=0;
            for (Field f:fields) {
                Excel annotation = f.getAnnotation(Excel.class);
                if(annotation.type()==Excel.Type.ALL||annotation.type()==Excel.Type.EXPORT){
                    //设置Excel表头
                    Cell cell = row.createCell(c++);
                    cell.setCellValue(annotation.name());
                }
            }
        }
        //数据
        for (Object o : list) {
            Row row = sheet.createRow(rowInt++);
            int c=0;
            for (Field f:fields) {
                Excel annotation = f.getAnnotation(Excel.class);
                if(annotation.type()==Excel.Type.ALL||annotation.type()==Excel.Type.EXPORT){
                    //设置Excel表头
                    Cell cell = row.createCell(c++);
                    try {
                        f.setAccessible(true);
                        if(!"".equals(annotation.dateFormat())
                                &&(f.getType()== LocalDateTime.class||f.getType()== LocalTime.class||f.getType()== LocalDate.class)){
                            cell.setCellValue(new SimpleDateFormat(annotation.dateFormat()).parse( f.get(o).toString())) ;
                        }else if(!"".equals(annotation.dictType())){
                            List<Options> direct = DirectUtil.getDirect(annotation.dictType());
                            if(direct!=null&&direct.size()>0){
                                for(Options options:direct){
                                    if(options.getCode().equals(f.get(o).toString())){
                                        cell.setCellValue(options.getLabel());
                                        break;
                                    }
                                }
                            }
                        }else if(!"".equals(annotation.readConverterExp())){
                            String[] split = annotation.readConverterExp().split(",");
                            if(split.length>0){
                                for(String s: split){
                                    String[] v = s.split("=");
                                    if(v.length>0&&v[0].equals(f.get(o).toString())){
                                        cell.setCellValue(v[1]);
                                        break;
                                    }
                                }
                            }
                        }else if(!"".equals(annotation.suffix())){
                            //后缀
                            cell.setCellValue(f.get(o).toString()+annotation.suffix());
                        }
                        else if(f.get(o)!=null) {
                            cell.setCellValue(f.get(o).toString());
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        //导出
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = requestAttributes.getResponse();
            if(fileName==null||"".equals(fileName)){
                fileName="表格.xlsx";
            }
            if(pc){
                download(work,response,fileName);
            }else{
                write(work,response,fileName,path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List importExcel(File file, Class entity) {
        List list=new ArrayList();
        try{
            InputStream is = new FileInputStream(file);
            Field[] fields = ClassUtil.getAllFields(entity,Excel.class);
            XSSFWorkbook book = new XSSFWorkbook(is);
            XSSFSheet sheet = book.getSheetAt(0);  //获取sheet
            int rows = sheet.getPhysicalNumberOfRows();
            Map<Integer,String> cell=new HashMap<>();
            for(int x=0;x<rows;x++){
                XSSFRow row = sheet.getRow(x);   //获取数据行
                //标题行
                if(x==0){
                    for(int y=0;y<row.getPhysicalNumberOfCells();y++){
                        for(Field f:fields){
                            Excel annotation = f.getAnnotation(Excel.class);
                            if((annotation.type()==Excel.Type.ALL||annotation.type()==Excel.Type.IMPORT)&&annotation.name().equals(row.getCell(y).getStringCellValue())){
                                cell.put(y,f.getName());
                                break;
                            }
                        }
                    }
                }else{
                    //数据
                    Object obj=entity.newInstance();
                    for(int y=0;y<row.getPhysicalNumberOfCells();y++){
                        for (Integer key : cell.keySet()) {
                            if(y==key){
                                for(Field f:fields){
                                    if(f.getName().equals(cell.get(key))){
                                        Excel annotation = f.getAnnotation(Excel.class);
                                        //字典项目
                                        if(!"".equals(annotation.dictType())){
                                            List<Options> direct = DirectUtil.getDirect(annotation.dictType());
                                            if(direct!=null&&direct.size()>0){
                                                for(Options options:direct){
                                                    if(options.getLabel().equals(row.getCell(y).getStringCellValue())){
                                                        reflect.setNormal(f,f.getType(),entity,options.getCode());
                                                        break;
                                                    }
                                                }
                                            }
                                        //转换
                                        }else if(!"".equals(annotation.readConverterExp())){
                                            String[] split = annotation.readConverterExp().split(",");
                                            if(split.length>0){
                                                for(String s: split){
                                                    String[] v = s.split("=");
                                                    if(v.length>0&&v[1].equals(row.getCell(y).getStringCellValue())){
                                                        reflect.setNormal(f,f.getType(),entity,v[0]);
                                                        break;
                                                    }
                                                }
                                            }
                                        }else if(!"".equals(annotation.suffix())){
                                            reflect.setNormal(f,f.getType(),entity,row.getCell(y).getStringCellValue().replace(annotation.suffix(),""));
                                            break;
                                        }else {
                                            reflect.setNormal(f,f.getType(),entity,row.getCell(y).getStringCellValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    list.add(obj);
                }
            }
        }catch (Exception e){
           throw new ExRuntimeException(e.getMessage());
        }
        return list;
    }
    /**
     *
     * @param workbook
     * @param response
     * @param fileName
     * @throws IOException
     */
    private static void download(Workbook workbook, HttpServletResponse response, String fileName) throws  IOException {
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        workbook.write(os);
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"utf-8"));
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setContentLength(os.size());
        response.addHeader("Content-Length", "" + os.size());
        ServletOutputStream outputStream = response.getOutputStream();
        os.writeTo(outputStream);
        os.close();
        outputStream.flush();
    }
    private static void write(Workbook workbook,  HttpServletResponse response,String fileName,String path) throws  IOException {
        //声明输出流
        OutputStream os = null;
        //设置响应头
        setResponseHeader(response,fileName);
        //
        File file = new File(path+"/"+fileName);
        os = new FileOutputStream(file);
        workbook.write(os);
        // 关闭输出流
        if (os != null) {
            os.close();
        }
    }
    /**
     * todo 即将废弃
     * @param response 响应
     * @param fileName 文件名
     * @param columnList 每列的标题名
     * @param dataList 导出的数据
     */
    public static void exportExcel(String filePath, HttpServletResponse response, String fileName, List<String> columnList, List<List<String>> dataList, ArrayList<String> title){
        //声明输出流
        OutputStream os = null;

        //设置响应头
        setResponseHeader(response,fileName);
        try {
            //获取输出流
//            os = response.getOutputStream();
            File file = new File(filePath+"/"+fileName+".xlsx");
            os = new FileOutputStream(file);
            //内存中保留1000条数据，以免内存溢出，其余写入硬盘
            SXSSFWorkbook wb = new SXSSFWorkbook(1000);
            //获取该工作区的第一个sheet
            Sheet sheet1 = wb.createSheet("sheet1");
            int excelRow = 0;
            //设置样式
            CellStyle cellStyle = wb.createCellStyle();
            //下边框
            cellStyle.setBorderBottom(BorderStyle.THIN);
            //左边框
            cellStyle.setBorderLeft(BorderStyle.THIN);
            //上边框
            cellStyle.setBorderTop(BorderStyle.THIN);
            //右边框
            cellStyle.setBorderRight(BorderStyle.THIN);
            //左右居中
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            //上下居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            //设置样式
            CellStyle cellStyletitle = wb.createCellStyle();

            //左右居中
            cellStyletitle.setAlignment(HorizontalAlignment.CENTER);
            //上下居中
            cellStyletitle.setVerticalAlignment(VerticalAlignment.CENTER);

            //字体
            Font font = wb.createFont();
            font.setBold(true);
            cellStyletitle.setFont(font);
            //合并标题
            CellRangeAddress cellAddresses = new CellRangeAddress(0, 0, 0, 5);
            sheet1.addMergedRegion(cellAddresses);
            //创建大标题行
            Row titles = sheet1.createRow(excelRow++);
            Cell cells = titles.createCell(0);
            cells.setCellValue(title.get(0));
            cells.setCellStyle(cellStyletitle);
            RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet1);
            RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet1);
            RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet1);
            RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet1);
            //创建标题行
            Row titleRow = sheet1.createRow(excelRow++);
            for(int i = 0;i<columnList.size();i++){
                //创建该行下的每一列，并写入标题数据
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(columnList.get(i));
                cell.setCellStyle(cellStyle);
            }


            //设置内容行
            if(dataList!=null && dataList.size()>0){
                //序号是从1开始的
                int count = 1;
                //外层for循环创建行
                for(int i = 0;i<dataList.size();i++){
                    Row dataRow = sheet1.createRow(excelRow++);
                    //内层for循环创建每行对应的列，并赋值
                    for(int j = 0;j<columnList.size();j++){//由于多了一列序号列所以内层循环从-1开始
                        Cell cell = dataRow.createCell(j);
                        if(j<dataList.get(i).size()){
//                            if(j==-1){//第一列是序号列，不是在数据库中读取的数据，因此手动递增赋值
//                                cell.setCellValue(count++);
//                            }else{//其余列是数据列，将数据库中读取到的数据依次赋值
//
//                            }
                            cell.setCellValue(dataList.get(i).get(j));
                            cell.setCellStyle(cellStyle);
                        }else{//空单元格
                            cell.setCellStyle(cellStyle);
                        }

                    }
                }
            }



            //将整理好的excel数据写入流中
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输出流
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * todo 即将废弃
     * @param response
     * @param fileName
     * @param columnList
     * @param dataList
     */
    public static void exportExcel(HttpServletResponse response, String fileName, List<String> columnList,List<List<String>> dataList){
        //声明输出流
        OutputStream os = null;

        //设置响应头
        setResponseHeader(response,fileName);
        try {
            //获取输出流
            os = response.getOutputStream();
            //内存中保留1000条数据，以免内存溢出，其余写入硬盘
            SXSSFWorkbook wb = new SXSSFWorkbook(1000);
            //获取该工作区的第一个sheet
            Sheet sheet1 = wb.createSheet("sheet1");
            int excelRow = 0;
            //创建标题行
            Row titleRow = sheet1.createRow(excelRow++);
            for(int i = 0;i<columnList.size();i++){
                //创建该行下的每一列，并写入标题数据
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(columnList.get(i));
            }
            //设置内容行
            if(dataList!=null && dataList.size()>0){
                //序号是从1开始的
                int count = 1;
                //外层for循环创建行
                for(int i = 0;i<dataList.size();i++){
                    Row dataRow = sheet1.createRow(excelRow++);
                    //内层for循环创建每行对应的列，并赋值
                    for(int j = -1;j<dataList.get(0).size();j++){//由于多了一列序号列所以内层循环从-1开始
                        Cell cell = dataRow.createCell(j+1);
                        if(j==-1){//第一列是序号列，不是在数据库中读取的数据，因此手动递增赋值
                            cell.setCellValue(count++);
                        }else{//其余列是数据列，将数据库中读取到的数据依次赋值
                            cell.setCellValue(dataList.get(i).get(j));
                        }
                    }
                }
            }
            //将整理好的excel数据写入流中
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输出流
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 设置浏览器下载响应头
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
