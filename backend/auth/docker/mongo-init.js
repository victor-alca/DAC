db = db.getSiblingDB("auth");

db.createCollection("users");

db.users.insertMany([
  {
    email: "admin@example.com",
    password: "123456",
    role: "admin",
  },
  {
    email: "user@example.com",
    password: "123456",
    role: "user",
  },
]);