package org.zahran.myshop.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zahran.myshop.entities.Role;
import org.zahran.myshop.entities.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Page<User> listAll(Integer page, String sortField, String sortDir, Specification<User> specification){

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page,5, sort);

        return  userRepository.findAll(specification,pageable);
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRepository.findAll();
    }

    public User saveUser(User user) {
        encodePassword(user);
        return userRepository.save(user);
    }

    private void encodePassword(User user){

        if (user.getId() != null && user.getPassword().isEmpty()){
            User existingUser = userRepository.findById(user.getId()).get();
            user.setPassword(existingUser.getPassword());
            return;
        }

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public boolean isEmailUnique(User user){

        Optional<User> userByEmail = userRepository.getUserByEmail(user.getEmail());

        if (userByEmail.isPresent() && user.getId() != null){
            User u = userByEmail.get();
            if (u.getEmail().equals(user.getEmail()) && u.getId().equals(user.getId())){
                return false;
            }
            return true;
        }

        if (userByEmail.isPresent() && user.getId() == null){
            return true;
        }

        return false;
    }

    public User getById(Integer id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()){
            return user.get();
        }

        throw new UserNotFoundException("User Not Found");
    }

    public void deleteUser(Integer id){
        User user = getById(id);
        userRepository.delete(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
