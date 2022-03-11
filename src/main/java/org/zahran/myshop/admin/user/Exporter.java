package org.zahran.myshop.admin.user;

import org.zahran.myshop.entities.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

abstract  class Exporter {

    public void setResponseHeader(HttpServletResponse response,String contentType,String extension) throws IOException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        String timeStamp = dateFormatter.format(new Date());

        String file_name = timeStamp + extension;

        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + file_name;
        response.setHeader(headerKey,headerValue);

    }
}
