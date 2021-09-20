package test.cookbook.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto implements Serializable {
    private String name;

    public ProductDto(String name) {
        this.name = name;
    }
}
