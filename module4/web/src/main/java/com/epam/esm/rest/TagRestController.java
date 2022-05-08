package com.epam.esm.rest;

import com.epam.esm.domain.Tag;
import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;
import com.epam.esm.util.SortTypeMapConverter;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagRestController {

    private final TagService tagService;

    private final ModelMapper mapper;

    @GetMapping
    public CollectionModel<TagDto> findAllTags(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size,
                                               @RequestParam(defaultValue = "-id") String sort) {

        Pageable pageable = PageRequest.of(page, size, SortTypeMapConverter.convert(sort));
        Page<TagDto> tags = tagService.findAll(pageable).map(t -> mapper.map(t, TagDto.class));
        tags.forEach(this::addSelfRelLink);
        Link link = linkTo(methodOn(TagRestController.class).findAllTags(page, size, sort)).withSelfRel();
        return CollectionModel.of(tags, link);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> findTagById(@PathVariable @Positive long id) {
        TagDto tagDto = mapper.map(tagService.findById(id), TagDto.class);
        addSelfRelLink(tagDto);
        return ResponseEntity.ok(tagDto);
    }

    @GetMapping("/popular")
    public ResponseEntity<TagDto> findPopularTagOfRichestUser() {
        TagDto tagDto = mapper.map(tagService.findPopularTagOfRichestUser(), TagDto.class);
        addSelfRelLink(tagDto);
        return ResponseEntity.ok(tagDto);
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody @Valid TagDto tagDto) {

        Tag tag = mapper.map(tagDto, Tag.class);
        TagDto dto = mapper.map(tagService.create(tag), TagDto.class);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        tagService.delete(id);
    }

    private void addSelfRelLink(TagDto tagDto) {
        Link link = linkTo(this.getClass()).slash(tagDto.getId()).withSelfRel();
        tagDto.add(link);
    }
}
