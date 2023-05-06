CREATE SCHEMA IF NOT EXISTS KITCHEN;
USE KITCHEN;
ALTER DATABASE Kitchen
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_520_ci;
CREATE TABLE IF NOT EXISTS ItemType (
    Item_ID int primary key,
    Item_name varchar(50),
    Item_mass float,
    Item_cost float,
    Item_purchase_date date
);
CREATE TABLE IF NOT EXISTS FoodType (
	Food_ID int primary key,
    Food_name varchar(50),
    Food_cal int,
    Food_fat int,
    Food_protein int,
    Food_carbs int
);
CREATE TABLE IF NOT EXISTS FoodQuantity (

	Food_ID int,
    Quantity_ID int primary key,
    FBestBy_Date date,
    foreign key (Quantity_ID) references ItemType(Item_ID),
    foreign key (Food_ID) references FoodType(Food_ID)
);
CREATE TABLE IF NOT EXISTS Recipe (
	Recipe_ID int primary key,
    Recipe_name varchar(50),
	Prep_time int,
	Cook_time int
);
CREATE TABLE IF NOT EXISTS RecipeAuthor (
	    Author varchar(50),
		Recipe_ID int,
        foreign key (Recipe_ID) references Recipe(Recipe_ID)
	);
CREATE TABLE IF NOT EXISTS Tool (
	Tool_ID int primary key,
    foreign key (Tool_ID) references ItemType(Item_ID)
);
CREATE TABLE IF NOT EXISTS CleaningItem (
	Cleaning_ID int,
    foreign key (Cleaning_ID) references ItemType(Item_ID)
);
CREATE TABLE IF NOT EXISTS ShoppingList (
	ListDate date,
    List_ID int primary key
);
CREATE TABLE IF NOT EXISTS ListSoughtItems (
    Item_ID int primary key,
	List_ID int,
    foreign key (List_ID) references ShoppingList(List_ID),
    foreign key (Item_ID) references ItemType(Item_ID)
);
CREATE TABLE IF NOT EXISTS ListLocation (
	Store_name varchar(50) primary key,
    Store_location varchar(100),
    List_date date,
    List_ID int,
    foreign key (List_ID) references ShoppingList(List_ID)
);


CREATE TABLE IF NOT EXISTS Used_tools ( 
	Tool_ID int,
    Recipe_ID int,
    foreign key (Tool_ID) references Tool(Tool_ID),
	foreign key (Recipe_ID) references Recipe(Recipe_ID)
);
CREATE TABLE IF NOT EXISTS Used_Ingredients (
	Food_ID int,
    Recipe_ID int,
    foreign key (Food_ID) references FoodType(Food_ID),
	foreign key (Recipe_ID) references Recipe(Recipe_ID)
);

delimiter //
create procedure searchForRecipeByName (IN recipe_name varchar(50), OUT ID int)
begin
	select R.Recipe_ID into ID
    from Recipe as R
    where recipe_name = R.Recipe_name;
end//
delimiter ;
delimiter //

create procedure searchForShoppingListByDate(IN Ldate date, OUT ID int)
begin
	select L.List_ID into ID	
    from ShoppingList as L
    where Ldate = L.ListDate;
end//
delimiter ;
delimiter //
create procedure maxListID (OUT MAX int)
begin
	select MAX(L.List_ID) into MAX
    from ShoppingList as L;
    
end//
delimiter ;
delimiter //
create procedure maxQuantityID (OUT MAX int)
begin
	select MAX(Q.Quantity_ID) into MAX
    from FoodQuantity as Q;
    
end//
delimiter ;
delimiter //
create procedure maxFoodTypeID (OUT MAX int)
begin
	select MAX(Q.Food_ID) into MAX
    from F as Q;
    
end//
delimiter ;
delimiter //
create procedure createShoppingList(IN ID int, LDate date)
begin
	insert into ShoppingList values(ID, LDate);
end//
delimiter //
create procedure retrieveIngredientsFromRecipe(IN RecipeID int)
begin
	select I.Food_ID 
    from Used_Ingredients as I
    where I.Recipe_ID = RecipeID;
end//
delimiter ;
delimiter //
create procedure createListSoughtItem(IN ListID int, ItemID int)
begin
	insert into ListSoughtItems values(ItemID, ListID);
end//
delimiter ;
delimiter //
create procedure createFoodType(IN Food_ID int, Food_name varchar(50), cal int, fat int, protein int, carb int)
begin
	insert into FoodType values(Food_ID, Food_name, cal, fat, protein, carbs);
end//
delimiter ;
delimiter //
create procedure returnAllSoughtItems(IN ListID int) 
begin
	select * from ListSoughtItems where ListID = ListSoughtItems.List_ID;
end//
delimiter ;
delimiter //
create procedure createItemType(IN ID int, Iname varchar(50), mass float, cost float, IDate date) 
begin
	insert into ItemType values(ID, Iname, mass, cost, IDate);
