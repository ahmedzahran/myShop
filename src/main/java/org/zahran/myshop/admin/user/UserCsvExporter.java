package org.zahran.myshop.admin.user;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.zahran.myshop.entities.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class UserCsvExporter extends Exporter{

    public void export(List<User> users, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response,"text/csv",".csv");
        ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID","Email","First Name","Last Name","Roles","Enabled"};
        String[] fieldMapping = {"id","email","firstName","lastName","roles","enabled"};
        writer.writeHeader(csvHeader);

        for (User user : users){
            writer.write(user,fieldMapping);
        }
        writer.close();
    }
}
