package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.CategoryDto;
import com.antigravity.commerce.dto.CategoryRequest;
import com.antigravity.commerce.entity.Category;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:40+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setParentId( categoryParentId( category ) );
        categoryDto.setId( category.getId() );
        categoryDto.setName( category.getName() );
        categoryDto.setSlug( category.getSlug() );
        categoryDto.setDescription( category.getDescription() );

        return categoryDto;
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( request.getName() );
        category.setDescription( request.getDescription() );

        return category;
    }

    @Override
    public void updateEntity(CategoryRequest request, Category category) {
        if ( request == null ) {
            return;
        }

        category.setName( request.getName() );
        category.setDescription( request.getDescription() );
    }

    private UUID categoryParentId(Category category) {
        if ( category == null ) {
            return null;
        }
        Category parent = category.getParent();
        if ( parent == null ) {
            return null;
        }
        UUID id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
