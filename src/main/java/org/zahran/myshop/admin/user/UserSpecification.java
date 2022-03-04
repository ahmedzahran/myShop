package org.zahran.myshop.admin.user;

import org.springframework.data.jpa.domain.Specification;
import org.zahran.myshop.entities.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserSpecification {

    public static Specification<User> firstNameContains(String expression) {
        return (root, query, builder) -> builder.like(root.get("firstName"),contains(expression));
    }


    public static Specification<User> lastNameContains(String expression) {
        return (root, query, builder) -> builder.like(root.get("lastName"),contains(expression));
    }

    public static Specification<User> userEmailEqual(String expression) {
        return (root, query, builder) -> builder.equal(root.get("email"),expression);
    }

    public static Specification<User> fromDateFilter(String from){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDate = LocalDate.parse(from,formatter).atStartOfDay();
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("createdAt"),fromDate);
    }

    public static Specification<User> userDateFilter(String from,String to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (from != null && !from.isEmpty() && to != null && !to.isEmpty()){
            LocalDateTime fromDate = LocalDate.parse(from,formatter).atStartOfDay();
            LocalDateTime toDate = LocalDate.parse(to,formatter).atStartOfDay();
            System.out.println(toDate);
            return (root, query, builder) -> builder.between(root.get("createdAt"),fromDate,toDate);
        }

        if (to != null && !to.isEmpty()){
            LocalDateTime toDate = LocalDate.parse(to,formatter).atStartOfDay();
            LocalDateTime currentDate = LocalDate.now().atStartOfDay();
            return (root, query, builder) -> builder.between(root.get("createdAt"),currentDate,toDate);
        }

        LocalDateTime fromDate = LocalDate.parse(from,formatter).atStartOfDay();
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("createdAt"),fromDate);
    }

    public static Specification<User> toDateFilter(String to){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime toDate = LocalDate.parse(to,formatter).atStartOfDay();
        System.out.println(toDate.plusDays(1));
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("createdAt"),toDate.plusDays(1));
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%",expression);
    }
}
