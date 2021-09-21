package test.cookbook.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class RecipeResponseDto implements Serializable {
    private String name;
    private String description;
    private String date;
    private List<ProductDto> products;
}
