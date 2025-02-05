package org.example.spingwallet.web;


import jakarta.validation.Valid;
import org.example.spingwallet.security.RequireAdminRole;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.service.UserService;
import org.example.spingwallet.web.dto.UserEditRequest;
import org.example.spingwallet.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenu(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(id);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("editRequest", DtoMapper.mapToUserEditRequest(user));

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateProfileMenu(@PathVariable UUID id, @Valid @ModelAttribute UserEditRequest editRequest, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-menu");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editRequest", editRequest);
            return modelAndView;

        }

        userService.editUser(id, editRequest);
        return new ModelAndView("redirect:/home");

    }

    @RequireAdminRole
    @GetMapping()
    public ModelAndView getAllUsers() {
        ModelAndView modelAndView = new ModelAndView();
        List<User> allUsers = userService.getAllUsers();
        modelAndView.addObject("users", allUsers);
        modelAndView.setViewName("users");
        return modelAndView;
    }


    @PutMapping("/{id}/status")
    public String updateProfileStatus(@PathVariable UUID id) {

        userService.changeStatus(id);

        return "redirect:/users";

    }

    @PutMapping("/{id}/role")
    public String updateProfileRole(@PathVariable UUID id) {

        userService.changeUserRole(id);

        return "redirect:/users";

    }
}
