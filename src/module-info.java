module CaLouselF {
	
	requires java.sql;
	requires javafx.graphics;
	requires javafx.controls;
	
	opens controller;
	opens database;
	opens main;
	opens model;
	opens views;
	
	exports main;
}