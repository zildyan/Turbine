package hr.modulit.controllers;

import hr.modulit.dto.AccountData;
import hr.modulit.dto.GenericResponse;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.services.AccountService;
import hr.modulit.services.CompanyService;
import hr.modulit.services.UserService;
import hr.modulit.async.events.SendEmailOnRegistrationCompleteEvent;
import hr.modulit.async.events.SendEmailOnResendVerificationTokenEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static hr.modulit.Constants.TOKEN_EXPIRED;
import static hr.modulit.Constants.TOKEN_VALID;
import static hr.modulit.utils.TurbineUtil.getAppUrl;

@Controller
public class RegistrationController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(final HttpServletRequest request, final Model model) {
        final AccountData accountData = new AccountData();
        model.addAttribute("account", accountData);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerAccount(@ModelAttribute("account") @Valid AccountData accountData,
                                            final HttpServletRequest request, BindingResult result, Errors errors) {
        Account account = null;

        if(!result.hasErrors())
            account = registerNewAccount(accountData, result);

        if (result.hasErrors())
            return new ModelAndView("registration", "account", accountData);

        eventPublisher.publishEvent(new SendEmailOnRegistrationCompleteEvent(account, getAppUrl(request)));
        return new ModelAndView("registrationSuccessful", "account", accountData);
    }

    private Account registerNewAccount(AccountData accountData, BindingResult result) {
        try {
            if (accountData.isCompanyAccount())
                return companyService.registerNewCompanyAccount(accountData);
            else
                return userService.registerNewUserAccount(accountData);

        } catch (EmailExistsException e){
            result.rejectValue("email", "message.regError");
            return null;
        }
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(final Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        final String result = accountService.validateVerificationToken(token);

        if (result.equals(TOKEN_VALID)) {
            accountService.enableAccount(token);
            model.addAttribute("message", getMessage("auth.message.accountVerified"));
            return "redirect:/login";
        }

        model.addAttribute("message", getMessage("auth.message." + result));
        model.addAttribute("expired", TOKEN_EXPIRED.equals(result));
        model.addAttribute("token", token);
        return "redirect:/badUser";
    }

    @RequestMapping(value = "/resendVerificationToken", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendVerificationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final Token newToken = accountService.generateNewVerificationToken(existingToken);
        final Account account = accountService.getAccountByVerificationToken(newToken.getToken());
        eventPublisher.publishEvent(new SendEmailOnResendVerificationTokenEvent(account, getAppUrl(request)));
        return new GenericResponse(getMessage("resendToken"));
    }

    private String getMessage(String messageKey) {
        return messages.getMessage(messageKey, null, null);
    }


    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String email) {
        final Account account = accountService.getAccountByEmail(email);
        if (account == null) {
        }



        return new GenericResponse(messages.getMessage("resetPasswordEmail", null, request.getLocale()));
    }


}
