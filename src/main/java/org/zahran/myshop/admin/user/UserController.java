package org.zahran.myshop.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zahran.myshop.entities.Role;
import org.zahran.myshop.entities.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.zahran.myshop.admin.user.UserSpecification.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("")
    public String listAll(@RequestParam("page") Optional<Integer> page,
                          @RequestParam("sortField") Optional<String> sortField,
                          @RequestParam("sortDir") Optional<String> sortDir,
                          @RequestParam("firstName") Optional<String> firstName,
                          @RequestParam("lastName") Optional<String> lastName,
                          @RequestParam("email") Optional<String> email,
                          @RequestParam("from") Optional<String> from,
                          @RequestParam("to") Optional<String> to,
                        Model model) {


        int evalPage = page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);

        String evalSortField = sortField.orElse("id");
        String evalSortDir = sortDir.orElse("asc");
        String evalFirstName = firstName.orElse(null);
        String evalLastName = lastName.orElse(null);
        String evalEmail = email.orElse(null);
        String evalFromDate = from.orElse(null);
        String evalToDate = to.orElse(null);


        
        Specification<User> specification = Specification
                .where(evalFirstName == null ? null :firstNameContains(evalFirstName))
                .and(evalLastName == null ? null : lastNameContains(evalLastName))
                .and(evalEmail  == null ||  evalEmail.isEmpty() ? null : userEmailEqual(evalEmail))
                .and(evalFromDate == null || evalFromDate.isEmpty() ? null : fromDateFilter(evalFromDate))
                .and(evalToDate == null || evalToDate.isEmpty() ? null : toDateFilter(evalToDate));

        Page<User> users = service.listAll(evalPage, evalSortField, evalSortDir, specification);

        List<String> paramNames = new ArrayList<>();
        paramNames.add("sortField");
        paramNames.add("sortDir");
        paramNames.add("firstName");
        paramNames.add("lastName");
        paramNames.add("email");
        paramNames.add("from");

        model.addAttribute("paramNames", paramNames);
        model.addAttribute("users", users);
        model.addAttribute("sortField", evalSortField);
        model.addAttribute("sortDir", evalSortDir);
        model.addAttribute("firstName", evalFirstName);
        model.addAttribute("lastName", evalLastName);
        model.addAttribute("email", evalEmail);
        model.addAttribute("from", evalFromDate);
        model.addAttribute("to", evalToDate);

        return "admin/users/index";
    }

    @GetMapping("/new")
    public String newUser(Model model){

        if (!model.containsAttribute("user")) {
            User user = new User();
            user.setEnabled(true);
            model.addAttribute("user", user);
        }

        List<Role> roles = service.listRoles();

        model.addAttribute("roles",roles);

        return "admin/users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirect) throws IOException {

        if (service.isEmailUnique(user)){
            result.rejectValue("email", "error.user", "An user already exists for this email.");
        }

        if (result.hasErrors()){
            redirect.addFlashAttribute("org.springframework.validation.BindingResult.user",result);
            redirect.addFlashAttribute("user",user);
            System.out.println(result.getAllErrors());
            if (user.getId() != null){
                return "redirect:/users/edit/"+user.getId();
            }
            return "redirect:/users/new";
        }

        MultipartFile multipartFile = user.getImage();

        if (user.getPhotos().isEmpty() && multipartFile.isEmpty()){
            user.setPhotos(null);
        }

        User saved_user = service.saveUser(user);

        if (!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "user-photos/" + saved_user.getId();
            saved_user.setPhotos(fileName);
            service.saveUser(saved_user);
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }

        redirect.addFlashAttribute("message","the user created Successfully");

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Integer id,Model model){

        if (!model.containsAttribute("user")) {
            User user = service.getById(id);
            model.addAttribute("user", user);
        }

        List<Role> roles = service.listRoles();
        model.addAttribute("roles",roles);

        return "admin/users/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id,RedirectAttributes redirect){
        service.deleteUser(id);
        redirect.addFlashAttribute("message","User deleted Success");
        return "redirect:/users";
    }

    //this function need to be improved
    @GetMapping("/export/csv")
    public void exportToCsv(@RequestParam("page") Optional<Integer> page,
                            @RequestParam("sortField") Optional<String> sortField,
                            @RequestParam("sortDir") Optional<String> sortDir,
                            @RequestParam("firstName") Optional<String> firstName,
                            @RequestParam("lastName") Optional<String> lastName,
                            @RequestParam("email") Optional<String> email
            ,HttpServletResponse response) throws IOException {

        int evalPage = page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);

        String evalSortField = sortField.orElse("id");
        String evalSortDir = sortDir.orElse("asc");
        String evalFirstName = firstName.orElse(null);
        String evalLastName = lastName.orElse(null);
        String evalEmail = email.orElse(null);

        Specification<User> specification = Specification
                .where(evalFirstName == null ? null :firstNameContains(evalFirstName))
                .and(evalLastName == null ? null : lastNameContains(evalLastName))
                .and(evalEmail  == null ||  evalEmail.isEmpty() ? null : userEmailEqual(evalEmail));

//        if (page.isPresent() && page.get() == 0){
//            List<User> users = service.getAllUsers();
//            UserCsvExporter exporter= new UserCsvExporter();
//            exporter.export(users,response);
//            return;
//        }
        Page<User> users = service.listAll(evalPage, evalSortField, evalSortDir, specification);

        UserCsvExporter exporter= new UserCsvExporter();
        exporter.export(users.getContent(),response);
    }

    //this function need to be improved
    @GetMapping("/export/excel")
    public void exportExcel(@RequestParam("page") Optional<Integer> page,
                            @RequestParam("sortField") Optional<String> sortField,
                            @RequestParam("sortDir") Optional<String> sortDir,
                            @RequestParam("firstName") Optional<String> firstName,
                            @RequestParam("lastName") Optional<String> lastName,
                            @RequestParam("email") Optional<String> email
            ,HttpServletResponse response) throws IOException {


        int evalPage = page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(0);

        String evalSortField = sortField.orElse("id");
        String evalSortDir = sortDir.orElse("asc");
        String evalFirstName = firstName.orElse(null);
        String evalLastName = lastName.orElse(null);
        String evalEmail = email.orElse(null);

        UserExcelExporter excelExporter = new UserExcelExporter();

        Specification<User> specification = Specification
                .where(evalFirstName == null ? null :firstNameContains(evalFirstName))
                .and(evalLastName == null ? null : lastNameContains(evalLastName))
                .and(evalEmail  == null ||  evalEmail.isEmpty() ? null : userEmailEqual(evalEmail));

        Page<User> users = service.listAll(evalPage, evalSortField, evalSortDir, specification);

        excelExporter.export(users.getContent(),response);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public String userNotFoundHandler(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("message","user not found");
        return "redirect:/users";
    }
}
