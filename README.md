I've created a file under the db folder that contains the DDL query to create the database. Please run this query in MySQL before executing the program.

http://localhost:8080/swagger-ui
GET /api/products/pdf
GET /api/products/{produtId}/qrcode

### **Sales (Base URL: `http://localhost:8080/api/sales`)**
# Get all sales
GET /api/sales

# Get sale by ID
GET /api/sales/1
GET /api/products?page=0&size=10

# Get product by ID
GET /api/products/1
DELETE /api/products/1

# Restore product (ADMIN only)
PATCH /api/products/restore/1

# Get total revenue
GET /api/products/revenue/total

# Get revenue by product
GET /api/products/revenue/1
