package org.wlpiaoyi.framework.mybatis.demo.workbook;

import lombok.NonNull;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;

import org.wlpiaoyi.framework.mybatis.utils.ValueUtils;
import org.wlpiaoyi.framework.mybatis.utils.data.DataUtils;
;

public class WorkBook1 {

    public static void main(String[] args) throws IOException {
        new WorkBook1().test();
    }


    public void test() throws IOException {
        String xlsPath = DataUtils.USER_DIR + "/_resources/1.xls";
        String picPath = DataUtils.USER_DIR + "/_resources/pic/";

        Workbook workbook = this.readExcel(xlsPath);
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        CreationHelper factory = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        for (int i = 1; i <= rowNum; i++){
            try{
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(0);
                String text = this.getCellStringValue(cell);
                if(ValueUtils.isBlank(text)) continue;


                InputStream is = new FileInputStream(picPath + text + ".jpg");
                byte[] bytes = IOUtils.toByteArray(is);
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                ClientAnchor anchor = factory.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 10);
                anchor.setRow1(row.getRowNum());
                anchor.setRow2(row.getRowNum() + 25);

                HSSFComment comment = (HSSFComment) drawing.createCellComment(anchor);
                comment.setBackgroundImage(pictureIdx);
                cell.setCellComment(comment);
                Comment cellComment = cell.getCellComment();
                cellComment.setVisible(false);//设置批注默认不显示

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        File file = new File(DataUtils.USER_DIR + "/2.xls");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        fileOut.flush();
    }


    //读取excel
    public Workbook readExcel(@NonNull String filePath){
        return this.readExcel(filePath, null);
    }
    public Workbook readExcel(@NonNull String filePath, String password){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            //解密
            boolean equalsXlsx = ".xlsx".equals(filePath.substring(filePath.lastIndexOf(".")));
            boolean equalsXls = ".xls".equals(filePath.substring(filePath.lastIndexOf(".")));
            if(ValueUtils.isBlank(password)){
                if(equalsXlsx) wb = new XSSFWorkbook(is);
                else if(equalsXls) wb = new HSSFWorkbook(is);
            }else{
                POIFSFileSystem pfs = new POIFSFileSystem(is);
                EncryptionInfo encInfo = new EncryptionInfo(pfs);
                Decryptor decryptor = Decryptor.getInstance(encInfo);
                decryptor.verifyPassword(password);
                if(equalsXlsx) wb = new XSSFWorkbook(decryptor.getDataStream(pfs));
                else if(equalsXls) wb = new HSSFWorkbook(decryptor.getDataStream(pfs));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wb;
    }
    public String getCellStringValue(Cell cell){
        switch(cell.getCellType()){
            case BOOLEAN:{
                return new Boolean(cell.getBooleanCellValue()).toString();
            }
            case NUMERIC:{
                return new BigDecimal(cell.getNumericCellValue()).toString();
            }
            case STRING:{
                if(cell.getStringCellValue() == null) return null;
                return cell.getStringCellValue();
            }
            case FORMULA:{
                if(cell.getCellFormula() == null) return null;
                return cell.getCellFormula().toString();
            }
            default:{
                String value = cell.getStringCellValue();
                if(value == null) return null;
                return value;
            }
        }

    }
}
