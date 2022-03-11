package org.zahran.myshop.admin.user;


import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.zahran.myshop.entities.User;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class UserExcelExporter extends Exporter{


    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public UserExcelExporter() {
        this.workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(){
         this.sheet = workbook.createSheet();
         XSSFRow row = sheet.createRow(0);

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row,0,"User Id",style);
        createCell(row,1,"E-mail",style);
        createCell(row,2,"First Name",style);
        createCell(row,3,"Last Name",style);
        createCell(row,4,"Roles",style);
        createCell(row,5,"Status",style);
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style){
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);
        if (value instanceof Integer){
            cell.setCellValue((Integer)value);
        }else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }
    public void export(List<User> users, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response,"application/octet-stream",".xlsx");
        writeHeaderLine();
        writeDataLines(users);
        ServletOutputStream stream = response.getOutputStream();

        workbook.write(stream);
        workbook.close();
        stream.close();
    }

    private void writeDataLines(List<User> users) {
        int rowIndex = 1;

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (User user : users){
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            createCell(row,columnIndex++,user.getId(),style);
            createCell(row,columnIndex++,user.getEmail(),style);
            createCell(row,columnIndex++,user.getFirstName(),style);
            createCell(row,columnIndex++,user.getLastName(),style);
            createCell(row,columnIndex++,user.getRoles().toString(),style);
            createCell(row,columnIndex++,user.isEnabled(),style);
        }
    }
}
