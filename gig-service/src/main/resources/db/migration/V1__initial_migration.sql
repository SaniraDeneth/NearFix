CREATE TABLE gigs (
                      id UUID PRIMARY KEY,
                      title TEXT NOT NULL,
                      description TEXT,
                      price DECIMAL NOT NULL,
                      provider_id UUID NOT NULL,
                      category_id UUID,
                      location GEOGRAPHY(POINT, 4326),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name TEXT NOT NULL,
                            parent_id UUID REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE gig_images (
                            id UUID PRIMARY KEY,
                            gig_id UUID NOT NULL,
                            image_url TEXT,
                            FOREIGN KEY (gig_id) REFERENCES gigs(id) ON DELETE CASCADE
);

CREATE TABLE gig_availability (
                                  id UUID PRIMARY KEY,
                                  gig_id UUID NOT NULL,
                                  available_day TEXT,
                                  start_time TIME,
                                  end_time TIME,
                                  FOREIGN KEY (gig_id) REFERENCES gigs(id) ON DELETE CASCADE
);

