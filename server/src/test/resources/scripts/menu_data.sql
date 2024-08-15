INSERT INTO menu(id, title, handle) VALUES
('6d035baaeda2494e828cf0d989e94ffd', 'Footer menu', 'footer');

INSERT INTO menu_item(id, title, uri, menu_id, position) VALUES
('a0a9e5e16748408d9a5847dd5eddf77c', 'Products', '/products', '52ab7e1aeda941f6824cbdf471c24efa', 1),
('0ccc273391dd4deca5d20e6d5ee1b125', 'About Us', '/pages/about', '6d035baaeda2494e828cf0d989e94ffd', 2),
('eeba1c8c58d24e408aea6a56779f624d', 'Contact Us', '/pages/contact', '6d035baaeda2494e828cf0d989e94ffd', 3)