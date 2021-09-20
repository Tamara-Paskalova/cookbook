package test.cookbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import test.cookbook.dto.ProductDto;
import test.cookbook.dto.RecipeRequestDto;
import test.cookbook.dto.RecipeResponseDto;
import test.cookbook.model.Product;
import test.cookbook.model.Recipe;
import test.cookbook.service.ProductService;
import test.cookbook.service.RecipeService;
import test.cookbook.util.DateParser;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecipeService recipeService;
    @MockBean
    private ProductService productService;
    @MockBean
    private ModelMapper mapper;

    private static final Map<String, Recipe> map = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        LocalDateTime time = LocalDateTime.now();
        Product fish = new Product();
        fish.setName("fish");
        Product chicken = new Product();
        chicken.setName("chicken");
        Product sugar = new Product();
        sugar.setName("sugar");
        Recipe friedChicken = new Recipe();
        friedChicken.setName("Fried Chicken");
        friedChicken.setDescription("First do that, than do that");
        friedChicken.setProducts(List.of(chicken, sugar));
        friedChicken.setDateTime(time);
        map.put("friedChicken", friedChicken);
        Recipe sugarFish = new Recipe();
        sugarFish.setName("Sugar Fish");
        sugarFish.setDescription("Very easy");
        sugarFish.setProducts(List.of(fish, sugar));
        sugarFish.setDateTime(time);
        map.put("sugarFish", sugarFish);
    }

    @Test
    void create_ValidParameters_OK() throws Exception {
        Recipe recipe = map.get("friedChicken");
        RecipeRequestDto request = getRequest("friedChicken");
        RecipeResponseDto response = getResponse("friedChicken");
        String uri = "/recipe/create";
        when(recipeService.save(any(Recipe.class))).thenReturn(recipe);
        when(mapper.map(recipe, RecipeResponseDto.class)).thenReturn(response);
        when(mapper.map(request, Recipe.class)).thenReturn(recipe);
        String json = mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        RecipeResponseDto answer =objectMapper.readValue(json, RecipeResponseDto.class);
        Assertions.assertEquals(response, answer);
    }

    @Test
    void getAll_Ok() throws Exception {
        Recipe friedChicken = map.get("friedChicken");
        Recipe sugarFish = map.get("sugarFish");
        RecipeResponseDto friedChickenDto = getResponse("friedChicken");
        RecipeResponseDto sugarFishDto = getResponse("sugarFish");
        when(recipeService.getAll()).thenReturn(new ArrayList<>(map.values()));
        when(mapper.map(sugarFish, RecipeResponseDto.class)).thenReturn(sugarFishDto);
        when(mapper.map(friedChicken, RecipeResponseDto.class)).thenReturn(friedChickenDto);
        mockMvc.perform(get("/recipe/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Fried Chicken")))
                .andExpect(jsonPath("$[1].name", is("Sugar Fish")));
    }

    @Test
    void getAllChildren_Ok() throws Exception {
        Recipe friedChicken = map.get("friedChicken");
        Recipe sugarFish = map.get("sugarFish");
        RecipeResponseDto friedChickenDto = getResponse("friedChicken");
        RecipeResponseDto sugarFishDto = getResponse("sugarFish");
        RecipeRequestDto friedChickenRequest = getRequest("friedChicken");
        when(recipeService.findByName(any())).thenReturn(friedChicken);
        when(recipeService.getAllVersions(friedChicken)).thenReturn(new ArrayList<>(map.values()));
        when(mapper.map(friedChickenRequest, Recipe.class)).thenReturn(friedChicken);
        when(mapper.map(sugarFish, RecipeResponseDto.class)).thenReturn(sugarFishDto);
        when(mapper.map(friedChicken, RecipeResponseDto.class)).thenReturn(friedChickenDto);

        mockMvc.perform(get("/recipe/all-children")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friedChickenRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Fried Chicken")))
                .andExpect(jsonPath("$[1].name", is("Sugar Fish")));

    }

    @Test
    void createChild_Ok() throws Exception {
        RecipeRequestDto sugarFishRequest = getRequest("sugarFish");
        RecipeRequestDto friedChickenRequest = getRequest("friedChicken");
        Recipe sugarFish = map.get("sugarFish");
        Recipe friedChicken = map.get("friedChicken");
        List<RecipeRequestDto> list = List.of(sugarFishRequest, friedChickenRequest);
        when(mapper.map(sugarFishRequest, Recipe.class)).thenReturn(sugarFish);
        when(mapper.map(friedChickenRequest, Recipe.class)).thenReturn(friedChicken);
        when(mapper.map(sugarFish, RecipeResponseDto.class)).thenReturn(getResponse("sugarFish"));
        when(recipeService.createAChild(any(), any(), any(), any())).thenReturn(sugarFish);
        String content = mockMvc.perform(post("/recipe/create-child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        RecipeResponseDto expected = objectMapper.readValue(content, RecipeResponseDto.class);
        Assertions.assertEquals(getResponse("sugarFish"), expected);
    }

    @Test
//    void modify_Ok() throws Exception {
//        RecipeRequestDto sugarFishRequest = getRequest("sugarFish");
//        Recipe sugarFish = map.get("sugarFish");
//        List<RecipeRequestDto> list = List.of(sugarFishRequest, sugarFishRequest);
//        when(mapper.map(sugarFishRequest, Recipe.class)).thenReturn(sugarFish);
//        when(mapper.map(sugarFish, RecipeResponseDto.class)).thenReturn(getResponse("sugarFish"));
//        String content = mockMvc.perform(post("/recipe/modify")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(list)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse().getContentAsString();
//        RecipeResponseDto expected = objectMapper.readValue(content, RecipeResponseDto.class);
//        Assertions.assertEquals(getResponse("sugarFish"), expected);
//    }

    private RecipeRequestDto getRequest(String recipeName) {
        Recipe recipe = map.get(recipeName);
        RecipeRequestDto request = new RecipeRequestDto();
        request.setName(recipe.getName());
        request.setDescription(recipe.getDescription());
        List<ProductDto> collect = recipe.getProducts().stream()
                .map(product -> new ProductDto(product.getName()))
                .collect(Collectors.toList());
        request.setProducts(collect);
        return request;
    }

    private RecipeResponseDto getResponse(String responseName) {
        Recipe recipe = map.get(responseName);
        RecipeResponseDto response = new RecipeResponseDto();
        response.setName(recipe.getName());
        response.setDescription(recipe.getDescription());
        response.setDate(DateParser.dateToString(recipe.getDateTime()));
        List<ProductDto> collect = recipe.getProducts().stream()
                .map(product -> new ProductDto(product.getName()))
                .collect(Collectors.toList());
        response.setProducts(collect);
        return response;
    }
}