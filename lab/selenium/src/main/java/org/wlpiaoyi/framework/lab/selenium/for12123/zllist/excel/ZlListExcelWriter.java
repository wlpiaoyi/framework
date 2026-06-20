package org.wlpiaoyi.framework.lab.selenium.for12123.zllist.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ZlListExcelWriter {

    private static final List<String> CELL_HEADS = new ArrayList<>();

    static {
        CELL_HEADS.add("合同编号");
        CELL_HEADS.add("号牌号码");
        CELL_HEADS.add("号牌种类");
        CELL_HEADS.add("合同有效期始");
        CELL_HEADS.add("合同有效期止");
        CELL_HEADS.add("未处理违法数");
        CELL_HEADS.add("承租人");
        CELL_HEADS.add("合同签订时间");
        CELL_HEADS.add("状态");
        CELL_HEADS.add("身份证明号码");
        CELL_HEADS.add("租赁类型");
    }

    public static Workbook exportData(List<Map<String, String>> dataList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook);
        int rowNum = 1;
        for (Iterator<Map<String, String>> it = dataList.iterator(); it.hasNext(); ) {
            Map<String, String> data = it.next();
            if (data == null) {
                continue;
            }
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(data, row);
        }
        return workbook;
    }

    private static Sheet buildDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        sheet.setDefaultRowHeight((short) 400);
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        Row head = sheet.createRow(0);
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(CELL_HEADS.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }

    private static void convertDataToRow(Map<String, String> data, Row row) {
        int cellNum = 0;
        for (String name : CELL_HEADS) {
            Cell cell = row.createCell(cellNum++);
            cell.setCellValue(data.getOrDefault(name, ""));
        }
    }
}
