INSERT INTO address(id) VALUES('88bb07ce021d4791b6d2b39c361d2ee7','709f2448ca574895b086f6520d015bdb','c8f79b1c8a9e4e9fbb22c41b918491c9');

INSERT INTO store_setting(id, name, email, currency_code, address_id, created_date, created_by, last_modified_date, last_modified_by, iana_timezone, shoperal_domain, store_version) VALUES
('8a12df0c1c404967bc37b91a18e375d0', 'jumia', 'jumia@example.com', 'GHS', '88bb07ce021d4791b6d2b39c361d2ee7', '2020-11-18 21:48:00+0:00', 'juliuskrah', '2020-11-18 21:49:00+0:00', 'juliuskrah', 'Africa/Accra', 'jumia.shoperal.app', '0.0.0'),
('86aac60237d247e2b34a16e77d48e9c1', 'kylie', 'kylie@example.com', 'ZMW', '709f2448ca574895b086f6520d015bdb', '2020-11-18 21:48:00+0:00', 'juliuskrah', '2020-11-18 21:49:00+0:00', 'juliuskrah', 'Africa/Lusaka', 'kylie.shoperal.app', '0.0.0'),
('eeca9d1b759f4ac1aa5e318acd5a5a47', 'fenty', 'fenty@example.com', 'KES', 'c8f79b1c8a9e4e9fbb22c41b918491c9', '2020-11-18 21:48:00+0:00', 'juliuskrah', '2020-11-18 21:49:00+0:00', 'juliuskrah', 'Africa/Nairobi', 'fenty.shoperal.app', '0.0.0');

INSERT INTO presentment_currency(store_id, currency) VALUES('8a12df0c1c404967bc37b91a18e375d0', 'GHS'), ('eeca9d1b759f4ac1aa5e318acd5a5a47', 'KES'), ('eeca9d1b759f4ac1aa5e318acd5a5a47', 'USD'), ('8a12df0c1c404967bc37b91a18e375d0', 'GBP'), ('86aac60237d247e2b34a16e77d48e9c1', 'ZMW')
