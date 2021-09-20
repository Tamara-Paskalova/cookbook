package test.cookbook.service;

import java.util.Optional;
import test.cookbook.model.Product;

public interface ProductService {
    Product save(Product product);

    Optional<Product> getByName(String name);

    Product saveIdNotExist(Product product);
}
