CREATE TABLE IF NOT EXISTS fxa_file_receipt(
  id int NOT NULL auto_increment PRIMARY KEY,
  filename NVARCHAR(100) NOT NULL,
  exchange NVARCHAR(50) NOT NULL
);

