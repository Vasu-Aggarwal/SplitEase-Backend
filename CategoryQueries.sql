-- Create categories
INSERT INTO category values (1, localtimestamp(), localtimestamp(), "Entertainment");
INSERT INTO category values (2, localtimestamp(), localtimestamp(), "Food and drink");
INSERT INTO category values (3, localtimestamp(), localtimestamp(), "Home");
INSERT INTO category values (4, localtimestamp(), localtimestamp(), "Life");
INSERT INTO category values (5, localtimestamp(), localtimestamp(), "Transportation");
INSERT INTO category values (6, localtimestamp(), localtimestamp(), "Utilities");

-- Create sub categories

-- Entertainment
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (1, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101989/Game_rmrcfg.png", "Games", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (2, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723102012/Movie_axib2g.png", "Movies", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (3, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101921/Music_sjvsfu.png", "Music", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (4, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101875/Sports_mdbdor.png", "Sports", 1);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (5, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723102104/Other_lzbxe0.png", "Other", 1);

-- Food and Drink
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (6, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101620/Grocery_vfsi68.png", "Groceries", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (7, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101554/Dining_z5slws.png", "Dining Out", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (8, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101663/Other_fwqwi1.png", "Other", 2);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (9, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101625/Liquor_x4zjq6.png", "Liquor", 2);

-- Home
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (10, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100977/Rent_oycx2o.png", "Rent", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (11, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100977/Rent_oycx2o.png", "Mortgage", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (12, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101247/Supplies_kmq4yp.png", "Household supplies", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (13, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101146/Furniture_zeyd3o.png", "Furniture", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (14, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101060/Maintainance_bsvqmo.png", "Maintenance", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (15, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101114/Other_gagryh.png", "Other", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (16, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101016/Pet_nmmcqr.png", "Pets", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (17, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101289/Services_q1vbzs.png", "Services", 3);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (18, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723101207/Electronics_x5did9.png", "Electronics", 3);

-- Life
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (19, localtimestamp(), localtimestamp(), "", "Insurance", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (20, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100694/Clothing_thiotl.png", "Clothing", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (21, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100552/Gift_pagunh.png", "Gifts", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (22, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100891/Medical_i3cv19.png", "Medical expenses", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (23, localtimestamp(), localtimestamp(), "", "Other", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (24, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100836/Tax_faoulr.png", "Taxes", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (25, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100598/Education_idxpik.png", "Education", 4);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (26, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100643/ChildCare_jeddak.png", "Childcare", 4);

-- Transportation
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (27, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100469/Parking_ari2hm.png", "Parking", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (28, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100254/Car_pljjxh.png", "Car", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (29, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100361/Bus_gueiip.png", "Bus/train", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (30, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100216/Gas_s73gqr.png", "Gas/fuel", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (31, localtimestamp(), localtimestamp(), "", "Other", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (32, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100411/Plane_ah3yiq.png", "Plane", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (33, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100313/Taxi_all299.png", "Taxi", 5);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (34, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100169/Hotel_new_kmno8f.png", "Hotel", 5);

-- Utilities
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (35, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723099949/Electricity_kmnbct.png", "Electricity", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (36, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723099860/Gas_tn2xn3.png", "Heat/gas", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (37, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723099540/Water_x4pffh.png", "Water", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (38, localtimestamp(), localtimestamp(), "https://res.cloudinary.com/dyhym5vjz/image/upload/v1723100009/Wifi_ro0vwq.png", "TV/phone/internet", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (39, localtimestamp(), localtimestamp(), "", "Other", 6);
INSERT INTO sub_category (sub_category_id, created_on, modified_on, image_url, name, category_id) values (40, localtimestamp(), localtimestamp(), "", "Cleaning", 6);