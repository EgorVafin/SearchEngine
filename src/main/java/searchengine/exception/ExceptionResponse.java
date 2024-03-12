package searchengine.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class ExceptionResponse {
    private int status;
    private String message;


}
