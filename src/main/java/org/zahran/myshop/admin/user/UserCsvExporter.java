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

public class UserCsvExporter {

    public void export(List<User> users, HttpServletResponse response) throws IOException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        String timeStamp = dateFormatter.format(new Date());

        String file_name = timeStamp + ".csv";

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + file_name;
        response.setHeader(headerKey,headerValue);

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
