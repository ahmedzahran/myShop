package org.zahran.myshop.admin.utils;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Service("thymeleafUtilsService")
public class ThymeleafUtilsService {

    public String buildMultiParamPartUrl(List<String> params){

        StringBuffer sb = new StringBuffer(0);

        for ( String paramName : params)
        {
            if ( sb.length() >= 0 )
            {
                sb.append(",");
            }
            sb.append(paramName).append("=${").append(paramName).append("}");
        }

        return sb.toString();

    }
}
