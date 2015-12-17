CREATE TABLE IF NOT EXISTS ref_sequence(
  sequence_name NVARCHAR(50) NOT NULL PRIMARY KEY,
  sequence_number INT NOT NULL,
  reset_point DATETIME DEFAULT(CURRENT_DATE()),
  reset_sequence_number INT NOT NULL
);

INSERT INTO ref_sequence (sequence_name, sequence_number, reset_sequence_number) VALUES ('ValueInstructionFile' , 8000, 8000);
INSERT INTO ref_sequence (sequence_name, sequence_number, reset_sequence_number) VALUES ('TierOneBanksImageExchange', 1, 1);
INSERT INTO ref_sequence (sequence_name, sequence_number, reset_sequence_number) VALUES ('AgencyBanksImageExchange', 1, 1);
