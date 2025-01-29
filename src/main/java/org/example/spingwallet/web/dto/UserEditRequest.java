package org.example.spingwallet.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditRequest{

    @Size(max = 25 ,message = "First name can't have more then 25 symbols")
    private String firstName;

    @Size(max = 25, message = "Last name can't have more then 25 symbols")
    private String lastName;

    @Email
    private String email;

    @URL
    private String profilePicture;

}
