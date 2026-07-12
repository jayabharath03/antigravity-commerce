package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.BrandDto;
import com.antigravity.commerce.dto.BrandRequest;
import com.antigravity.commerce.entity.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:41+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public BrandDto toDto(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandDto brandDto = new BrandDto();

        brandDto.setId( brand.getId() );
        brandDto.setName( brand.getName() );
        brandDto.setSlug( brand.getSlug() );
        brandDto.setDescription( brand.getDescription() );
        brandDto.setLogoUrl( brand.getLogoUrl() );
        brandDto.setWebsite( brand.getWebsite() );
        brandDto.setCountry( brand.getCountry() );
        brandDto.setActive( brand.getActive() );

        return brandDto;
    }

    @Override
    public Brand toEntity(BrandRequest request) {
        if ( request == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setName( request.getName() );
        brand.setDescription( request.getDescription() );
        brand.setLogoUrl( request.getLogoUrl() );
        brand.setWebsite( request.getWebsite() );
        brand.setCountry( request.getCountry() );
        brand.setActive( request.getActive() );

        return brand;
    }

    @Override
    public void updateEntity(BrandRequest request, Brand brand) {
        if ( request == null ) {
            return;
        }

        brand.setName( request.getName() );
        brand.setDescription( request.getDescription() );
        brand.setLogoUrl( request.getLogoUrl() );
        brand.setWebsite( request.getWebsite() );
        brand.setCountry( request.getCountry() );
        brand.setActive( request.getActive() );
    }
}
