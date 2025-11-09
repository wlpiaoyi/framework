package org.wlpiaoyi.framework.lab.selenium.for12123.lease.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.wlpiaoyi.framework.lab.selenium.for12123.lease.test.SubmitHT;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>{@code @author:}</b>         wlpia</p>
 * <p><b>{@code @description:}</b>
 * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * Excel文件读取工具类
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/9 20:48</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class ExcelReaderUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 读取Excel文件到SubmitHT对象列表
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>filePath</b>
     * {@link String}
     * filePath Excel文件路径
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/9 20:48</p>
     * <p><b>{@code @return:}</b>{@link List< SubmitHT>} SubmitHT对象列表 </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static List<SubmitHT> readExcelToSubmitHTList(String filePath) {
        List<SubmitHT> resultList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 获取第一个sheet
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过标题行，从第二行开始读取数据（索引从0开始）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                SubmitHT submitHT = parseRowToSubmitHT(row);
                if (submitHT != null) {
                    resultList.add(submitHT);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage(), e);
        }

        return resultList;
    }


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 将Excel行数据解析为SubmitHT对象
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>row</b>
     * {@link Row}
     * row Excel行
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/9 20:50</p>
     * <p><b>{@code @return:}</b>{@link SubmitHT} SubmitHT对象 </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    private static SubmitHT parseRowToSubmitHT(Row row) {
        try {
            // 获取单元格数据（根据Excel列顺序）
            String carNo = getCellStringValue(row.getCell(0)); // 车牌号
            String cardId = getCellStringValue(row.getCell(1)); // 身份证号
            String htNo = getCellStringValue(row.getCell(2)); // 合同编码
            String htSignTimeStr = getCellStringValue(row.getCell(3)); // 合同签订时间
            String leaseStartTimeStr = getCellStringValue(row.getCell(4)); // 合同开始时间
            String leaseEndTimeStr = getCellStringValue(row.getCell(5)); // 合同结束时间

            // 构建SubmitHT对象
            return SubmitHT.builder()
                    .carNo(carNo)
                    .htNo(htNo)
                    .cardId(cardId)
                    .htSignTime(parseDateTime(htSignTimeStr))
                    .leaseStartTime(parseDateTime(leaseStartTimeStr))
                    .leaseEndTime(parseDateTime(leaseEndTimeStr))
                    .build();

        } catch (Exception e) {
            System.err.println("解析第 " + (row.getRowNum() + 1) + " 行数据失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取单元格的字符串值
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>cell</b>
     * {@link Cell}
     * Excel单元格
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/9 20:51</p>
     * <p><b>{@code @return:}</b>{@link String} 字符串值</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 处理日期格式
                    return cell.getLocalDateTimeCellValue().format(DATE_TIME_FORMATTER);
                } else {
                    // 处理数字格式
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 解析日期时间字符串为LocalDateTime
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>dateTimeStr</b>
     * {@link String}
     * 日期时间字符串
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/9 20:55</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    private static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("日期时间解析失败: " + dateTimeStr);
            return null;
        }
    }

//    public static void main(String[] args) {
//        try {
//            // 读取Excel文件
//            String filePath = "C:\\Users\\wlpia\\Desktop\\12123司机信息表.xlsx";
//            List<SubmitHT> submitHTList = ExcelReaderUtil.readExcelToSubmitHTList(filePath);
//
//            // 打印读取结果
//            System.out.println("成功读取 " + submitHTList.size() + " 条数据:");
//            for (int i = 0; i < Math.min(5, submitHTList.size()); i++) {
//                SubmitHT item = submitHTList.get(i);
//                System.out.println("第" + (i + 1) + "条: " + item);
//            }
//
//            // 处理业务逻辑...
//            // processSubmitHTList(submitHTList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
