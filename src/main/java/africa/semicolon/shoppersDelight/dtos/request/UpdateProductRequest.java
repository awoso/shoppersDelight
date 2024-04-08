package africa.semicolon.shoppersDelight.dtos.request;

import africa.semicolon.shoppersDelight.data.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequest {
    public Long StoreId;
    public Long ProductId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;
    private Category productCategory;

}
