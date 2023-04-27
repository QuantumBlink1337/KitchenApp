CREATE SCHEMA IF NOT EXISTS KITCHEN;
USE KITCHEN;
ALTER DATABASE Kitchen
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_520_ci;
CREATE TABLE IF NOT EXISTS ItemType (
	ID_Type varchar(1),
    ID_Value int primary key
);
CREATE TABLE IF NOT EXISTS FoodItem (
	Food_ID int primary key,
    Food_ID_Type varchar(1),
    Food_name varchar(50),
    Food_cost float,
    Food_mass float, # in grams
    FPurchase_date date,
    FBestBy_Date date,
    Food_days_age int,
    Days_past_spoilage int,
    Food_cal int,
    Food_fat int,
    Food_protein int,
    Food_carbs int,
    foreign key (Food_ID) references ItemType(ID_Value)

);

CREATE TABLE IF NOT EXISTS Recipe (
	Recipe_ID int primary key,
    Recipe_ID_Type varchar(1),
    Author varchar(50),
    Recipe_name varchar(50),
    Recipe_time int, # in minutes
		Prep_time int,
        Cook_time int,
	Recipe_cal int,
    Recipe_fat int,
    Recipe_carbs int,
    Recipe_protein int,
    foreign key (Recipe_ID) references ItemType(ID_Value)
);
CREATE TABLE IF NOT EXISTS Tool (
	Tool_ID int primary key,
    Tool_ID_Type varchar(1),
    Recipe_ID int,
    foreign key (Recipe_ID) references Recipe(Recipe_ID)

);
CREATE TABLE IF NOT EXISTS CleaningItem (
	Cleaning_ID int primary key,
    Cleaning_ID_Type varchar(1),
    Clean_mass float,
    Clean_name varchar(50),
    Clean_cost float,
    Purchase_date datetime,
    foreign key (Cleaning_ID) references ItemType(ID_Value)
);
CREATE TABLE IF NOT EXISTS ShoppingList (
	ListDate date,
    Overall_cost float,
    List_ID int primary key,
    List_ID_Type varchar(1),
    foreign key (List_ID) references ItemType(ID_Value)
);
CREATE TABLE IF NOT EXISTS ListSoughtItems (
	Item_mass float,
    Item_cost float,
    Item_name varchar(40),
    List_date date,
    Item_ID int primary key,
	List_ID int,
    Item_Type varchar(1),
    foreign key (List_ID) references ShoppingList(List_ID),
    foreign key (Item_ID) references ItemType(ID_Value)
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
    Food_name varchar(50),
    UCount int,
    UMass int,
    foreign key (Food_ID) references FoodItem(Food_ID),
	foreign key (Recipe_ID) references Recipe(Recipe_ID)
);


insert into ItemType values('F', 1);
insert into ItemType values('F', 2);
insert into ItemType values('F', 3);
insert into ItemType values('F', 4);

insert into FoodItem values(1, 'F', "Shell Pasta", 2.5, 200, '2023-4-25', '2025-4-25', 0, 0, 200, 10, 2, 15);




