package hr.modulit.controllers;

import hr.modulit.dto.AccountData;
import hr.modulit.dto.GenericResponse;
import hr.modulit.dto.PasswordData;
import hr.modulit.emails.events.SendEmailOnForgottenPasswordEvent;
import hr.modulit.emails.events.SendEmailOnRegistrationCompleteEvent;
import hr.modulit.emails.events.SendEmailOnResendVerificationTokenEvent;
import hr.modulit.enums.TokenValidationStatus;
import hr.modulit.exceptions.EmailExistsException;
import hr.modulit.exceptions.InvalidAccountIdAuthenticationException;
import hr.modulit.persistence.models.Account;
import hr.modulit.persistence.models.Token;
import hr.modulit.services.AccountService;
import hr.modulit.services.CompanyService;
import hr.modulit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

import static hr.modulit.enums.TokenValidationStatus.EXPIRED_TOKEN;
import static hr.modulit.enums.TokenValidationStatus.VALID_TOKEN;
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
        final TokenValidationStatus tokenValidationStatus = accountService.validateToken(token);

        if (tokenValidationStatus.equals(VALID_TOKEN)) {
            accountService.enableAccount(token);
            model.addAttribute("message", getMessage("auth.message.accountVerified"));
            return "redirect:/login";
        }

        model.addAttribute("message", getMessage("auth.message." + tokenValidationStatus.getDescription()));
        model.addAttribute("expired", EXPIRED_TOKEN.equals(tokenValidationStatus));
        model.addAttribute("token", token);
        return "redirect:/badUser";
    }

    @RequestMapping(value = "/resendVerificationToken", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendVerificationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final Token newToken = accountService.generateNewVerificationToken(existingToken);
        eventPublisher.publishEvent(new SendEmailOnResendVerificationTokenEvent(newToken.getAccount(), getAppUrl(request)));
        return new GenericResponse(getMessage("resendToken"));
    }

    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse forgotPassword(final HttpServletRequest request, @RequestParam("email") final String email) {
        final Account account = accountService.getAccountByEmail(email);
        if (account == null)
            return new GenericResponse(null, getMessage("forgottenPassword.account.notExists"));

        eventPublisher.publishEvent(new SendEmailOnForgottenPasswordEvent(account, getAppUrl(request)));
        return new GenericResponse(getMessage("forgottenPassword.email.sent"));
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(final HttpServletRequest request, final Model model,  @RequestParam("id") final String id, @RequestParam("token") final String token){
        final TokenValidationStatus tokenValidationStatus = accountService.validateToken(token);

        if(VALID_TOKEN.equals(tokenValidationStatus)) {
            try {
                accountService.authenticateAccountUserWithToken(id, token);
                return "redirect:/updatePassword.html";
            } catch (InvalidAccountIdAuthenticationException e) {
                model.addAttribute("message", getMessage("auth.message.invalidAccountId"));
                return "redirect:/login.html";
            }
        }

        model.addAttribute("message", getMessage("auth.message" + tokenValidationStatus.getDescription()));
        return "redirect:/login.html";
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @PreAuthorize("hasRole('READ_PRIVILEGE')")
    @ResponseBody
    public GenericResponse changeUserPassword(@ModelAttribute("passwordData") @Valid PasswordData passwordData) {
        String email = ((Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        final Account account = accountService.getAccountByEmail(email);

        // TODO: 16.12.2016. error handling
        accountService.validatePasswords(account, passwordData);

        accountService.changeAccountPassword(account, passwordData.getNewPassword());
        return new GenericResponse(getMessage("message.updatePasswordSuc"));
    }


    // TODO: 16.12.2016. error handling
    @ExceptionHandler(RuntimeException.class)
    public GenericResponse handleError(HttpServletRequest req, Exception ex) {
        return new GenericResponse(null, getMessage(""));
    }

    private String getMessage(String messageKey) {
        return messages.getMessage(messageKey, null, null);
    }
}

