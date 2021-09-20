package test.cookbook.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class RecipeRequestDto implements Serializable {
    @NotEmpty(message = "Name can not be empty")
    private String name;
    @NotEmpty(message = "Description can not be empty")
    private String description;
    private List<ProductDto> products;
}
