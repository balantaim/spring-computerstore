DROP TABLE IF EXISTS reviews;

CREATE TABLE reviews (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  vote FLOAT(2, 1) NOT NULL,
  user_id BIGINT NOT NULL,
  product_id INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE reviews ADD CONSTRAINT fk_user_review FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE reviews ADD CONSTRAINT fk_product_review FOREIGN KEY (product_id) REFERENCES products(id);