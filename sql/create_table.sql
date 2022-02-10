CREATE TABLE allUser (
    userID int IDENTITY(1,1) PRIMARY KEY,
    userName VARCHAR(256),
    email VARCHAR(256),
    image varbinary(MAX)
);

