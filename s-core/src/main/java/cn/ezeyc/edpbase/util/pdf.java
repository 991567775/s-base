package cn.ezeyc.edpbase.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;

public class pdf {
    /**
     * pdf
     * @param files
     * @param saveDir
     * @throws Exception
     */
    public static void mergePdf(File[] files,String saveDir) throws Exception {
        // pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        // 合并pdf生成的文件名
        String destinationFileName = files[0].getName();
        for (File file : files) {
            mergePdf.addSource(saveDir + file.separator + file.getName());
        }
        // 设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(saveDir + File.separator + destinationFileName);
        // 合并pdf
        try {
            try {
                mergePdf.mergeDocuments();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("pdf文件合并成功");
    }
}
