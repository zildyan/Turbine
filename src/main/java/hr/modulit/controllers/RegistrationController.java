package hr.modulit.controllers;

import hr.modulit.dto.UserData;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.persistence.models.User;
import hr.modulit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;
import static hr.modulit.Constants.TOKEN_EXPIRED;
import static hr.modulit.Constants.TOKEN_VALID;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserData userData,
                                            BindingResult result, WebRequest request, Errors errors) {
        User user = new User();

        if (!result.hasErrors())
            user = createUserAccount(userData, result);

        if (user == null)
            result.rejectValue("email", "message.regError");

        if (result.hasErrors())
            return new ModelAndView("registration", "user", userData);

        return new ModelAndView("registrationSuccessful", "user", userData);

    }

    private User createUserAccount(UserData accountDto, BindingResult result) {
        try {
            return userService.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            return null;
        }
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(final Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        final String result = userService.validateVerificationToken(token);

        if (result.equals(TOKEN_VALID)) {
            model.addAttribute("message", getMessage("auth.message.accountVerified"));
            return "redirect:/login";
        }

        model.addAttribute("message", getMessage("auth.message." + result));
        model.addAttribute("expired", TOKEN_EXPIRED.equals(result));
        model.addAttribute("token", token);
        return "redirect:/badUser";
    }

    private String getMessage(String messageKey) {
        return messages.getMessage(messageKey, null, null);
    }
}
