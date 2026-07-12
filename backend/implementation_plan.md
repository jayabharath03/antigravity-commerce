# Phase 2: Enterprise Product Catalog Management (Finalized)

This plan outlines the finalized architecture for a highly scalable, production-ready Product Catalog that supports unlimited product variants, dynamic attributes, enterprise soft-deletes, and advanced e-commerce fields.

## 1. Database Architecture & ER Diagram

### ER Diagram

```mermaid
erDiagram
    CATEGORIES ||--o{ CATEGORIES : "parent_id"
    CATEGORIES ||--o{ PRODUCTS : "category_id"
    BRANDS ||--o{ PRODUCTS : "brand_id"

    PRODUCTS ||--o{ PRODUCT_VARIANTS : "product_id"
    PRODUCTS ||--o{ PRODUCT_IMAGES : "product_id"
    PRODUCT_VARIANTS ||--o{ PRODUCT_IMAGES : "variant_id"
    
    ATTRIBUTES ||--o{ ATTRIBUTE_VALUES : "attribute_id"
    PRODUCTS ||--o{ PRODUCT_ATTRIBUTES : "product_id"
    ATTRIBUTES ||--o{ PRODUCT_ATTRIBUTES : "attribute_id"
    
    PRODUCT_VARIANTS ||--o{ VARIANT_ATTRIBUTE_VALUES : "variant_id"
    ATTRIBUTE_VALUES ||--o{ VARIANT_ATTRIBUTE_VALUES : "attribute_value_id"

    CATEGORIES {
        UUID id PK
        VARCHAR name
        VARCHAR slug
        VARCHAR description
        UUID parent_id FK
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        VARCHAR created_by
        VARCHAR updated_by
        VARCHAR deleted_by
    }
    
    BRANDS {
        UUID id PK
        VARCHAR name
        VARCHAR slug
        VARCHAR description
        VARCHAR logo_url
        VARCHAR website
        VARCHAR country
        BOOLEAN active
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        VARCHAR created_by
        VARCHAR updated_by
        VARCHAR deleted_by
    }

    PRODUCTS {
        UUID id PK
        VARCHAR name
        VARCHAR slug
        TEXT short_description
        TEXT long_description
        UUID category_id FK
        UUID brand_id FK
        VARCHAR status "DRAFT, ACTIVE, etc"
        VARCHAR seo_title
        TEXT seo_description
        VARCHAR tax_class
        VARCHAR hsn_code
        VARCHAR country_of_origin
        VARCHAR manufacturer
        VARCHAR warranty_period
        BOOLEAN returnable
        INT return_days
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        VARCHAR created_by
        VARCHAR updated_by
        VARCHAR deleted_by
    }

    PRODUCT_VARIANTS {
        UUID id PK
        UUID product_id FK
        VARCHAR sku "Unique, e.g. IPH16-BLK-128"
        VARCHAR barcode "Optional"
        DECIMAL price
        DECIMAL cost_price
        INT stock_quantity
        DECIMAL weight
        DECIMAL length
        DECIMAL width
        DECIMAL height
        VARCHAR status
        INT low_stock_threshold
        BOOLEAN allow_backorder
        INT min_order_quantity
        INT max_order_quantity
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
        VARCHAR created_by
        VARCHAR updated_by
        VARCHAR deleted_by
    }

    PRODUCT_IMAGES {
        UUID id PK
        UUID product_id FK
        UUID variant_id FK "Nullable"
        VARCHAR image_url
        INT display_order
        BOOLEAN is_primary
    }

    ATTRIBUTES {
        UUID id PK
        VARCHAR name "e.g., Color, Size"
    }

    ATTRIBUTE_VALUES {
        UUID id PK
        UUID attribute_id FK
        VARCHAR value "e.g., Black, XL"
    }

    PRODUCT_ATTRIBUTES {
        UUID product_id PK,FK
        UUID attribute_id PK,FK
    }

    VARIANT_ATTRIBUTE_VALUES {
        UUID variant_id PK,FK
        UUID attribute_value_id PK,FK
    }
```

## 2. Core Architectural Decisions

- **Soft Delete Strategy:** All major entities include `deleted_at`, `deleted_by`, `created_by`, `updated_by`. Records are never physically deleted. `status` is also set to `ARCHIVED`.
- **Supabase Storage:** Implemented via a `StorageService` interface. Images are uploaded to a public bucket, and only the public URL is stored in the DB.
- **Inventory Future-proofing:** Inventory tracking logic will be strictly separated. Stock levels are currently maintained on `ProductVariant`, but the service layer will be designed so a future `InventoryTransaction` module can be injected easily.
- **Images:** Polymorphic approach. Images belong to a Product, but can optionally belong to a specific Variant, allowing unlimited images and variant-specific overrides.

## 3. Implementation Order

1. Flyway Migration (Database)
2. JPA Entities (With Auditing setup)
3. Repository Layer
4. DTOs
5. MapStruct Mappers
6. Services (`StorageService` interface, `ProductService`, etc.)
7. Controllers (Secured via Roles)
8. Validation (Jakarta Validation)
9. Security Integration
10. Swagger Documentation
11. React Admin Pages (Dashboard, Product Builder)
12. React Customer Pages (Storefront, Product Display)
13. API Integration
14. Testing

## 4. Completion Deliverable
Phase 2 will NOT transition automatically to Phase 3. A comprehensive review artifact will be provided covering ER diagrams, endpoints, postman collections, frontend screenshots, and tests.
