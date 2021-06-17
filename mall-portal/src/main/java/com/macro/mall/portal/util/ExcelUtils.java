package com.macro.mall.portal.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelUtils {

    public List<JSONObject> excelToList(String sheetName, InputStream input) throws Exception {
        List<JSONObject> list = new ArrayList<>();


        Workbook workbook = null;
        workbook = WorkbookFactory.create(input);

        input.close();

        Sheet sheet;
        if (StrUtil.isNotEmpty(sheetName)) {
            sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
        } else {
            sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
        }
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows > 0) {// 有数据时才处理

            // 取出头部index
            Map<Integer, String> firstMap = new HashMap<>();
            Row row = sheet.getRow(0);
            int cellNum = row.getPhysicalNumberOfCells();
            // int cellNum = row.getLastCellNum();

            for (int k = 0; k < cellNum; k++) {
                firstMap.put(k, row.getCell(k).getStringCellValue());
            }


            for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
                row = sheet.getRow(i);
                for (int j = 0; j < cellNum; j++) {
                    Cell cell = row.getCell(j);
                    /*if (cell == null) {
                        continue;
                    }
                    int cellType = cell.getCellType();
                    String tempStr;
                    if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                        tempStr = String.valueOf(cell.getNumericCellValue());
                    } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                        tempStr = String.valueOf(cell.getBooleanCellValue());
                    } else {
                        tempStr = cell.getStringCellValue();
                    }*/

                    JSONObject json = new JSONObject();
                    json.put(firstMap.get(j), null == cell ? cell : cell.toString());
                    list.add(json);
                }
            }
        }

        return list;
    }


}