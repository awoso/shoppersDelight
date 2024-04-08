package africa.semicolon.shoppersDelight.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AddProductRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;
    private String category;
}