end//
delimiter ;
delimiter //
create procedure displayShoppingList(IN ListID int) 
begin
	select SL.ListDate
	from ShoppingList as SL
	where ListID = SL.List_ID;
    
    select I.Item_name
    from ItemType as I
    where I.Item_ID in (
		select LI.Item_ID
        from ListSoughtItems as LI
        where LI.List_ID = ListID);
        
	select L.Store_name, L.Store_location
    from ListLocation as L
    where L.List_ID = ListID;
	
	
    
end//
delimiter ;
delimiter //
create procedure searchForItemByName(IN ItemName varchar(50), OUT ID int)
begin
	select I.Item_ID into ID
    from ItemType as I
    where I.Item_name = ItemName;
end//
delimiter ;

delimiter //
create procedure searchForFoodItem(IN ID int, OUT FoodName varchar(50))
begin
	select F.Food_name into F
    from FoodType as F
    where ID = F.Food_ID;
end//
delimiter ;
call searchForFoodItem(4, @ID);
select @ID;
delimiter //
create procedure createFoodQuantity(IN FoodID int, QuantityID int, purchaseDate date)
begin
	insert into FoodQuantity values(FoodID, QuantityID, purchaseDate);
end//
delimiter ;
delimiter //
create procedure deleteSoughtItem(IN ItemID int)
begin
	delete from ListSoughtItems where ItemID = ListSoughtItems.Item_ID;
end//
delimiter ;
delimiter //
create procedure retrieveSpoiledFoods(IN CurrentDate date)
begin
	select F.Quantity_ID, F.Food_ID, I.Item_name,  F.FBestBy_Date
    from FoodQuantity as F, ItemType as I
    where DATE(CurrentDate) > F.FBestBy_Date AND F.Quantity_ID = I.Item_ID;
end//
delimiter ;

select * from ShoppingList;
call displayShoppingList(1);
call retrieveIngredientsFromRecipe(0);
call searchForFoodItem(0, @FoodName);
select @FoodName;

call maxListID(@MAX);
select @MAX;


call searchForItemByName("Pepper", @ID);
call displayShoppingList(2);
select @ID;
insert into ItemType values(0, "Shell Pasta", 123.5, 3.73, "2023-4-20");
insert into ItemType values(1, "Sun-Dried Tomatoes", 200, 4.00, "2023-4-20");
insert into ItemType values(2, "Chicken Breast", 180, 6.23, "2023-4-20");
insert into ItemType values(3, "Pesto", 90, 4.23, "2023-4-20");
insert into ItemType values(4, "Popcorn", 120, 6.88, "2023-4-22");
insert into ItemType values(5, "Salt", 2, 1.22, "2023-5-2");
insert into ItemType values(7, "Bad Cheese", 3, 1.22, "2023-2-28");

insert into FoodType values(0, "Shell Pasta",  100, 50, 25, 10);
insert into FoodType values(1, "Sun-Dried Tomatoes", 10, 25, 50, 100);
insert into FoodType values(2, "Chicken Breast", 1, 3, 5, 7);
insert into FoodType values(3, "Pesto", 2, 4, 6, 8);
insert into FoodType values(7, "Bad Cheese", 1, 3, 5, 7);

insert into Used_Ingredients values(0, 0);
insert into Used_Ingredients values(1, 0);
insert into Used_Ingredients values(2, 0);
insert into Used_Ingredients values(3, 0);

insert into Used_Ingredients values(4, 1);
insert into Used_Ingredients values(5, 1);

insert into FoodQuantity values(0, 0, "2025-1-25");
insert into FoodQuantity values(1, 1, "2025-1-25");
insert into FoodQuantity values(2, 2, "2023-4-25");
insert into FoodQuantity values(3, 3, "2025-4-20");
insert into FoodQuantity values(7, 7, "2023-3-1");

insert into Recipe values(0, "Chicken Pesto Pasta", 20, 20);
insert into Recipe values(1, "Matt's Popcorn", 20, 20);
insert into RecipeAuthor values("Matt Marlow", 0);
insert into RecipeAuthor values("Matt Marlow", 1);
call searchForShoppingListByDate("2023-4-27", @ID);
call createListSoughtItem(2, 1);
call retrieveIngredientsFromRecipe(0);
call returnAllSoughtItems(3);
select * from Used_Ingredients, Recipe where Recipe.Recipe_ID = 1;
select * from Used_Ingredients ;
select * from Recipe;
select * from FoodQuantity;
select @ID;
select * from Tool;
select * from ShoppingList;
select * from ListSoughtItems;
delete from ListSoughtItems where List_ID = 1;
delete from ShoppingList where List_ID = 1;
delete from RecipeAuthor where Recipe_ID = 0;
delete from Recipe where Recipe_ID = 0;
delete from FoodQuantity where Food_ID = 0;
select * from Recipe;
