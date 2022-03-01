package org.zahran.myshop.admin.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.zahran.myshop.entities.Role;
import org.zahran.myshop.entities.User;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createUserWithOneRole(){
        Role roleAdmin = entityManager.find(Role.class,1);
        User ahmed = new User("ahmedzahran@gmail.com","123456","ahmed","zahran");
        ahmed.addRole(roleAdmin);
        User savedUser = userRepository.save(ahmed);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);

    }
    @Test
    public void createUserWithTwoRoles(){
        User mohamed = new User("mohamedzahran@gmail.com","123456","mohamed","zahran");

        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        mohamed.addRole(roleEditor);
        mohamed.addRole(roleAssistant);

        User savedUser = userRepository.save(mohamed);

        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);

    }

    @Test
    public void testListAllUsers(){

        List<User> users = (List<User>) userRepository.findAll();

        users.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById(){
        User user = userRepository.findById(1).get();
        System.out.println(user);
        Assertions.assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user = userRepository.findById(1).get();

        user.setEnabled(true);
        user.setEmail("zahran@gmail.com");
        userRepository.save(user);
    }

    @Test
    public void testUpdateUserRoles(){
        User user = userRepository.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);

        user.getRoles().remove(roleEditor);
        user.addRole(roleSalesperson);

        userRepository.save(user);
    }

    @Test
    public void testDeleteUser(){
        Integer userId = 2;
        userRepository.deleteById(userId);
    }
}
