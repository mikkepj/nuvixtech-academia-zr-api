DELETE FROM courses;

INSERT INTO courses (code, name, description, duration, type, price) VALUES
('JAVA-101', 'Java Fundamentals', 'Introducción al lenguaje Java', 40, 'PRESENCIAL', 299.99),
('JAVA-201', 'Advanced Java', 'Conceptos avanzados de Java', 60, 'PRESENCIAL', 449.99),
('PYTH-101', 'Python for Beginners', 'Aprende Python desde cero', 30, 'ONLINE', 199.99),
('PYTH-201', 'Data Science con Python', 'Análisis de datos y ML con Python', 80, 'ONLINE', 599.99),
('WEB-101', 'HTML y CSS Fundamentals', 'Desarrollo web frontend básico', 20, 'ONLINE', 149.99),
('WEB-201', 'React JS Development', 'Aplicaciones web reactivas con React', 50, 'PRESENCIAL', 399.99),
('CLOUD-101', 'AWS Cloud Fundamentals', 'Introducción a la nube con AWS', 35, 'ONLINE', 349.99),
('DB-101', 'SQL y Diseño de BD', 'Diseño de bases de datos relacionales', 25, 'PRESENCIAL', 249.99),
('DEVOPS-101', 'DevOps con Docker', 'Contenedores y pipelines CI/CD', 45, 'ONLINE', 379.99),
('SPRING-101', 'Spring Boot Development', 'Desarrollo de APIs REST con Spring Boot', 55, 'PRESENCIAL', 499.99);
