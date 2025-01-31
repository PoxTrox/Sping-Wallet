package org.example.spingwallet.web.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder

public class UserEditRequest{

    @Size(max = 25 ,message = "First name can't have more then 25 symbols")

    private String firstName;

    @Size(max = 25, message = "Last name can't have more then 25 symbols")

    private String lastName;

    @Email  (message = "Requires valid email")
    private String email;

    @URL (message = "Requires valid URL")
    private String profilePicture;

}
