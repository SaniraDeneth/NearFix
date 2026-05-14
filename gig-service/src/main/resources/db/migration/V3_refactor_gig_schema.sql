CREATE TABLE gig_service_modes (
                                   id UUID PRIMARY KEY,
                                   gig_id UUID NOT NULL,
                                   mode VARCHAR(50) NOT NULL,

                                   FOREIGN KEY (gig_id) REFERENCES gigs(id) ON DELETE CASCADE
);

CREATE TABLE service_pricing (
                                 id UUID PRIMARY KEY,
                                 gig_id UUID NOT NULL UNIQUE,
                                 base_price DECIMAL(19, 2) NOT NULL,
                                 travel_fee_per_km DECIMAL(19, 2) DEFAULT 0,
                                 price_type VARCHAR(50),
                                 max_visit_radius_km INTEGER,

                                 FOREIGN KEY (gig_id) REFERENCES gigs(id) ON DELETE CASCADE
);

ALTER TABLE gigs DROP COLUMN price;
