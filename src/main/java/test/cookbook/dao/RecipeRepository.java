package test.cookbook.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import test.cookbook.model.Recipe;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    @Query("select distinct r from Recipe r join fetch r.products where r.parentId=:parentId and r.id <:id ")
    List<Recipe> findAllByParentId(Long parentId, Long id);

    @Query("select distinct r from Recipe r join fetch r.products")
    Iterable<Recipe> findAll();

    @Query("select distinct r from Recipe r join fetch r.products where r.id=:id")
    Optional<Recipe> findById(Long id);

    @Query("select distinct r from Recipe r join fetch r.products where r.name=:name")
    Optional<Recipe> findByName(String name);
}
