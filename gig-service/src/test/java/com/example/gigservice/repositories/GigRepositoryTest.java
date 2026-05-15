package com.example.gigservice.repositories;

import com.example.gigservice.AbstractPostgresTest;
import com.example.gigservice.entities.Category;
import com.example.gigservice.entities.Gig;
import com.example.gigservice.entities.ServicePricing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GigRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private GigRepository gigRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private Category electrical;

    @BeforeEach
    void setUp() {
        gigRepository.deleteAll();
        categoryRepository.deleteAll();

        Category plumbing = new Category();
        plumbing.setName("Plumbing");
        plumbing = categoryRepository.save(plumbing);

        electrical = new Category();
        electrical.setName("Electrical");
        electrical = categoryRepository.save(electrical);

        Gig gig1 = Gig.builder()
                .title("Rajagiriya Plumber")
                .providerId(UUID.randomUUID())
                .category(plumbing)
                .location(createPoint(6.9067, 79.9194))
                .build();
        var pricing1 = ServicePricing.builder()
                .gig(gig1)
                .basePrice(new BigDecimal("100.0"))
                .build();
        gig1.setPricing(pricing1);

        Gig gig2 = Gig.builder()
                .title("Kandy Electrician")
                .providerId(UUID.randomUUID())
                .category(electrical)
                .location(createPoint(7.2906, 80.6337))
                .build();

        var pricing2 = ServicePricing.builder()
                .gig(gig2)
                .basePrice(new BigDecimal("200.0"))
                .build();
        gig2.setPricing(pricing2);

        gigRepository.saveAll(List.of(gig1, gig2));
    }

    @Test
    @DisplayName("Should find gigs within 10km radius")
    void searchNearby_Within10km() {
        double lat = 6.9271;
        double lng = 79.8612;
        double radius = 10000;

        List<Gig> results = gigRepository.searchNearby(lat, lng, radius, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Rajagiriya Plumber");
    }

    @Test
    @DisplayName("Should find all gigs within 150km radius")
    void searchNearby_Within150km() {
        double lat = 6.9271;
        double lng = 79.8612;
        double radius = 150000;

        List<Gig> results = gigRepository.searchNearby(lat, lng, radius, null, null, null);

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Should filter by category")
    void searchNearby_WithCategoryFilter() {
        double lat = 6.9271;
        double lng = 79.8612;
        double radius = 150000;

        List<Gig> results = gigRepository.searchNearby(lat, lng, radius, electrical.getId(), null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Kandy Electrician");
    }

    @Test
    @DisplayName("Should filter by price range")
    void searchNearby_WithPriceFilter() {
        double lat = 6.9271;
        double lng = 79.8612;
        double radius = 150000;

        List<Gig> results = gigRepository.searchNearby(lat, lng, radius, null, 150.0, 250.0);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Kandy Electrician");
    }

    private Point createPoint(double lat, double lng) {
        return geometryFactory.createPoint(new Coordinate(lng, lat));
    }
}
