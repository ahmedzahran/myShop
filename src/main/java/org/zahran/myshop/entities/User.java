package org.zahran.myshop.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;
import org.zahran.myshop.admin.user.PasswordNotNullOnFirstSave;
import org.zahran.myshop.admin.user.ValidImage;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@PasswordNotNullOnFirstSave(message = "password error")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Email
    @Size(min = 2,max = 128)
    @Column(length = 128,nullable = false,unique = true)
    private String email;

    @Column(length = 64,nullable = false)
    private String password;

    @NotEmpty
    @Size(min = 3,max = 45)
    @Column(name = "first_name",length = 45,nullable = false)
    private String firstName;

    @NotEmpty
    @Size(min = 3,max = 45)
    @Column(name = "last_name",length = 45,nullable = false)
    private String lastName;

    @Column(length = 64)
    private String photos;

    @Transient
    @ValidImage
    private MultipartFile image;

    private boolean enabled;

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Valid
    @NotEmpty(message = "at least one role must chosen")
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;


    public void addRole(Role role){
        roles.add(role);
    }

    public String getPhotosImagePath(){
        if (this.getId() == null || this.getPhotos() == null){
            return "/images/default-image.png";
        }
        return "/user-photos/" + this.getId() + "/" + this.getPhotos();

    }
}
