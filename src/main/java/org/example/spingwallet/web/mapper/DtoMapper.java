package org.example.spingwallet.web.mapper;

import lombok.experimental.UtilityClass;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.web.dto.UserEditRequest;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest mapToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}