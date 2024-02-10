INSERT INTO DIRIGIBLE_TENANTS(TENANT_SLUG, TENANT_NAME)
    VALUES('tenant1', 'The first tenant');
INSERT INTO DIRIGIBLE_TENANTS(TENANT_SLUG, TENANT_NAME)
    VALUES('tenant2', 'The second tenant');

/* admin / admin / ADMINISTRATOR */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
    VALUES(NULL,
           'admin',
           '$2a$10$OzDFCJGmEak7JstwpC6pw.BHbJvEgTKeVIOw75zenPsTuOnZ86QbC',
           0); /* 0 is ADMINISTRATOR */

/* tenant1 / user1@dirigible.io / user1 / USER */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
    VALUES((SELECT ten.TENANT_ID FROM DIRIGIBLE_TENANTS AS ten WHERE ten.TENANT_SLUG = 'tenant1'),
           'user1@dirigible.io',
           '$2a$10$Oz6X5bxK10HXLVeTVgirDOnuYJPrqnDEuWyINX9jonEqxHI9q6Z7W',
           2); /* 2 is USER */

/* tenant2 / user1@dirigible.io / user1 / TENANT_ADMINISTRATOR */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
   VALUES((SELECT ten.TENANT_ID FROM DIRIGIBLE_TENANTS AS ten WHERE ten.TENANT_SLUG = 'tenant2'),
           'user1@dirigible.io',
           '$2a$10$Oz6X5bxK10HXLVeTVgirDOnuYJPrqnDEuWyINX9jonEqxHI9q6Z7W',
           1); /* 1 is TENANT_ADMINISTRATOR */

/* tenant1 / user2@dirigible.io / user2 / USER */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
    VALUES((SELECT ten.TENANT_ID FROM DIRIGIBLE_TENANTS AS ten WHERE ten.TENANT_SLUG = 'tenant1'),
           'user2@dirigible.io',
           '$2a$10$lrkFQ4rwNF3F30kneQ/td.u37Vh.XOR/MWQQS8ES90DjU39HGe5qe',
           2); /* 2 is USER */

/* tenant2 / user2@dirigible.io / user2 / USER */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
    VALUES((SELECT ten.TENANT_ID FROM DIRIGIBLE_TENANTS AS ten WHERE ten.TENANT_SLUG = 'tenant2'),
           'user2@dirigible.io',
           '$2a$10$lrkFQ4rwNF3F30kneQ/td.u37Vh.XOR/MWQQS8ES90DjU39HGe5qe',
           2); /* 2 is USER */

/* tenant1 / dev1@dirigible.io / dev1 / USER */
INSERT INTO DIRIGIBLE_USERS(USER_TENANT_ID, USER_EMAIL, USER_PASSWORD, USER_ROLE)
    VALUES((SELECT ten.TENANT_ID FROM DIRIGIBLE_TENANTS AS ten WHERE ten.TENANT_SLUG = 'tenant1'),
           'dev1@dirigible.io',
           '$2a$10$GMvJa8CAIfkD6oKBcGA4gO/SR7o5M7WUm5BwEZgP7hNP1ZgEcgZOK',
           3); /* 3 is DEVELOPER */
