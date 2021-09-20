package test.cookbook.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.cookbook.dao.ProductRepository;
import test.cookbook.model.Product;
import test.cookbook.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {

        return repository.save(product);
    }

    @Override
    public Optional<Product> getByName(String name) {
        return Optional.ofNullable(repository.findByName(name));
    }

    @Override
    public Product saveIdNotExist(Product product) {
       if (getByName(product.getName()).isEmpty()) {
          return repository.save(product);
       }
       return product;
    }

}
