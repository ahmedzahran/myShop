package org.zahran.myshop.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.zahran.myshop.entities.User;

public interface UserRepository extends CrudRepository<User,Integer> {
}
