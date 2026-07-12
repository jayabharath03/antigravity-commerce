package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.AttributeValueDto;
import com.antigravity.commerce.dto.ProductDto;
import com.antigravity.commerce.dto.ProductImageDto;
import com.antigravity.commerce.dto.ProductRequest;
import com.antigravity.commerce.dto.ProductVariantDto;
import com.antigravity.commerce.entity.AttributeValue;
import com.antigravity.commerce.entity.Product;
import com.antigravity.commerce.entity.ProductImage;
import com.antigravity.commerce.entity.ProductVariant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:41+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public ProductDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setId( product.getId() );
        productDto.setName( product.getName() );
        productDto.setSlug( product.getSlug() );
        productDto.setShortDescription( product.getShortDescription() );
        productDto.setLongDescription( product.getLongDescription() );
        productDto.setCategory( categoryMapper.toDto( product.getCategory() ) );
        productDto.setBrand( brandMapper.toDto( product.getBrand() ) );
        productDto.setStatus( product.getStatus() );
        productDto.setSeoTitle( product.getSeoTitle() );
        productDto.setSeoDescription( product.getSeoDescription() );
        productDto.setTaxClass( product.getTaxClass() );
        productDto.setHsnCode( product.getHsnCode() );
        productDto.setCountryOfOrigin( product.getCountryOfOrigin() );
        productDto.setManufacturer( product.getManufacturer() );
        productDto.setWarrantyPeriod( product.getWarrantyPeriod() );
        productDto.setReturnable( product.getReturnable() );
        productDto.setReturnDays( product.getReturnDays() );
        productDto.setCreatedAt( product.getCreatedAt() );
        productDto.setUpdatedAt( product.getUpdatedAt() );
        productDto.setVariants( productVariantSetToProductVariantDtoList( product.getVariants() ) );
        productDto.setImages( productImageSetToProductImageDtoList( product.getImages() ) );

        return productDto;
    }

    @Override
    public Product toEntity(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product product = new Product();

        product.setName( request.getName() );
        product.setShortDescription( request.getShortDescription() );
        product.setLongDescription( request.getLongDescription() );
        product.setStatus( request.getStatus() );
        product.setSeoTitle( request.getSeoTitle() );
        product.setSeoDescription( request.getSeoDescription() );
        product.setTaxClass( request.getTaxClass() );
        product.setHsnCode( request.getHsnCode() );
        product.setCountryOfOrigin( request.getCountryOfOrigin() );
        product.setManufacturer( request.getManufacturer() );
        product.setWarrantyPeriod( request.getWarrantyPeriod() );
        product.setReturnable( request.getReturnable() );
        product.setReturnDays( request.getReturnDays() );

        return product;
    }

    @Override
    public void updateEntity(ProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setName( request.getName() );
        product.setShortDescription( request.getShortDescription() );
        product.setLongDescription( request.getLongDescription() );
        product.setStatus( request.getStatus() );
        product.setSeoTitle( request.getSeoTitle() );
        product.setSeoDescription( request.getSeoDescription() );
        product.setTaxClass( request.getTaxClass() );
        product.setHsnCode( request.getHsnCode() );
        product.setCountryOfOrigin( request.getCountryOfOrigin() );
        product.setManufacturer( request.getManufacturer() );
        product.setWarrantyPeriod( request.getWarrantyPeriod() );
        product.setReturnable( request.getReturnable() );
        product.setReturnDays( request.getReturnDays() );
    }

    protected ProductImageDto productImageToProductImageDto(ProductImage productImage) {
        if ( productImage == null ) {
            return null;
        }

        ProductImageDto productImageDto = new ProductImageDto();

        productImageDto.setId( productImage.getId() );
        productImageDto.setImageUrl( productImage.getImageUrl() );
        productImageDto.setDisplayOrder( productImage.getDisplayOrder() );
        productImageDto.setIsPrimary( productImage.getIsPrimary() );

        return productImageDto;
    }

    protected List<ProductImageDto> productImageSetToProductImageDtoList(Set<ProductImage> set) {
        if ( set == null ) {
            return null;
        }

        List<ProductImageDto> list = new ArrayList<ProductImageDto>( set.size() );
        for ( ProductImage productImage : set ) {
            list.add( productImageToProductImageDto( productImage ) );
        }

        return list;
    }

    protected AttributeValueDto attributeValueToAttributeValueDto(AttributeValue attributeValue) {
        if ( attributeValue == null ) {
            return null;
        }

        AttributeValueDto attributeValueDto = new AttributeValueDto();

        attributeValueDto.setId( attributeValue.getId() );
        attributeValueDto.setValue( attributeValue.getValue() );

        return attributeValueDto;
    }

    protected List<AttributeValueDto> attributeValueSetToAttributeValueDtoList(Set<AttributeValue> set) {
        if ( set == null ) {
            return null;
        }

        List<AttributeValueDto> list = new ArrayList<AttributeValueDto>( set.size() );
        for ( AttributeValue attributeValue : set ) {
            list.add( attributeValueToAttributeValueDto( attributeValue ) );
        }

        return list;
    }

    protected ProductVariantDto productVariantToProductVariantDto(ProductVariant productVariant) {
        if ( productVariant == null ) {
            return null;
        }

        ProductVariantDto productVariantDto = new ProductVariantDto();

        productVariantDto.setId( productVariant.getId() );
        productVariantDto.setSku( productVariant.getSku() );
        productVariantDto.setBarcode( productVariant.getBarcode() );
        productVariantDto.setPrice( productVariant.getPrice() );
        productVariantDto.setCostPrice( productVariant.getCostPrice() );
        productVariantDto.setStockQuantity( productVariant.getStockQuantity() );
        productVariantDto.setWeight( productVariant.getWeight() );
        productVariantDto.setLength( productVariant.getLength() );
        productVariantDto.setWidth( productVariant.getWidth() );
        productVariantDto.setHeight( productVariant.getHeight() );
        productVariantDto.setStatus( productVariant.getStatus() );
        productVariantDto.setLowStockThreshold( productVariant.getLowStockThreshold() );
        productVariantDto.setAllowBackorder( productVariant.getAllowBackorder() );
        productVariantDto.setMinOrderQuantity( productVariant.getMinOrderQuantity() );
        productVariantDto.setMaxOrderQuantity( productVariant.getMaxOrderQuantity() );
        productVariantDto.setVariantImages( productImageSetToProductImageDtoList( productVariant.getVariantImages() ) );
        productVariantDto.setAttributeValues( attributeValueSetToAttributeValueDtoList( productVariant.getAttributeValues() ) );

        return productVariantDto;
    }

    protected List<ProductVariantDto> productVariantSetToProductVariantDtoList(Set<ProductVariant> set) {
        if ( set == null ) {
            return null;
        }

        List<ProductVariantDto> list = new ArrayList<ProductVariantDto>( set.size() );
        for ( ProductVariant productVariant : set ) {
            list.add( productVariantToProductVariantDto( productVariant ) );
        }

        return list;
    }
}
