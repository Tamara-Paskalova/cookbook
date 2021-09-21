package test.cookbook.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.cookbook.dao.RecipeRepository;
import test.cookbook.model.Product;
import test.cookbook.model.Recipe;
import test.cookbook.service.ProductService;
import test.cookbook.service.RecipeService;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository repository;
    private final ProductService service;

    public RecipeServiceImpl(RecipeRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    @Transactional
    public Recipe save(Recipe recipe) {
        recipe.setDateTime(LocalDateTime.now());
        List<Product> productList = recipe.getProducts().stream()
                .peek(service::save)
                .collect(Collectors.toList());
        recipe.setProducts(productList);
        return repository.save(recipe);
    }

    @Override
    public Recipe createAChild(Recipe recipe, List<Product> products,
                               String name, String description) {
        Recipe parent = repository.findByName(recipe.getName()).get();
        List<Product> childList = parent.getProducts();
        childList.addAll(products.stream()
                .map(service::save)
                .collect(Collectors.toList()));
        if (parent.getParentId() == null) {
            parent.setParentId(parent.getId());
            repository.save(parent);
        }
        Recipe child = new Recipe();
        child.setName(name);
        child.setProducts(new ArrayList<>(childList));
        child.setDescription(description);
        child.setDateTime(LocalDateTime.now());
        child.setParentId(parent.getParentId());
        return repository.save(child);
    }

    @Override
    public List<Recipe> getAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipe> getAllVersions(Recipe recipe) {
        return repository.findAllByParentId(recipe.getParentId(), recipe.getId());
    }

    @Override
    public Recipe modify(Recipe from, Recipe to) {
        Recipe recipe = repository.findByName(from.getName()).get();
        recipe.setName(to.getName());
        recipe.setDescription(to.getDescription());
        recipe.setProducts(new ArrayList<>(to.getProducts()));
        return repository.save(recipe);
    }

    @Override
    public Recipe findByName(String name) {
        return repository.findByName(name).get();
    }
}
