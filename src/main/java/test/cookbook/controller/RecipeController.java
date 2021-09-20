package test.cookbook.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import test.cookbook.dto.RecipeRequestDto;
import test.cookbook.dto.RecipeResponseDto;
import test.cookbook.model.Recipe;
import test.cookbook.service.RecipeService;
import test.cookbook.util.DateParser;

@Validated
@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final ModelMapper modelMapper;
    private final RecipeService recipeService;
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    public RecipeController(ModelMapper modelMapper, RecipeService recipeService) {
        this.modelMapper = modelMapper;
        this.recipeService = recipeService;
        this.modelMapper.addConverter((Converter<LocalDateTime, String>) context -> DateParser.dateToString(context.getSource()));
        this.modelMapper.addConverter((Converter<String, LocalDateTime>) context -> DateParser.stringToDate(context.getSource()));
    }

    @GetMapping
    public String pageView() {
        return "";
    }

    @PostMapping("/create")
    public RecipeResponseDto create(@Valid @RequestBody RecipeRequestDto dto) {
        Recipe recipe = modelMapper.map(dto, Recipe.class);
        Recipe saved = recipeService.save(recipe);
        return modelMapper.map(saved, RecipeResponseDto.class);
    }

    @GetMapping("/all")
    @ResponseBody
    public List<RecipeResponseDto> getAll() {
        return recipeService.getAll()
                .stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDto.class))
                .sorted(Comparator.comparing(RecipeResponseDto::getName))
                .collect(Collectors.toList());
    }

    @GetMapping("/all-children")
    public List<RecipeResponseDto> getAllChildren(@RequestBody RecipeRequestDto dto) {
        Recipe recipe = recipeService.findByName(modelMapper.map(dto, Recipe.class).getName());
        return recipeService.getAllVersions(recipe).stream()
                .map(r -> modelMapper.map(r, RecipeResponseDto.class))
                .sorted(Comparator.comparing(RecipeResponseDto::getName))
                .collect(Collectors.toList());
    }

    @PostMapping("/create-child")
    public RecipeResponseDto createChild(@RequestBody List<RecipeRequestDto> dtoList) {
        Recipe parent = modelMapper.map(dtoList.get(FIRST_INDEX), Recipe.class);
        Recipe child = modelMapper.map(dtoList.get(SECOND_INDEX), Recipe.class);
        Recipe created = recipeService.createAChild(parent, child.getProducts(),
                child.getName(), child.getDescription());
        return modelMapper.map(created, RecipeResponseDto.class);
    }

    @PostMapping("/modify")
    public RecipeResponseDto modify(@RequestBody List<RecipeRequestDto> list) {
        Recipe origin = modelMapper.map(list.get(FIRST_INDEX), Recipe.class);
        Recipe modified = modelMapper.map(list.get(SECOND_INDEX), Recipe.class);
        Recipe recipe = recipeService.modify(origin, modified);
        return modelMapper.map(recipe, RecipeResponseDto.class);


    }
}
