package test.cookbook.service;

import java.util.List;
import test.cookbook.model.Product;
import test.cookbook.model.Recipe;

public interface RecipeService {
    Recipe save(Recipe recipe);

    Recipe createAChild(Recipe parent, List<Product> products, String name, String description);

    List<Recipe> getAll();

    List<Recipe> getAllVersions(Recipe recipe);

    Recipe modify(Recipe from, Recipe to);

    Recipe findByName(String name);
}
