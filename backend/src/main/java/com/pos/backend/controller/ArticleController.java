package com.pos.backend.controller;

import com.pos.backend.dto.ArticleRequest;
import com.pos.backend.entity.Article;
import com.pos.backend.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // Endpoint público para obtener todos los artículos
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = articleService.findAll();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<Article>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<Article> articles = articleService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}/active")
    public ResponseEntity<List<Article>> getActiveArticles(@PathVariable Integer organizationId) {
        try {
            List<Article> articles = articleService.findActiveByOrganization(organizationId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<Article> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<Article> article = articleService.findById(id, organizationId);
            return article.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/organization/{organizationId}")
    public ResponseEntity<List<Article>> searchByNameOrCode(@RequestParam String q, @PathVariable Integer organizationId) {
        try {
            List<Article> articles = articleService.searchByNameOrCode(q, organizationId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}/organization/{organizationId}")
    public ResponseEntity<Article> getByCode(@PathVariable String code, @PathVariable Integer organizationId) {
        try {
            Optional<Article> article = articleService.findByCode(code, organizationId);
            return article.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/price-range/organization/{organizationId}")
    public ResponseEntity<List<Article>> getByPriceRange(@RequestParam BigDecimal minPrice,
                                                        @RequestParam BigDecimal maxPrice,
                                                        @PathVariable Integer organizationId) {
        try {
            List<Article> articles = articleService.findByPriceRange(minPrice, maxPrice, organizationId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/low-stock/organization/{organizationId}")
    public ResponseEntity<List<Article>> getLowStockArticles(@RequestParam BigDecimal minimumStock,
                                                           @PathVariable Integer organizationId) {
        try {
            List<Article> articles = articleService.findLowStockArticles(minimumStock, organizationId);
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ArticleRequest request) {
        try {
            Article article = new Article();
            // Mapear campos del nuevo esquema INV_Article
            article.setSku(request.getSku());
            article.setName(request.getName());
            article.setDescription(request.getDescription());
            article.setImageUrl(request.getImageUrl());
            article.setRetailPrice(request.getRetailPrice());
            article.setIsVatExempt(request.getIsVatExempt());
            article.setOrganizationId(request.getOrganizationId());

            Article created = articleService.create(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating article: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody ArticleRequest request) {
        try {
            Article articleDetails = new Article();
            // Mapear campos del nuevo esquema INV_Article para update
            articleDetails.setSku(request.getSku());
            articleDetails.setName(request.getName());
            articleDetails.setDescription(request.getDescription());
            articleDetails.setImageUrl(request.getImageUrl());
            articleDetails.setRetailPrice(request.getRetailPrice());
            articleDetails.setIsVatExempt(request.getIsVatExempt());

            Article updated = articleService.update(id, articleDetails, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating article: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating article: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock/organization/{organizationId}")
    public ResponseEntity<?> updateStock(@PathVariable Integer id,
                                        @PathVariable Integer organizationId,
                                        @RequestParam BigDecimal newStock) {
        try {
            // Nota: INV_Article no maneja inventarios
            Optional<Article> article = articleService.findById(id, organizationId);
            if (article.isPresent()) {
                return ResponseEntity.ok(article.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock/add/organization/{organizationId}")
    public ResponseEntity<?> addStock(@PathVariable Integer id,
                                     @PathVariable Integer organizationId,
                                     @RequestParam BigDecimal quantityToAdd) {
        try {
            // Nota: INV_Article no maneja inventarios
            Optional<Article> article = articleService.findById(id, organizationId);
            if (article.isPresent()) {
                return ResponseEntity.ok(article.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/stock/subtract/organization/{organizationId}")
    public ResponseEntity<?> subtractStock(@PathVariable Integer id,
                                          @PathVariable Integer organizationId,
                                          @RequestParam BigDecimal quantityToSubtract) {
        try {
            // Nota: INV_Article no maneja inventarios
            Optional<Article> article = articleService.findById(id, organizationId);
            if (article.isPresent()) {
                return ResponseEntity.ok(article.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/stock/check/organization/{organizationId}")
    public ResponseEntity<?> checkStockAvailability(@PathVariable Integer id,
                                                   @PathVariable Integer organizationId,
                                                   @RequestParam BigDecimal requiredQuantity) {
        try {
            boolean available = articleService.checkStockAvailability(id, requiredQuantity, organizationId);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking stock: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            articleService.delete(id, organizationId);
            return ResponseEntity.ok("Article deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deactivating article: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deactivating article: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}/hard")
    public ResponseEntity<?> hardDelete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            articleService.hardDelete(id, organizationId);
            return ResponseEntity.ok("Article permanently deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting article: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting article: " + e.getMessage());
        }
    }
}