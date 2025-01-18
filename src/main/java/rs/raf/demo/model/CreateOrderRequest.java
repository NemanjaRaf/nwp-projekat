package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CreateOrderRequest {
    @NotBlank(message = "Username is mandatory")
    private List<Long> dishes;

    private LocalDateTime scheduledTime;
}
