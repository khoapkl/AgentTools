IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'adminUsers' AND COLUMN_NAME = 'email')
BEGIN
   ALTER TABLE adminUsers ADD [email] [nvarchar](50) NULL
END
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'adminUsers' AND COLUMN_NAME = 'fullname')
BEGIN
   ALTER TABLE adminUsers ADD [fullname] [nvarchar](50) NULL
END
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'adminUsers' AND COLUMN_NAME = 'report')
BEGIN
   ALTER TABLE adminUsers ADD [report] [bit] CONSTRAINT [DF_adminUsers_report]  DEFAULT ((0))
END
GO
UPDATE agent_report SET channel='Customers' WHERE ORDERNUMBER IN (12527867,12614604,12614604,12614604,12614604,12614604,12614604,12614604,12614604,12614604)
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'maxdiscount' AND COLUMN_NAME = 'holddays')
BEGIN
   ALTER TABLE maxdiscount ADD holddays INT NOT NULL  DEFAULT ((0));
END
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ordersHeld' AND COLUMN_NAME = 'exp_date')
BEGIN
   ALTER TABLE ordersHeld ADD exp_date datetime NOT NULL DEFAULT (getdate())
END
GO
UPDATE inventoryAttribute SET reorderList = 0 WHERE  category_id = 11947 AND attribute_id = 4 
GO
UPDATE inventoryattribute SET attribute_name = 'Operating System' WHERE attribute_name ='OPeraing System'
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'estore_basket_item' AND COLUMN_NAME = 'byAgent')
BEGIN
   ALTER TABLE estore_basket_item ADD byAgent BIT NOT NULL DEFAULT (0)
END
GO
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'orderHeader' AND COLUMN_NAME = 'byAgent')
BEGIN
   ALTER TABLE orderHeader ADD byAgent BIT NOT NULL DEFAULT (0)
END
