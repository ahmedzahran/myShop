package org.zahran.myshop.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.zahran.myshop.entities.Role;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    @Test
    public void createRoles(){

        Role admin = new Role("Admin","manage everything");

        Role salesperson = new Role("Salesperson","manage product, price,customers,shipping " +
                                                            ", orders and sales report");

        Role editor = new Role("Editor","manage categories ,price, articles and menus");

        Role shipper = new Role("shipper","view products,view orders and update order status");

        Role assistant = new Role("Assistant","manage question and reviews");

        repository.saveAll(List.of(admin,salesperson,editor,shipper,assistant));

    }

}
