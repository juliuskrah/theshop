package com.shoperal.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import com.shoperal.core.model.HtmlElement;
import com.shoperal.core.model.Product;
import com.shoperal.core.model.SEO;
import com.shoperal.core.model.StructuredLDData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SEORepositoryTest extends AbstractPostgreSQLTestContainer {
    private final TestEntityManager em;
    private final SEORepository repository;
    private Product product;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setName("Product with SEO");
        product.setFeaturedMedia(URI.create("/files/product-with-seo.mov"));
        product.setFriendlyUriFragment(URI.create("product-with-seo"));
        em.persist(product);
    }

    @AfterEach
    void cleanUp() {
        em.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();
    }

    @Test
    void testFindSEOByProduct() {
        // Set up <meta name="keywords" content="Keyword1, Keyword2, Keyword3">
        var keywords = new SEO();
        keywords.setName("keywords");
        keywords.setContent("Keyword1, Keyword2, Keyword3");
        keywords.setProduct(product);
        em.persist(keywords);
        
        /*
         * Set up 
         * <script type="application/ld+json">
         * {
         *   "@context": "https://schema.org/",
         *   "@type": "Product",
         *   "name": "Product with SEO",
         *   ...
         * }
         * </script>
         */
        var structuredData = new StructuredLDData();
        structuredData.setId(product.getId().toString());
        structuredData.setName("Product with SEO");
        structuredData.setContext("https://schema.org");
        var jsonLD = new SEO();
        jsonLD.setHtmlElementType(HtmlElement.SCRIPT);
        jsonLD.setType("application/ld+json");
        jsonLD.setBody(structuredData);
        jsonLD.setProduct(product);
        em.persist(jsonLD);

        // Set up <link href="https://shoperal.com" rel="canonical">
        var canonical = new SEO();
        canonical.setHtmlElementType(HtmlElement.LINK);
        canonical.setRel("canonical");
        canonical.setHref(URI.create("https://shoperal.com"));
        canonical.setProduct(product);
        em.persist(canonical);

        var seo = this.repository.findByProduct(product);
        assertThat(seo).isNotEmpty();
        assertThat(seo).hasSize(3);
        assertThat(seo).filteredOn(predicate -> //
            predicate.getHtmlElementType().equals(HtmlElement.SCRIPT)) //
            .allMatch(predicate -> predicate.getBody().equals(structuredData));
    }
}
