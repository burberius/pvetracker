package net.troja.eve.pve.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error(final HttpServletResponse response, final Model model) {
        final HttpStatus status = HttpStatus.valueOf(response.getStatus());
        final String message = status.value() + " - " + status.getReasonPhrase();
        model.addAttribute("errorMessage", message);
        return "error";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
