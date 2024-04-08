package africa.semicolon.shoppersDelight.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddProductResponse  {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;
}
