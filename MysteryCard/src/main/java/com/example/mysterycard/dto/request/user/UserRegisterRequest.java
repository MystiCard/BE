package com.example.mysterycard.dto.request.user;

import com.example.mysterycard.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterRequest {
    @Email(message = "Email invalid")
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password at least 8 character and at least 1 letter and at least 1 number")
    private String password;
    @NotNull(message = "Please choose gender")
    private Gender gender;
    @NotNull(message = "Address is required")
    @Size(max = 255, min= 5 , message = "Address must be from 5 to 255 character")
    private String address;
    @NotNull(message = "Name is required")
    @Size(max = 255, min= 5 , message = "Name must be from 5 to 255 character")
    private String name;
    @NotNull(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",message = "Phone must be start 0 or +84 followed by 9 digit  ")
    private String phone;
    @NotNull(message = "District ID is required")
    private String districtId;
    @NotNull(message = "Ward Id is required")
    private String wardId;
}
