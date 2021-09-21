package test.cookbook;

import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import test.cookbook.model.Product;
import test.cookbook.model.Recipe;
import test.cookbook.service.ProductService;
import test.cookbook.service.RecipeService;

@SpringBootApplication
public class CookbookApplication {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizeJson() {
        return builder -> {
            builder.indentOutput(true);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(CookbookApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ProductService productService,
                                  RecipeService recipeService) {
        return (args) -> {
            // save products
            Product fish = new Product();
            fish.setName("fish");
            Product chicken = new Product();
            chicken.setName("chicken");
            Product milk = new Product();
            milk.setName("milk");
            Product sugar = new Product();
            sugar.setName("sugar");
            System.out.println(productService.save(fish));
            System.out.println(productService.save(chicken));
            System.out.println(productService.save(milk));
            System.out.println(productService.save(sugar));
            System.out.println(productService.getByName("sugar"));
            System.out.println(productService.getByName("milk"));
            System.out.println(productService.getByName("chicken"));
            System.out.println(productService.getByName("fish"));
            // save recipes
            Recipe friedChicken = new Recipe();
            friedChicken.setName("Fried Chicken");
            friedChicken.setDescription("First do that, than do that");
            List<Product> products = new ArrayList<>();
            products.add(chicken);
            products.add(sugar);
            friedChicken.setProducts(products);
            System.out.println(recipeService.save(friedChicken));
            Recipe sugarFish = new Recipe();
            sugarFish.setName("Sugar Fish");
            sugarFish.setDescription("Very easy");
            sugarFish.setProducts(List.of(fish, sugar));
            System.out.println(recipeService.save(sugarFish));
            recipeService.getAll().forEach(System.out::println);

            // save a child
            List<Product> childList = new ArrayList<>();
            childList.add(milk);
            Recipe child = recipeService.createAChild(friedChicken, childList,
                    "Fried Chicken With Milk",
                    "First do that, than do that, than do the first again");
            System.out.println(child);
            recipeService.getAll().forEach(System.out::println);

            System.out.println("*********************________*******************");
            recipeService.getAllVersions(child).forEach(System.out::println);
            System.out.println("*********************________*******************");
            recipeService.getAll().forEach(System.out::println);
        };
    }

}
