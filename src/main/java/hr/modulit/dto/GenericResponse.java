package hr.modulit.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(final String message) {
        this.message = message;
    }

    public GenericResponse(final List<FieldError> fieldErrors, final List<ObjectError> globalErrors) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            this.message = mapper.writeValueAsString(fieldErrors);
            this.error = mapper.writeValueAsString(globalErrors);
        } catch (final JsonProcessingException e) {
            this.message = "";
            this.error = "";
        }
    }
}
