-- Create categories
INSERT INTO category values (1, localtimestamp(), localtimestamp(), "Entertainment");
INSERT INTO category values (2, localtimestamp(), localtimestamp(), "Food and drink");
INSERT INTO category values (3, localtimestamp(), localtimestamp(), "Home");
INSERT INTO category values (4, localtimestamp(), localtimestamp(), "Life");
INSERT INTO category values (5, localtimestamp(), localtimestamp(), "Transportation");
INSERT INTO category values (6, localtimestamp(), localtimestamp(), "Utilities");

-- Create sub categories

-- Entertainment
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (1, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625495/games_j9hr4g.png", "Games", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (2, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625496/movie_wimiaq.png", "Movies", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (3, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723022492/music-note_yrqerf.png", "Music", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (4, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625495/sports_dolgva.png", "Sports", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (5, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625494/other_yzrzvk.png", "Other", 1);

-- Food and Drink
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (6, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625532/grocery_uhzbrb.png", "Groceries", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (7, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625533/dining_qraxwr.png", "Dining Out", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (8, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625531/other_eksjrq.png", "Other", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (9, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625530/Liquor_qtnjao.png", "Liquor", 2);

-- Home
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (10, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625561/Rent_xxdfmj.png", "Rent", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (11, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625561/Rent_xxdfmj.png", "Mortgage", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (12, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625554/supplies_mnmwqg.svg", "Household supplies", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (13, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625556/Furniture_ofne8d.png", "Furniture", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (14, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625558/maintainance_zvx89d.png", "Maintenance", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (15, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625557/other_ed1rjm.png", "Other", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (16, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625559/pets_tdidus.png", "Pets", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (17, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625553/services_r8yrhb.png", "Services", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (18, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625555/electronics_jwmiel.png", "Electronics", 3);

-- Life
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (19, localtimestamp(), localtimestamp(), "", "Insurance", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (20, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625588/clothing_tpr4q5.png", "Clothing", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (21, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625592/gifts_ica2eo.png", "Gifts", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (22, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625586/medical_kmte7m.png", "Medical expenses", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (23, localtimestamp(), localtimestamp(), "", "Other", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (24, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625587/tax_trfeoh.png", "Taxes", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (25, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625591/education_kzgb7e.png", "Education", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (26, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625590/child_care_dobgio.png", "Childcare", 4);

-- Transportation
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (27, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625617/parking_sz2vcr.png", "Parking", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (28, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625623/car_heston.png", "Car", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (29, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625621/bus-train_gbxrgy.png", "Bus/train", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (30, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625624/fuel_cs6yje.png", "Gas/fuel", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (31, localtimestamp(), localtimestamp(), "", "Other", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (32, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625619/plane_syklkd.png", "Plane", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (33, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625620/taxi_l6ewnx.png", "Taxi", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (34, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625625/hotel_id8hnl.png", "Hotel", 5);

-- Utilities
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (35, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625652/electricity_smwi2v.png", "Electricity", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (36, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625654/gas_a1zetb.png", "Heat/gas", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (37, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625656/water_hdon0t.png", "Water", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (38, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1722625651/wifi_yv4b4m.png", "TV/phone/internet", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (39, localtimestamp(), localtimestamp(), "", "Other", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (40, localtimestamp(), localtimestamp(), "", "Cleaning", 6);