package com.example.mysterycard.dto.request.user;

import com.example.mysterycard.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UpdatUserRequest {
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
