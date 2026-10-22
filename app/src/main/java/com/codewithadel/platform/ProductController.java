package com.codewithadel.platform;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("application", "product-api", "version", version);
    }

    @GetMapping("/products")
    public List<Product> products() {
        return List.of(
            new Product(1, "Ceramic Vase", "Home Decor", new BigDecimal("29.99")),
            new Product(2, "Linen Cushion", "Home Decor", new BigDecimal("24.50")),
            new Product(3, "Table Lamp", "Lighting", new BigDecimal("79.00"))
        );
    }
}
