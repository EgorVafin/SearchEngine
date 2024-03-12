package searchengine.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(NotFoundHttpException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody ExceptionResponse handleResourceNotFoundException(
            final NotFoundHttpException exception,
            final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage(exception.getMessage());
        error.setStatus(404);
        return error;
    }


}
