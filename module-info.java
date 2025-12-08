/**
 * 
 */
/**
 * 
 */
module mini_java_project_bibliothèque {
	requires java.sql;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
    exports main;       
    exports classes;    
    exports database;
    opens main to javafx.fxml;
    opens controller to javafx.fxml, javafx.base;
    opens classes to javafx.fxml;
    exports controller; 
    requires java.prefs;      // ← ajoute cette ligne

}
