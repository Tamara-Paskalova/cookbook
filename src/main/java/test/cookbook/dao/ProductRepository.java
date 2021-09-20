package test.cookbook.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import test.cookbook.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findByName(String  name);
}
