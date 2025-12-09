package controller;

import classes.Livre;
import classes.Emprunt;
import database.DBEmprunt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import main.BibliothequeService;
import javafx.scene.image.Image;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.shape.Rectangle;



/**
 * ═══════════════════════════════════════════════════════════════════
 * 🎵 SPOTIFY-INSPIRED DASHBOARD CONTROLLER - PRO MAX ULTRA VERSION
 * ═══════════════════════════════════════════════════════════════════
 * 
 * Professional library management dashboard with modern UI/UX
 * Features: Smooth animations, fluid transitions, glass morphism effects
 * 
 * @author GHARBI'S LIBRARY TEAM
 * @version 2.0 PRO MAX ULTRA
 */
public class DashboardController {

    // ═══════════════════════════════════════════════════════════════════
    // SIDEBAR NAVIGATION
    // ═══════════════════════════════════════════════════════════════════
    @FXML private Button btnHome;
    @FXML private Button btnSearch;
    @FXML private Button btnLibrary;
    @FXML private Button btnStaff;
    @FXML private Button btnMembers;
    @FXML private Button btnCategories;
    @FXML private Button btnFines;
    @FXML private Button btnLoans;
    @FXML private Button btnStats;
    @FXML private Button btnNotifications;
    
    // ═══════════════════════════════════════════════════════════════════
    // USER PROFILE
    // ═══════════════════════════════════════════════════════════════════
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label greetingLabel;
    @FXML private Region profileAvatar;
    
    // ═══════════════════════════════════════════════════════════════════
    // SEARCH
    // ═══════════════════════════════════════════════════════════════════
    @FXML private TextField searchField;
    
    // ═══════════════════════════════════════════════════════════════════
    // CONTENT AREAS
    // ═══════════════════════════════════════════════════════════════════
    @FXML private StackPane contentArea;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dashboardContent;
    @FXML private GridPane quickAccessGrid;
    @FXML private HBox recentBooksContainer;
    @FXML private GridPane categoriesGrid;
    @FXML private HBox availableBooksContainer;
    
    // ═══════════════════════════════════════════════════════════════════
    // BOTTOM BAR STATS
    // ═══════════════════════════════════════════════════════════════════
    @FXML private Label totalBooksCount;
    @FXML private Label activeLoansCount;
    @FXML private Label availableCount;
    @FXML private Label currentBookTitle;
    @FXML private Label currentBookAuthor;
    @FXML private Label notificationBadge;
    @FXML private Slider volumeSlider;
    
    // ═══════════════════════════════════════════════════════════════════
    // ADMIN SECTION
    // ═══════════════════════════════════════════════════════════════════
    @FXML private Label adminSectionLabel;
    
    // ═══════════════════════════════════════════════════════════════════
    // INTERNAL STATE
    // ═══════════════════════════════════════════════════════════════════
    private String userRole;
    private String username;
    private static String sessionUsername;
    private static String sessionRole;
    
    private BibliothequeService service = new BibliothequeService();
    private List<String> navigationHistory = new ArrayList<>();
    private int navigationIndex = -1;
    @FXML
    private Button settingsButton;
    private Button currentActiveButton;
    private Timeline ambientAnimation;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML private HBox authorsContainer; 
    // ═══════════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════
    
    @FXML
    private void initialize() {
        System.out.println("🎨 Initializing PRO MAX ULTRA Dashboard...");
        
        if (sessionUsername != null && sessionRole != null) {
            setUserInfo(sessionUsername, sessionRole);
        }
        if (notificationBadge != null) {
            notificationBadge.setVisible(false);
            notificationBadge.setManaged(false);
        }
        
        setupSearchListener();
        setupAnimations();
        setupAmbientEffects();
        setupProfileAvatar();
        
        currentActiveButton = btnHome;
        updateGreeting();
        
        System.out.println("✅ Dashboard initialized successfully!");
    }

    // ═══════════════════════════════════════════════════════════════════
    // SESSION MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════
    
    public static String getSessionUsername() {
        return sessionUsername;
    }
    
    public static String getSessionRole() {
        return sessionRole;
    }

    public void setUserInfo(String username, String role) {
        this.username = username;
        this.userRole = role;
        
        sessionUsername = username;
        sessionRole = role;
        
        if (welcomeLabel != null) {
            welcomeLabel.setText(username);
            animateLabelWithGlow(welcomeLabel);
        }
        
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        
        if (roleLabel != null) {
            roleLabel.setText(isAdmin ? "Administrator" : "Member");
            animateLabelWithGlow(roleLabel);
        }
        
        configureAccess();
        loadDashboardContent();
        updateBottomBarStats();
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESS CONTROL
    // ═══════════════════════════════════════════════════════════════════
    
    private void configureAccess() {
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);
        
        // Admin controls with smooth fade
        setVisibleWithAnimation(adminSectionLabel, isAdmin);
        setVisibleWithAnimation(btnStaff, isAdmin);
        setVisibleWithAnimation(btnMembers, isAdmin);
        setVisibleWithAnimation(btnCategories, isAdmin);
        setVisibleWithAnimation(btnFines, isAdmin);
        
        // Public access controls
        setVisibleWithAnimation(btnHome, true);
        setVisibleWithAnimation(btnSearch, true);
        setVisibleWithAnimation(btnLibrary, true);
        setVisibleWithAnimation(btnLoans, true);
        setVisibleWithAnimation(btnStats, true);
        setVisibleWithAnimation(btnNotifications, true);
    }
    
    private void setVisibleWithAnimation(Node node, boolean visible) {
        if (node == null) return;
        
        if (visible) {
            node.setVisible(true);
            node.setManaged(true);
            FadeTransition fade = new FadeTransition(Duration.millis(400), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } else {
            FadeTransition fade = new FadeTransition(Duration.millis(300), node);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                node.setVisible(false);
                node.setManaged(false);
            });
            fade.play();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // PROFILE AVATAR ANIMATION
    // ═══════════════════════════════════════════════════════════════════
    
    private void setupProfileAvatar() {
        if (profileAvatar == null) return;
        
        // Pulsing glow effect
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(profileAvatar.scaleXProperty(), 1.0),
                new KeyValue(profileAvatar.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(1500), 
                new KeyValue(profileAvatar.scaleXProperty(), 1.05),
                new KeyValue(profileAvatar.scaleYProperty(), 1.05)
            )
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
        
        // Glow effect
        Glow glow = new Glow(0.3);
        profileAvatar.setEffect(glow);
    }

    // ═══════════════════════════════════════════════════════════════════
    // GREETING UPDATE
    // ═══════════════════════════════════════════════════════════════════
    
    private void updateGreeting() {
        if (greetingLabel == null) return;
        
        int hour = LocalDateTime.now().getHour();
        String greeting;
        
        if (hour < 12) {
            greeting = "☀️ Good Morning";
        } else if (hour < 18) {
            greeting = "🌤️ Good Afternoon";
        } else {
            greeting = "🌙 Good Evening";
        }
        
        greetingLabel.setText(greeting);
        
        // Smooth fade-in with scale
        ParallelTransition parallel = new ParallelTransition();
        
        FadeTransition fade = new FadeTransition(Duration.millis(1200), greetingLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(1200), greetingLabel);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        parallel.getChildren().addAll(fade, scale);
        parallel.play();
    }

    // ═══════════════════════════════════════════════════════════════════
    // DASHBOARD CONTENT LOADING
    // ═══════════════════════════════════════════════════════════════════
    
    private void loadDashboardContent() {
        if (quickAccessGrid != null) {
            populateQuickAccess();
        }
        
        if (recentBooksContainer != null) {
            populateRecentBooks();
        }
        
        if (categoriesGrid != null) {
            populateCategories();
        }
        
        if (availableBooksContainer != null) {
            populateAvailableBooks();
        }
        if (authorsContainer != null) {
            populateAuthors();
        }
        
        animateDashboardEntrance();
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUICK ACCESS CARDS
    // ═══════════════════════════════════════════════════════════════════
    
    private void populateQuickAccess() {
        quickAccessGrid.getChildren().clear();
        
        String[][] quickAccess = {
            {"📚", "Browse Books", "library", "#1DB954"},
            {"📖", "My Loans", "loans", "#E13300"},
            {"🔍", "Search", "search", "#2D46B9"},
            {"📊", "Statistics", "stats", "#E8115B"}
        };
        
        int col = 0;
        for (String[] item : quickAccess) {
            VBox card = createQuickAccessCard(item[0], item[1], item[2], item[3]);
            quickAccessGrid.add(card, col++, 0);
            
            if (col >= 4) break;
        }
    }

    private VBox createQuickAccessCard(String icon, String title, String action, String accentColor) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("quick-access-card");
        card.setPadding(new Insets(20));
        card.setPrefHeight(100);
        card.setMaxWidth(Double.MAX_VALUE);
        
        HBox content = new HBox(16);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 36px;");
        
        // Add subtle rotation animation to icon
        RotateTransition rotate = new RotateTransition(Duration.millis(300), iconLabel);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("quick-access-title");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        content.getChildren().addAll(iconLabel, titleLabel);
        card.getChildren().add(content);
        
        // Click handler
        card.setOnMouseClicked(e -> {
            handleQuickAccessClick(action);
            createRippleEffect(card, e.getX(), e.getY(), accentColor);
        });
        
        // Enhanced hover effect with glow
        card.setOnMouseEntered(e -> {
            ParallelTransition parallel = new ParallelTransition();
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.03);
            scale.setToY(1.03);
            
            // Add glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web(accentColor));
            glow.setRadius(20);
            glow.setSpread(0.4);
            card.setEffect(glow);
            
            // Rotate icon
            rotate.setByAngle(10);
            
            parallel.getChildren().addAll(scale, rotate);
            parallel.play();
        });
        
        card.setOnMouseExited(e -> {
            ParallelTransition parallel = new ParallelTransition();
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            // Remove glow
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.BLACK);
            shadow.setRadius(12);
            shadow.setSpread(0);
            card.setEffect(shadow);
            
            // Reset icon rotation
            rotate.setByAngle(-10);
            
            parallel.getChildren().addAll(scale, rotate);
            parallel.play();
        });
        
        return card;
    }

    private void handleQuickAccessClick(String action) {
        switch (action) {
            case "library":
                showBooks();
                break;
            case "loans":
                openBorrow(null);
                break;
            case "search":
                showSearch();
                break;
            case "stats":
                openStats(null);
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // RECENT BOOKS
    // ═══════════════════════════════════════════════════════════════════
    
    private void populateRecentBooks() {
        recentBooksContainer.getChildren().clear();
        
        List<Emprunt> recentLoans = DBEmprunt.getAllEmprunts().stream()
            .sorted((e1, e2) -> e2.getDateEmprunt().compareTo(e1.getDateEmprunt()))
            .limit(6)
            .collect(Collectors.toList());
        
        int delay = 0;
        for (Emprunt loan : recentLoans) {
            Livre livre = service.getAllLivres().stream()
                .filter(l -> l.getIsbn().equals(loan.getIsbnLivre()))
                .findFirst()
                .orElse(null);
            
            if (livre != null) {
                StackPane card = createBookCard(livre, delay);
                recentBooksContainer.getChildren().add(card);
                delay += 100;
            }
        }
        
        if (recentBooksContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("No recent loans yet");
            emptyLabel.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 14px; -fx-font-style: italic;");
            recentBooksContainer.getChildren().add(emptyLabel);
            animateFadeIn(emptyLabel, 0);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CATEGORIES
    // ═══════════════════════════════════════════════════════════════════
    
    private void populateCategories() {
        categoriesGrid.getChildren().clear();
        
        List<String> categories = service.getAllLivres().stream()
            .map(Livre::getCategorie)
            .distinct()
            .limit(8)
            .collect(Collectors.toList());
        
        int row = 0, col = 0;
        String[] colors = {
            "#1DB954", "#E13300", "#2D46B9", "#E8115B", 
            "#148A08", "#DC148C", "#8D67AB", "#E91429"
        };
        
        int index = 0;
        for (String category : categories) {
            VBox card = createCategoryCard(category, colors[index % colors.length], index);
            categoriesGrid.add(card, col, row);
            
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
            index++;
        }
    }
    private void populateAuthors() {
        if (authorsContainer == null) return;
        
        authorsContainer.getChildren().clear();
        
        // Get unique authors with their book counts
        Map<String, Long> authorBookCounts = service.getAllLivres().stream()
            .collect(Collectors.groupingBy(Livre::getAuteur, Collectors.counting()));
        
        // Get unique authors
        List<String> authors = new ArrayList<>(authorBookCounts.keySet());
        authors.sort(Comparator.comparing(a -> authorBookCounts.get(a)).reversed());
        
        int delay = 0;
        for (String author : authors.stream().limit(10).collect(Collectors.toList())) {
            long bookCount = authorBookCounts.get(author);
            StackPane card = createAuthorCard(author, (int) bookCount, delay);
            authorsContainer.getChildren().add(card);
            delay += 80;
        }
        
        if (authorsContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("No authors available");
            emptyLabel.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 14px; -fx-font-style: italic;");
            authorsContainer.getChildren().add(emptyLabel);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CREATE AUTHOR CARD (Spotify Artist Style)
    // ═══════════════════════════════════════════════════════════════════
    private StackPane createAuthorCard(String authorName, int bookCount, int delay) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(16));
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        card.getStyleClass().add("author-card");
        card.setStyle(
            "-fx-background-color: #181818;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        // ────────────────────────────────────────────────────────────
        // CIRCULAR AUTHOR AVATAR (now photo instead of initials)
        // ────────────────────────────────────────────────────────────
        StackPane avatarContainer = new StackPane();
        avatarContainer.setPrefSize(140, 140);

        // Background circle with gradient
        Region avatarBg = new Region();
        avatarBg.setPrefSize(140, 140);
        String[] gradients = {
            "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
            "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
            "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
            "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
            "linear-gradient(135deg, #30cfd0 0%, #330867 100%)",
            "linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)",
            "linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)"
        };

        int gradientIndex = Math.abs(authorName.hashCode()) % gradients.length;
        avatarBg.setStyle(
            "-fx-background-color: " + gradients[gradientIndex] + ";" +
            "-fx-background-radius: 50%;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);"
        );

        // Photo instead of initials
        ImageView avatarImage = new ImageView();
        avatarImage.setFitWidth(120);
        avatarImage.setFitHeight(120);
        avatarImage.setPreserveRatio(true);
        avatarImage.setSmooth(true);

        // Load /images/authors/<safeName>.png with fallback
        Image img = null;
        String safeName = authorName;
        String path = "/images/"+ safeName+ ".jpeg";

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new NullPointerException("Author image not found: " + path);
            }
            img = new Image(is);
        } catch (Exception ex) {
            InputStream is = getClass().getResourceAsStream("/images/author.jpeg");
            if (is != null) {
                img = new Image(is);
            }
        }

        if (img != null) {
            avatarImage.setImage(img);
        }

        avatarContainer.getChildren().addAll(avatarBg, avatarImage);

        // ────────────────────────────────────────────────────────────
        // AUTHOR INFO
        // ────────────────────────────────────────────────────────────
        VBox info = new VBox(4);
        info.setAlignment(Pos.CENTER);

        Label name = new Label(authorName);
        name.setWrapText(true);
        name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        name.setMaxWidth(150);
        name.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label bookLabel = new Label(bookCount + " book" + (bookCount != 1 ? "s" : ""));
        bookLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #B3B3B3;"
        );

        Label badge = new Label("Author");
        badge.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: #B3B3B3;" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 12;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        info.getChildren().addAll(name, bookLabel, badge);
        card.getChildren().addAll(avatarContainer, info);

        // PLAY BUTTON OVERLAY (unchanged)
        StackPane cardStack = new StackPane(card);

        Button playBtn = new Button("▶");
        playBtn.setStyle(
            "-fx-background-color: #1DB954;" +
            "-fx-text-fill: black;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 48px;" +
            "-fx-min-height: 48px;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
        );
        playBtn.setVisible(false);
        playBtn.setTranslateY(-50);
        StackPane.setAlignment(playBtn, Pos.CENTER);

        cardStack.getChildren().add(playBtn);

        // HOVER ANIMATIONS (just change avatarBg reference stays valid)
        card.setOnMouseEntered(e -> {
            playBtn.setVisible(true);

            ParallelTransition parallel = new ParallelTransition();

            TranslateTransition lift = new TranslateTransition(Duration.millis(250), card);
            lift.setToY(-8);

            FadeTransition btnFade = new FadeTransition(Duration.millis(250), playBtn);
            btnFade.setFromValue(0);
            btnFade.setToValue(1);

            ScaleTransition btnScale = new ScaleTransition(Duration.millis(250), playBtn);
            btnScale.setFromX(0.7);
            btnScale.setFromY(0.7);
            btnScale.setToX(1.0);
            btnScale.setToY(1.0);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#1DB954"));
            glow.setRadius(20);
            glow.setSpread(0.3);
            card.setEffect(glow);

            ScaleTransition avatarScale = new ScaleTransition(Duration.millis(250), avatarBg);
            avatarScale.setToX(1.05);
            avatarScale.setToY(1.05);

            parallel.getChildren().addAll(lift, btnFade, btnScale, avatarScale);
            parallel.play();
        });

        card.setOnMouseExited(e -> {
            ParallelTransition parallel = new ParallelTransition();

            TranslateTransition lift = new TranslateTransition(Duration.millis(250), card);
            lift.setToY(0);

            FadeTransition btnFade = new FadeTransition(Duration.millis(200), playBtn);
            btnFade.setToValue(0);
            btnFade.setOnFinished(ev -> playBtn.setVisible(false));

            ScaleTransition avatarScale = new ScaleTransition(Duration.millis(250), avatarBg);
            avatarScale.setToX(1.0);
            avatarScale.setToY(1.0);

            card.setEffect(null);

            parallel.getChildren().addAll(lift, btnFade, avatarScale);
            parallel.play();
        });

        card.setOnMouseClicked(e -> showAuthorBooks(authorName));
        playBtn.setOnAction(e -> showAuthorBooks(authorName));

        animateFadeInUp(cardStack, delay);

        return cardStack;
    }


    // ═══════════════════════════════════════════════════════════════════
    // GET AUTHOR INITIALS
    // ═══════════════════════════════════════════════════════════════════
    private String getAuthorInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    // ═══════════════════════════════════════════════════════════════════
    // SHOW AUTHOR'S BOOKS MODAL (Spotify Artist Page Style)
    // ═══════════════════════════════════════════════════════════════════
    private void showAuthorBooks(String authorName) {
        // Create overlay
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        overlay.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        
        // Main modal
        VBox modal = new VBox(0);
        modal.setMaxWidth(920);
        modal.setMaxHeight(680);
        modal.setStyle(
            "-fx-background-color: #121212;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 40, 0, 0, 10);"
        );
        
        // ═══════════════════════════════════════════════════════════════
        // HEADER - Author Banner (like Spotify artist header)
        // ═══════════════════════════════════════════════════════════════
        StackPane headerStack = new StackPane();
        headerStack.setPrefHeight(280);
        
        // Gradient background
        Region headerBg = new Region();
        headerBg.setPrefHeight(280);
        String[] gradients = {
            "linear-gradient(180deg, #667eea 0%, #121212 100%)",
            "linear-gradient(180deg, #f093fb 0%, #121212 100%)",
            "linear-gradient(180deg, #4facfe 0%, #121212 100%)",
            "linear-gradient(180deg, #43e97b 0%, #121212 100%)",
            "linear-gradient(180deg, #fa709a 0%, #121212 100%)"
        };
        int gradientIndex = Math.abs(authorName.hashCode()) % gradients.length;
        headerBg.setStyle("-fx-background-color: " + gradients[gradientIndex] + ";");
        
        // Author info overlay
        HBox headerContent = new HBox(25);
        headerContent.setAlignment(Pos.BOTTOM_LEFT);
        headerContent.setPadding(new Insets(0, 40, 30, 40));
        
        // Large circular avatar
        StackPane avatarLarge = new StackPane();
        avatarLarge.setPrefSize(200, 200);
        
        Region avatarBg = new Region();
        avatarBg.setPrefSize(200, 200);
        avatarBg.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15);" +
            "-fx-background-radius: 50%;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 8);"
        );
        
        Label initials = new Label(getAuthorInitials(authorName));
        initials.setStyle(
            "-fx-font-size: 72px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4);"
        );
        
        avatarLarge.getChildren().addAll(avatarBg, initials);
        
        // Author details
        VBox details = new VBox(10);
        details.setAlignment(Pos.BOTTOM_LEFT);
        
        Label verified = new Label("✓ Verified Author");
        verified.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-opacity: 0.9;"
        );
        
        Label name = new Label(authorName);
        name.setStyle(
            "-fx-font-size: 72px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 4);"
        );
        
        // Get book count
        long bookCount = service.getAllLivres().stream()
            .filter(l -> l.getAuteur().equals(authorName))
            .count();
        
        Label stats = new Label(bookCount + " book" + (bookCount != 1 ? "s" : "") + " in library");
        stats.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: 600;" +
            "-fx-opacity: 0.8;"
        );
        
        details.getChildren().addAll(verified, name, stats);
        headerContent.getChildren().addAll(avatarLarge, details);
        
        headerStack.getChildren().addAll(headerBg, headerContent);
        
        // ═══════════════════════════════════════════════════════════════
        // ACTION BUTTONS
        // ═══════════════════════════════════════════════════════════════
        HBox actions = new HBox(12);
        actions.setPadding(new Insets(25, 40, 20, 40));
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setStyle("-fx-background-color: #121212;");
        
        Button followBtn = new Button("Follow");
        followBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 500px;" +
            "-fx-background-radius: 500px;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 28;" +
            "-fx-cursor: hand;"
        );
        addSpotifyButtonHover(followBtn);
        
        Button moreBtn = new Button("•••");
        moreBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8;"
        );
        addSpotifyButtonHover(moreBtn);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 40px;" +
            "-fx-min-height: 40px;" +
            "-fx-cursor: hand;"
        );
        addSpotifyButtonHover(closeBtn);
        closeBtn.setOnAction(e -> closeModal(overlay));
        
        actions.getChildren().addAll(followBtn, moreBtn, spacer, closeBtn);
        
        // ═══════════════════════════════════════════════════════════════
        // BOOKS LIST (like Spotify's song list)
        // ═══════════════════════════════════════════════════════════════
        VBox booksList = new VBox(0);
        booksList.setPadding(new Insets(0, 40, 30, 40));
        booksList.setStyle("-fx-background-color: #121212;");
        
        Label sectionTitle = new Label("Popular");
        sectionTitle.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 0 0 15 0;"
        );
        
        booksList.getChildren().add(sectionTitle);
        
        // Get author's books
        List<Livre> authorBooks = service.getAllLivres().stream()
            .filter(l -> l.getAuteur().equals(authorName))
            .collect(Collectors.toList());
        
        // Create book rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(280);
        
        VBox booksContainer = new VBox(0);
        
        int index = 1;
        for (Livre livre : authorBooks) {
            HBox bookRow = createBookRow(livre, index++, overlay);
            booksContainer.getChildren().add(bookRow);
        }
        
        scrollPane.setContent(booksContainer);
        booksList.getChildren().add(scrollPane);
        
        // ═══════════════════════════════════════════════════════════════
        // ASSEMBLE MODAL
        // ═══════════════════════════════════════════════════════════════
        modal.getChildren().addAll(headerStack, actions, booksList);
        overlay.getChildren().add(modal);
        
        // Add to scene
        StackPane root = (StackPane) contentArea.getParent();
        root.getChildren().add(overlay);
        
        // Entrance animation
        overlay.setOpacity(0);
        modal.setScaleX(0.9);
        modal.setScaleY(0.9);
        
        ParallelTransition entrance = new ParallelTransition(
            createFadeTransition(overlay, 0, 1, 250),
            createScaleTransition(modal, 0.9, 1.0, 300)
        );
        entrance.play();
        
        // Close on background click
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                closeModal(overlay);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════
    // CREATE BOOK ROW (Spotify song row style)
    // ═══════════════════════════════════════════════════════════════════
    private HBox createBookRow(Livre livre, int index, StackPane overlay) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));
        row.setPrefHeight(64);
        row.setStyle("-fx-cursor: hand;");
        
        // Track number
        Label number = new Label(String.valueOf(index));
        number.setPrefWidth(40);
        number.setAlignment(Pos.CENTER);
        number.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: 500;"
        );
        
        // Book cover thumbnail
        StackPane coverThumb = new StackPane();
        coverThumb.setPrefSize(48, 48);
        
        ImageView thumbImg = new ImageView();
        thumbImg.setFitWidth(48);
        thumbImg.setFitHeight(48);
        thumbImg.setPreserveRatio(true);
        thumbImg.setSmooth(true);
        
        Image image = loadBookImage(livre);
        if (image != null) {
            thumbImg.setImage(image);
        }
        
        Rectangle clip = new Rectangle(48, 48);
        clip.setArcWidth(4);
        clip.setArcHeight(4);
        thumbImg.setClip(clip);
        
        coverThumb.getChildren().add(thumbImg);
        
        // Book info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Label title = new Label(livre.getTitre());
        title.setMaxWidth(400);
        title.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: 500;"
        );
        
        Label category = new Label(livre.getCategorie());
        category.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 13px;"
        );
        
        info.getChildren().addAll(title, category);
        
        // Availability badge
        Label badge = new Label(livre.isDisponible() ? "Available" : "Borrowed");
        badge.setStyle(
            "-fx-background-color: " + (livre.isDisponible() ? "rgba(29,185,84,0.15)" : "rgba(231,76,60,0.15)") + ";" +
            "-fx-text-fill: " + (livre.isDisponible() ? "#1DB954" : "#E74C3C") + ";" +
            "-fx-padding: 4 10;" +
            "-fx-background-radius: 12;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        // More button
        Button moreBtn = new Button("•••");
        moreBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-opacity: 0;"
        );
        
        row.getChildren().addAll(number, coverThumb, info, badge, moreBtn);
        
        // Hover effect
        row.setOnMouseEntered(e -> {
            row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-cursor: hand; -fx-background-radius: 4;");
            moreBtn.setStyle(moreBtn.getStyle() + "-fx-opacity: 1;");
            number.setText("▶");
            number.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
            );
        });
        
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-cursor: hand;");
            moreBtn.setStyle(moreBtn.getStyle().replace("-fx-opacity: 1;", "-fx-opacity: 0;"));
            number.setText(String.valueOf(index));
            number.setStyle(
                "-fx-text-fill: #B3B3B3;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: 500;"
            );
        });
        
        // Click to show book details
        row.setOnMouseClicked(e -> {
            closeModal(overlay);
            showBookDetails(livre);
        });
        
        return row;
    }
    /**
     * ═══════════════════════════════════════════════════════════════════
     * 📚 SHOW ALL AUTHORS PAGE - Add to DashboardController
     * ═══════════════════════════════════════════════════════════════════
     */

    @FXML
    private void showAllAuthors() {
        System.out.println("📚 Showing all authors...");
        
        if (availableBooksContainer == null) return;
        
        // Clear current view
        availableBooksContainer.getChildren().clear();
        
        // Get all unique authors
        Map<String, Long> authorBookCounts = service.getAllLivres().stream()
            .collect(Collectors.groupingBy(Livre::getAuteur, Collectors.counting()));
        
        List<String> authors = new ArrayList<>(authorBookCounts.keySet());
        authors.sort(Comparator.comparing(a -> authorBookCounts.get(a)).reversed());
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 8, 4));
        
        Label title = new Label("All Authors");
        title.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label count = new Label(authors.size() + " authors");
        count.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 14px;"
        );
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 500px;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> populateAvailableBooks());
        addSpotifyButtonHover(backBtn);
        
        header.getChildren().addAll(title, spacer, count, backBtn);
        availableBooksContainer.getChildren().add(header);
        
        // Display all author cards
        int delay = 40;
        for (String author : authors) {
            long bookCount = authorBookCounts.get(author);
            StackPane card = createAuthorCard(author, (int) bookCount, delay);
            availableBooksContainer.getChildren().add(card);
            delay += 40;
        }
        
        // Animate entrance
        FadeTransition fade = new FadeTransition(Duration.millis(300), availableBooksContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private VBox createCategoryCard(String category, String color, int delay) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setPrefHeight(130);
        card.setSpacing(10);
        card.setStyle(String.format(
            "-fx-background-color: linear-gradient(135deg, %s, %s); " +
            "-fx-background-radius: 14; -fx-cursor: hand;",
            color, adjustColorBrightness(color, -0.22)
        ));

        // --- IMAGE + TITLE (Spotify style row) ---
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);

        // Category cover image
        ImageView cover = new ImageView();
        cover.setFitWidth(70);
        cover.setFitHeight(70);
        cover.setPreserveRatio(true);
        cover.setSmooth(true);

        // Each category has its own image: /images/categories/ategory>.png
        // Example: "Science" -> /images/categories/Science.png
        String imgPath = "/images/" + category + ".jpeg";
        try {
            Image img = new Image(
                Objects.requireNonNull(
                    getClass().getResourceAsStream(imgPath),
                    "Category image not found: " + imgPath
                )
            );
            cover.setImage(img);
        } catch (Exception ex) {
            // Optional: you can load a default category image here
            // Image fallback = new Image(getClass().getResourceAsStream("/images/categories/default.png"));
            // cover.setImage(fallback);
        }

        // Rounded image container with subtle glass effect
        StackPane coverWrapper = new StackPane(cover);
        coverWrapper.setPrefSize(72, 72);
        coverWrapper.setStyle(
            "-fx-background-color: rgba(15,23,42,0.45);" +
            "-fx-background-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 14, 0.3, 0, 6);"
        );

        // Category title + optional subtitle
        VBox textBox = new VBox(4);
        Label titleLabel = new Label(category);
        titleLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 2);"
        );

        Label subtitle = new Label("Browse " + category + " books");
        subtitle.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: rgba(243,244,246,0.82);"
        );

        textBox.getChildren().addAll(titleLabel, subtitle);
        row.getChildren().addAll(coverWrapper, textBox);

        card.getChildren().add(row);

        // Click handler
        card.setOnMouseClicked(e -> {
            filterByCategory(category);
            createRippleEffect(card, e.getX(), e.getY(), "#FFFFFF");
        });

        // Enhanced hover with 3D effect (conservé mais un peu plus premium)
        card.setOnMouseEntered(e -> {
            ParallelTransition parallel = new ParallelTransition();

            ScaleTransition scale = new ScaleTransition(Duration.millis(250), card);
            scale.setToX(1.06);
            scale.setToY(1.06);

            RotateTransition rotate = new RotateTransition(Duration.millis(250), card);
            rotate.setAxis(javafx.geometry.Point3D.ZERO.add(1, 1, 0));
            rotate.setByAngle(3);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.web(color));
            glow.setRadius(26);
            glow.setSpread(0.55);
            card.setEffect(glow);

            parallel.getChildren().addAll(scale, rotate);
            parallel.play();
        });

        card.setOnMouseExited(e -> {
            ParallelTransition parallel = new ParallelTransition();

            ScaleTransition scale = new ScaleTransition(Duration.millis(250), card);
            scale.setToX(1.0);
            scale.setToY(1.0);

            RotateTransition rotate = new RotateTransition(Duration.millis(250), card);
            rotate.setAxis(javafx.geometry.Point3D.ZERO.add(1, 1, 0));
            rotate.setByAngle(-3);

            card.setEffect(null);

            parallel.getChildren().addAll(scale, rotate);
            parallel.play();
        });

        // Entrance animation
        animateFadeInUp(card, delay * 50);

        return card;
    }


    // ═══════════════════════════════════════════════════════════════════
    // AVAILABLE BOOKS
    // ═══════════════════════════════════════════════════════════════════
    
    private void populateAvailableBooks() {
        availableBooksContainer.getChildren().clear();
        
        List<Livre> availableBooks = service.getAllLivres().stream()
            .filter(Livre::isDisponible)
            .limit(10)
            .collect(Collectors.toList());
        
        int delay = 0;
        for (Livre livre : availableBooks) {
            StackPane card = createBookCard(livre, delay);
            availableBooksContainer.getChildren().add(card);
            delay += 100;
        }
    }

    private StackPane createBookCard(Livre livre, int delay) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("book-card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        
        // --- COVER IMAGE ---
        ImageView img = new ImageView();
        img.setFitWidth(150);
        img.setFitHeight(180);
        img.setPreserveRatio(true);
        img.setSmooth(true);

        Image image = null;
        String path = livre.getImagePath(); // ex: "/images/téléchargement.png"

        try {
            if (path != null && !path.isBlank()) {
                // 1) chemin explicite stocké dans le livre
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + path);
                }
                image = new Image(is);
            } else {
                // 2) tentative avec le titre du livre
                String autoPath = "/images/" + livre.getTitre() + ".jpeg";
                InputStream is = getClass().getResourceAsStream(autoPath);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + autoPath);
                }
                image = new Image(is);
            }
        } catch (NullPointerException ex) {
            // 3) image par défaut absolue
            InputStream is = getClass().getResourceAsStream("/images/book.jpeg");
            if (is != null) {
                image = new Image(is);
            } else {
                System.out.println("❌ Default image /images/book.jpeg introuvable");
            }
        }

        // applique l'image si on en a une
        if (image != null) {
            img.setImage(image);
        }

        // Book cover with gradient (background behind the image)
        Region cover = new Region();
        cover.setPrefSize(150, 150);
        cover.setStyle(
            "-fx-background-color: linear-gradient(135deg, #282828, #181818); " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 4);"
        );

        StackPane coverStack = new StackPane();
        coverStack.getChildren().add(cover);
        if (img.getImage() != null) {
            coverStack.getChildren().add(img);
        }

        // Book info
        VBox info = new VBox(6);
        
        Label title = new Label(livre.getTitre());
        title.getStyleClass().add("book-card-title");
        title.setWrapText(true);
        title.setMaxWidth(150);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label author = new Label(livre.getAuteur());
        author.getStyleClass().add("book-card-author");
        author.setStyle("-fx-font-size: 12px; -fx-text-fill: #B3B3B3;");
        
        // Availability badge
        Label badge = new Label(livre.isDisponible() ? "Available" : "Borrowed");
        badge.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: %s; " +
            "-fx-background-radius: 12; -fx-padding: 3 10; " +
            "-fx-font-size: 10px; -fx-font-weight: bold;",
            livre.isDisponible() ? "#1DB954" : "#E13300",
            livre.isDisponible() ? "#000000" : "#FFFFFF"
        ));
        
        info.getChildren().addAll(title, author, badge);
        card.getChildren().addAll(coverStack, info);

        // Play button overlay
        StackPane cardStack = new StackPane(card);
        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("play-button");
        playBtn.setStyle(
            "-fx-background-color: #1DB954; -fx-text-fill: black; " +
            "-fx-background-radius: 50%; -fx-min-width: 48; -fx-min-height: 48; " +
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        playBtn.setVisible(false);
        playBtn.setTranslateY(-40);
        StackPane.setAlignment(playBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(playBtn, new Insets(0, 16, 80, 0));
        
        cardStack.getChildren().add(playBtn);
        
        // Hover animations (inchangés)
        card.setOnMouseEntered(e -> {
            playBtn.setVisible(true);
            
            ParallelTransition parallel = new ParallelTransition();
            
            TranslateTransition lift = new TranslateTransition(Duration.millis(250), card);
            lift.setToY(-8);
            
            ScaleTransition btnScale = new ScaleTransition(Duration.millis(250), playBtn);
            btnScale.setFromX(0.7);
            btnScale.setFromY(0.7);
            btnScale.setToX(1.0);
            btnScale.setToY(1.0);
            
            FadeTransition btnFade = new FadeTransition(Duration.millis(250), playBtn);
            btnFade.setFromValue(0);
            btnFade.setToValue(1);
            
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#1DB954"));
            glow.setRadius(20);
            glow.setSpread(0.3);
            card.setEffect(glow);
            
            parallel.getChildren().addAll(lift, btnScale, btnFade);
            parallel.play();
        });
        
        card.setOnMouseExited(e -> {
            ParallelTransition parallel = new ParallelTransition();
            
            TranslateTransition lift = new TranslateTransition(Duration.millis(250), card);
            lift.setToY(0);
            
            FadeTransition btnFade = new FadeTransition(Duration.millis(200), playBtn);
            btnFade.setToValue(0);
            btnFade.setOnFinished(ev -> playBtn.setVisible(false));
            
            card.setEffect(null);
            
            parallel.getChildren().addAll(lift, btnFade);
            parallel.play();
        });
        card.setOnMouseClicked(e -> showBookDetails(livre));
        playBtn.setOnAction(e -> showBookDetails(livre));
        
        // Entrance animation
        animateFadeInUp(cardStack, delay);
        
        return cardStack;
    }


    // ═══════════════════════════════════════════════════════════════════
    // SEARCH FUNCTIONALITY
    // ═══════════════════════════════════════════════════════════════════
    
    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && newVal.length() > 2) {
                    performSearch(newVal);
                }
            });
            
            // Add search icon glow on focus
            searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (isNowFocused) {
                    DropShadow glow = new DropShadow();
                    glow.setColor(Color.web("#1DB954"));
                    glow.setRadius(15);
                    searchField.getParent().setEffect(glow);
                } else {
                    searchField.getParent().setEffect(null);
                }
            });
        }
    }

    /**
     * 
     * ═══════════════════════════════════════════════════════════════════
     * 🔍 LIVE SEARCH – DASHBOARD PRO
     * ═══════════════════════════════════════════════════════════════════
     */
    @FXML
    private void onOpenSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Paramètres");
            stage.initOwner(settingsButton.getScene().getWindow()); // ← ici à la place de someNode
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch(String query) {
        System.out.println("🔍 Searching for: " + query);

        if (availableBooksContainer == null) {
            return;
        }

        String normalized = query.trim().toLowerCase();
        availableBooksContainer.getChildren().clear();

        List<Livre> results = service.getAllLivres().stream()
            .filter(l -> {
                String titre = l.getTitre() != null ? l.getTitre().toLowerCase() : "";
                String auteur = l.getAuteur() != null ? l.getAuteur().toLowerCase() : "";
                String isbn = l.getIsbn() != null ? l.getIsbn().toLowerCase() : "";
                return titre.contains(normalized) ||
                       auteur.contains(normalized) ||
                       isbn.contains(normalized);
            })
            .limit(30)
            .collect(Collectors.toList());

        int delay = 40;
        for (Livre livre : results) {
            StackPane card = createBookCard(livre, delay);
            availableBooksContainer.getChildren().add(card);
            delay += 40;
        }

        if (results.isEmpty()) {
            VBox emptyBox = new VBox(6);
            emptyBox.setAlignment(Pos.CENTER_LEFT);
            emptyBox.setPadding(new Insets(8, 0, 0, 4));

            Label title = new Label("No results for \"" + query + "\"");
            title.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
            );

            Label subtitle = new Label("Try another keyword, author, or ISBN.");
            subtitle.setStyle(
                "-fx-text-fill: #B3B3B3;" +
                "-fx-font-size: 12px;"
            );

            emptyBox.getChildren().addAll(title, subtitle);
            availableBooksContainer.getChildren().add(emptyBox);
            animateFadeInUp(emptyBox, 0);
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // BOTTOM BAR STATS UPDATE
    // ═══════════════════════════════════════════════════════════════════
    private Image loadBookImage(Livre livre) {
        Image image = null;
        String path = livre.getImagePath();
        try {
            if (path != null && !path.isBlank()) {
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) throw new NullPointerException("Image not found: " + path);
                image = new Image(is);
            } else {
                String autoPath = "/images/" + livre.getTitre() + ".jpeg";
                InputStream is = getClass().getResourceAsStream(autoPath);
                if (is == null) throw new NullPointerException("Image not found: " + autoPath);
                image = new Image(is);
            }
        } catch (NullPointerException ex) {
            InputStream is = getClass().getResourceAsStream("/images/book.jpeg");
            if (is != null) {
                image = new Image(is);
            }
        }
        return image;
    }
    
    private void updateBottomBarStats() {
        if (totalBooksCount != null) {
            int total = service.getAllLivres().size();
            animateNumber(totalBooksCount, 0, total);
        }
        
        if (activeLoansCount != null) {
            long active = DBEmprunt.getAllEmprunts().stream()
                .filter(e -> e.getDateRetour() == null || 
                       e.getDateRetour().isAfter(java.time.LocalDate.now()))
                .count();
            animateNumber(activeLoansCount, 0, (int) active);
        }
        
        if (availableCount != null) {
            long available = service.getAllLivres().stream()
                .filter(Livre::isDisponible)
                .count();
            animateNumber(availableCount, 0, (int) available);
        }
    }

    private void animateNumber(Label label, int from, int to) {
        Timeline timeline = new Timeline();
        final int[] counter = {from};
        final int step = Math.max(1, (to - from) / 50);
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), e -> {
            counter[0] = Math.min(counter[0] + step, to);
            label.setText(String.valueOf(counter[0]));
            
            if (counter[0] >= to) {
                timeline.stop();
                // Pulse effect when done
                ScaleTransition pulse = new ScaleTransition(Duration.millis(200), label);
                pulse.setFromX(1.0);
                pulse.setFromY(1.0);
                pulse.setToX(1.1);
                pulse.setToY(1.1);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(2);
                pulse.play();
            }
        });
        
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // ═══════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════
    
    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("sidebar-item-active");
            
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), currentActiveButton);
            fadeOut.setToValue(0.7);
            fadeOut.play();
        }
        
        if (button != null) {
            button.getStyleClass().add("sidebar-item-active");
            currentActiveButton = button;
            
            // Fade in + scale animation
            ParallelTransition parallel = new ParallelTransition();
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), button);
            fadeIn.setToValue(1.0);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), button);
            scale.setFromX(0.98);
            scale.setFromY(0.98);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            parallel.getChildren().addAll(fadeIn, scale);
            parallel.play();
        }
    }

    @FXML
    private void showDashboard() {
        System.out.println("🏠 showDashboard called");
        setActiveButton(btnHome);
        scrollPane.setVisible(true);
        scrollPane.setManaged(true);
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        recordNavigation("dashboard");
        animateFadeIn(dashboardContent, 0);
    }

    @FXML
    private void showSearch() {
        setActiveButton(btnSearch);
        if (searchField != null) {
            searchField.requestFocus();
            
            // Pulse search field
            ScaleTransition pulse = new ScaleTransition(Duration.millis(300), searchField.getParent());
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.03);
            pulse.setToY(1.03);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        }
    }

    @FXML
    private void showBooks() {
        setActiveButton(btnLibrary);
        loadContent("/fxml/livre_dialog.fxml");
        recordNavigation("books");
    }

    @FXML
    private void openStaff(javafx.event.ActionEvent event) {
        if (checkAccess("admin")) {
            setActiveButton(btnStaff);
            loadContent("/fxml/staff_dialog.fxml");
        }
    }

    @FXML
    private void openMembers() {
        if (checkAccess("admin")) {
            setActiveButton(btnMembers);
            loadContent("/fxml/adherent_dialog.fxml");
        }
    }

    @FXML
    private void openCategory(javafx.event.ActionEvent event) {
        if (checkAccess("admin")) {
            setActiveButton(btnCategories);
            loadContent("/fxml/category_dialog.fxml");
        }
    }

    @FXML
    private void openFines(javafx.event.ActionEvent event) {
        if (checkAccess("admin")) {
            setActiveButton(btnFines);
            loadContent("/fxml/fines.fxml");
        }
    }

    @FXML
    private void openBorrow(javafx.event.ActionEvent event) {
        setActiveButton(btnLoans);
        loadContent("/fxml/emprunt_dialog.fxml");
    }

    @FXML
    private void openStats(javafx.event.ActionEvent event) {
        setActiveButton(btnStats);
        loadContent("/fxml/statistics.fxml");
    }

    @FXML
    private void openNotifications() {
        setActiveButton(btnNotifications);
        loadContent("/fxml/notifications.fxml");
        if (notificationBadge != null) {
            notificationBadge.setVisible(false);
            notificationBadge.setManaged(false);
        }
    }
    private void markNotificationAsUnread() {
        if (notificationBadge == null) return;

        notificationBadge.setText("");  // ou "!" ou "1" plus tard
        notificationBadge.setManaged(true);
        notificationBadge.setVisible(true);
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            // Slide out old content
            if (scrollPane.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), scrollPane);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    scrollPane.setVisible(false);
                    scrollPane.setManaged(false);
                    
                    // Slide in new content
                    contentArea.setVisible(true);
                    contentArea.setManaged(true);
                    contentArea.getChildren().setAll(view);
                    
                    animateSlideIn(view);
                });
                fadeOut.play();
            } else {
                contentArea.getChildren().setAll(view);
                animateSlideIn(view);
            }
            
        } catch (IOException e) {
            System.err.println("❌ ERROR loading page: " + fxmlPath);
            e.printStackTrace();
            showErrorNotification("Failed to load page");
        }
    }

    private boolean checkAccess(String requiredRole) {
        if (requiredRole.equalsIgnoreCase(userRole)) {
            return true;
        }
        
        showAccessDeniedAlert();
        return false;
    }

    private void showAccessDeniedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText("🔒 Administrator Access Required");
        alert.setContentText("This feature requires administrator privileges.");
        
        // Customize alert style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        
        alert.showAndWait();
    }

    /**
     * Enregistre une route logique pour la navigation (ex: "dashboard", "books", "loans").
     */
    private void recordNavigation(String route) {
        if (route == null || route.isEmpty()) return;

        // On coupe l’historique "en avant" si on navigue après un back
        if (navigationIndex < navigationHistory.size() - 1) {
            navigationHistory = new ArrayList<>(navigationHistory.subList(0, navigationIndex + 1));
        }

        navigationHistory.add(route);
        navigationIndex = navigationHistory.size() - 1;
    }

    private void goToRoute(String route) {
        switch (route) {
            case "dashboard":
                showDashboard();
                break;
            case "search":
                showSearch();
                break;
            case "books":
                showBooks();
                break;
            case "loans":
                openBorrow(null);
                break;
            case "stats":
                openStats(null);
                break;
            case "notifications":
                openNotifications();
                break;
            default:
                System.out.println("❓ Unknown route: " + route);
        }
    }

    @FXML
    private void navigateBack() {
        System.out.println("⬅️ Navigate back");
        if (navigationHistory.isEmpty() || navigationIndex <= 0) {
            showErrorNotification("No previous page.");
            return;
        }
        navigationIndex--;
        String route = navigationHistory.get(navigationIndex);
        goToRoute(route);
    }

    @FXML
    private void navigateForward() {
        System.out.println("➡️ Navigate forward");
        if (navigationHistory.isEmpty() || navigationIndex >= navigationHistory.size() - 1) {
            showErrorNotification("No next page.");
            return;
        }
        navigationIndex++;
        String route = navigationHistory.get(navigationIndex);
        goToRoute(route);
    }


    @FXML
    private void showNotificationsPopup() {
        System.out.println("🔔 Show notifications popup");

        if (contentArea == null) return;

        VBox panel = new VBox(10);
        panel.setPadding(new Insets(12));
        panel.setPrefWidth(260);
        panel.setStyle(
            "-fx-background-color: rgba(24,24,24,0.96);" +
            "-fx-background-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 22, 0, 0, 8);"
        );

        Label title = new Label("Notifications");
        title.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        Label empty = new Label("You are all caught up.");
        empty.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 12px;"
        );

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
            "-fx-background-color: #1DB954;" +
            "-fx-text-fill: #000000;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 999;" +
            "-fx-padding: 4 12 4 12;"
        );

        VBox.setMargin(closeBtn, new Insets(4, 0, 0, 0));
        panel.getChildren().addAll(title, empty, closeBtn);

        StackPane.setAlignment(panel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(panel, new Insets(0, 24, 72, 0));
        panel.setOpacity(0);
        panel.setTranslateY(20);

        contentArea.getChildren().add(panel);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), panel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(180), panel);
        slideUp.setFromY(20);
        slideUp.setToY(0);

        ParallelTransition showAnim = new ParallelTransition(fadeIn, slideUp);
        showAnim.play();

        closeBtn.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(160), panel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> contentArea.getChildren().remove(panel));
            fadeOut.play();
        });
    }

    @FXML
    private void showProfileMenu() {
        System.out.println("👤 Show profile menu");

        if (contentArea == null || profileAvatar == null) return;

        VBox menu = new VBox(6);
        menu.setPadding(new Insets(8, 12, 8, 12));
        menu.setStyle(
            "-fx-background-color: rgba(24,24,24,0.98);" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 18, 0, 0, 6);"
        );

        Label name = new Label(username != null ? username : "Guest");
        name.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        Label role = new Label(userRole != null ? userRole.toUpperCase() : "");
        role.setStyle(
            "-fx-text-fill: #1DB954;" +
            "-fx-font-size: 11px;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #333333;");

        Label settingsItem = new Label("Settings");
        settingsItem.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 4 0 4 0;" +
            "-fx-cursor: hand;"
        );
        settingsItem.setOnMouseEntered(e ->
            settingsItem.setStyle(settingsItem.getStyle() + "-fx-text-fill: #1DB954;")
        );
        settingsItem.setOnMouseExited(e ->
            settingsItem.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 4 0 4 0;" +
                "-fx-cursor: hand;"
            )
        );
        settingsItem.setOnMouseClicked(e -> {
            showSettings();
        });

        Label logoutItem = new Label("Sign out");
        logoutItem.setStyle(
            "-fx-text-fill: #FF4D4F;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 4 0 4 0;" +
            "-fx-cursor: hand;"
        );
        logoutItem.setOnMouseClicked(e -> {
            onLogout(); // utilise la méthode déjà définie plus bas dans DashboardController
        });

        menu.getChildren().addAll(name, role, sep, settingsItem, logoutItem);

        StackPane.setAlignment(menu, Pos.TOP_RIGHT);
        StackPane.setMargin(menu, new Insets(72, 24, 0, 0));
        menu.setOpacity(0);
        menu.setTranslateY(-10);

        contentArea.getChildren().add(menu);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(160), menu);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(160), menu);
        slideDown.setFromY(-10);
        slideDown.setToY(0);

        new ParallelTransition(fadeIn, slideDown).play();

        menu.setOnMouseExited(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(140), menu);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> contentArea.getChildren().remove(menu));
            fadeOut.play();
        });
    }



    @FXML
    private void showSettings() {
        System.out.println("⚙️ Show settings");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText(null);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.setStyle(
            "-fx-background-color: #121212;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 12px;"
        );

        Label title = new Label("Dashboard Settings");
        title.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Fine-tune your experience. More options coming soon.");
        subtitle.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 12px;"
        );

        // Placeholder toggle (theme)
        CheckBox darkMode = new CheckBox("Enable ultra dark mode");
        darkMode.setSelected(true);
        darkMode.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 12px;"
        );

        content.getChildren().addAll(title, subtitle, new Separator(), darkMode);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        try {
            dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/css/dashboard.css").toExternalForm());
        } catch (NullPointerException ignored) {}

        dialog.getDialogPane().getStyleClass().add("custom-dialog");
        dialog.showAndWait();
    }


    @FXML
    private void showAllLoans() {
        openBorrow(null);
    }

    @FXML
    private void showAllBooks() {
    	populateAvailableBooks(); 
    }

    /**
     * ═══════════════════════════════════════════════════════════════════
     * 🔎 BROWSE BY CATEGORY – PRO MAX PLUS ULTRA
     * ═══════════════════════════════════════════════════════════════════
     * Filtre dynamiquement la section "Available Books" par catégorie
     * avec animations, badge de contexte et message vide stylé.
     */
    private void filterByCategory(String category) {
        if (availableBooksContainer == null) {
            System.out.println("⚠️ availableBooksContainer is null, cannot filter by category.");
            return;
        }

        System.out.println("🏷️ Applying category filter: " + category);

        // Petite animation de fade-out sur l'ancien contenu
        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), availableBooksContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(ev -> {
            availableBooksContainer.getChildren().clear();

            // Badge de contexte "Browsing: <category>"
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(0, 0, 8, 4));

            Label chipLabel = new Label("Browsing category");
            chipLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #B3B3B3;" +
                "-fx-font-weight: normal;"
            );

            Label chipCategory = new Label(category);
            chipCategory.setStyle(
                "-fx-background-color: rgba(29,185,84,0.18);" +   // #1DB954 with opacity
                "-fx-text-fill: #1DB954;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 999;" +
                "-fx-padding: 3 10 3 10;"
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label clearFilter = new Label("Clear");
            clearFilter.setStyle(
                "-fx-text-fill: #B3B3B3;" +
                "-fx-font-size: 11px;" +
                "-fx-underline: true;" +
                "-fx-cursor: hand;"
            );
            clearFilter.setOnMouseClicked(e -> {
                System.out.println("🧹 Clearing category filter, showing all available books.");
                populateAvailableBooks();   // retour à l'état normal du dashboard
            });

            header.getChildren().addAll(chipLabel, chipCategory, spacer, clearFilter);
            availableBooksContainer.getChildren().add(header);

            // Récupération + filtrage des livres
            List<Livre> filteredBooks = service.getAllLivres().stream()
                .filter(l -> l.getCategorie() != null &&
                             l.getCategorie().equalsIgnoreCase(category))
                .filter(Livre::isDisponible)
                .limit(30)
                .collect(Collectors.toList());

            int delay = 80;
            for (Livre livre : filteredBooks) {
                StackPane card = createBookCard(livre, delay);
                availableBooksContainer.getChildren().add(card);
                delay += 80;
            }

            if (filteredBooks.isEmpty()) {
                // Message vide stylé façon Spotify
                VBox emptyBox = new VBox(8);
                emptyBox.setAlignment(Pos.CENTER_LEFT);
                emptyBox.setPadding(new Insets(12, 0, 0, 4));

                Label title = new Label("No books found in \"" + category + "\"");
                title.setStyle(
                    "-fx-text-fill: #FFFFFF;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
                );

                Label subtitle = new Label("Try another category or clear the filter to explore all available books.");
                subtitle.setStyle(
                    "-fx-text-fill: #B3B3B3;" +
                    "-fx-font-size: 12px;"
                );

                emptyBox.getChildren().addAll(title, subtitle);
                availableBooksContainer.getChildren().add(emptyBox);

                animateFadeInUp(emptyBox, 0);
            }

            // Fade-in du nouveau contenu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), availableBooksContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }


    /**
     * COMPLETE Ultra Premium Book Details Dialog Methods
     * Add ALL these methods to your DashboardController class
     * This completes the missing helper methods
     */

    // ==================== MAIN DIALOG METHOD ====================
    /**
     * ═══════════════════════════════════════════════════════════════════
     * 🎵 SPOTIFY-STYLE BOOK DETAILS MODAL - ULTRA PREMIUM
     * ═══════════════════════════════════════════════════════════════════
     * Replace your existing showBookDetails() method with this
     */

    private void showBookDetails(Livre livre) {
        // Create overlay background (like Spotify's dimmed backdrop)
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        overlay.setPrefSize(contentArea.getWidth(), contentArea.getHeight());
        
        // Main modal container
        VBox modal = new VBox(0);
        modal.setMaxWidth(880);
        modal.setMaxHeight(640);
        modal.setStyle(
            "-fx-background-color: #181818;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 40, 0, 0, 10);"
        );
        
        // ═══════════════════════════════════════════════════════════════
        // HEADER SECTION - Split Layout (Image Left, Info Right)
        // ═══════════════════════════════════════════════════════════════
        HBox header = new HBox(30);
        header.setPadding(new Insets(40, 40, 30, 40));
        header.setAlignment(Pos.TOP_LEFT);
        header.setStyle("-fx-background-color: transparent;");
        
        // LEFT: Book Cover with Vinyl Effect
        StackPane coverContainer = new StackPane();
        coverContainer.setPrefSize(240, 240);
        
        // Shadow layer
        Region coverShadow = new Region();
        coverShadow.setPrefSize(240, 240);
        coverShadow.setStyle(
            "-fx-background-color: rgba(0,0,0,0.4);" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0.3, 0, 8);"
        );
        
        // Book image
        ImageView coverImg = new ImageView();
        coverImg.setFitWidth(232);
        coverImg.setFitHeight(232);
        coverImg.setPreserveRatio(true);
        coverImg.setSmooth(true);
        
        Image image = loadBookImage(livre);
        if (image != null) {
            coverImg.setImage(image);
        }
        
        Rectangle clip = new Rectangle(232, 232);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        coverImg.setClip(clip);
        
        coverContainer.getChildren().addAll(coverShadow, coverImg);
        
        // RIGHT: Book Info
        VBox infoBox = new VBox(16);
        infoBox.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(infoBox, Priority.ALWAYS);
        
        // Category badge
        Label categoryBadge = new Label(livre.getCategorie().toUpperCase());
        categoryBadge.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: #B3B3B3;" +
            "-fx-padding: 4 10;" +
            "-fx-background-radius: 4;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-letter-spacing: 1px;"
        );
        
        // Title
        Label title = new Label(livre.getTitre());
        title.setWrapText(true);
        title.setMaxWidth(500);
        title.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-line-spacing: -5px;"
        );
        
        // Author
        Label author = new Label(livre.getAuteur());
        author.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-weight: 600;"
        );
        
        // Metadata row (ISBN, Year, etc.)
        HBox metadata = new HBox(20);
        metadata.setAlignment(Pos.CENTER_LEFT);
        
        Label isbnLabel = new Label("ISBN: " + livre.getIsbn());
        isbnLabel.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 13px;");
        
        // Availability dot
        Region dot = new Region();
        dot.setPrefSize(6, 6);
        dot.setStyle(
            "-fx-background-color: " + (livre.isDisponible() ? "#1DB954" : "#E74C3C") + ";" +
            "-fx-background-radius: 50%;"
        );
        
        Label availStatus = new Label(livre.isDisponible() ? "Available" : "Borrowed");
        availStatus.setStyle(
            "-fx-text-fill: " + (livre.isDisponible() ? "#1DB954" : "#E74C3C") + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );
        
        metadata.getChildren().addAll(isbnLabel, dot, availStatus);
        
        // Star rating
        HBox stars = createInteractiveStars(livre);
        
        infoBox.getChildren().addAll(categoryBadge, title, author, metadata, stars);
        header.getChildren().addAll(coverContainer, infoBox);
        
        // ═══════════════════════════════════════════════════════════════
        // ACTION BUTTONS (Spotify-style)
        // ═══════════════════════════════════════════════════════════════
        HBox actions = new HBox(12);
        actions.setPadding(new Insets(0, 40, 25, 40));
        actions.setAlignment(Pos.CENTER_LEFT);
        
        if (livre.isDisponible()) {
            Button borrowBtn = new Button("Borrow");
            borrowBtn.setGraphic(createIcon("▶", 16));
            borrowBtn.setStyle(
                "-fx-background-color: #1DB954;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12 32;" +
                "-fx-background-radius: 500;" +
                "-fx-cursor: hand;"
            );
            addSpotifyButtonHover(borrowBtn);
            borrowBtn.setOnAction(e -> {
                showSuccessNotification("📚 " + livre.getTitre() + " borrowed!");
                closeModal(overlay);
            });
            actions.getChildren().add(borrowBtn);
        }
        
        Button detailsBtn = new Button("More Details");
        detailsBtn.setGraphic(createIcon("ⓘ", 14));
        detailsBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #535353;" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 500;" +
            "-fx-background-radius: 500;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;"
        );
        addSpotifyButtonHover(detailsBtn);
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 40px;" +
            "-fx-min-height: 40px;" +
            "-fx-cursor: hand;"
        );
        addSpotifyButtonHover(closeBtn);
        closeBtn.setOnAction(e -> closeModal(overlay));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(detailsBtn, spacer, closeBtn);
        
        // ═══════════════════════════════════════════════════════════════
        // CONTENT TABS (Details, Reviews, Stats)
        // ═══════════════════════════════════════════════════════════════
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 40, 30, 40));
        content.setStyle("-fx-background-color: #121212; -fx-background-radius: 0 0 12 12;");
        
        // Tab buttons
        HBox tabs = new HBox(20);
        tabs.setAlignment(Pos.CENTER_LEFT);
        
        Button[] tabButtons = new Button[3];
        String[] tabNames = {"About", "Statistics", "Reviews"};
        VBox[] tabContents = {
            createAboutTab(livre),
            createStatsTab(livre),
            createReviewsTab(livre)
        };
        
        StackPane tabContentArea = new StackPane();
        tabContentArea.setPrefHeight(200);
        tabContentArea.getChildren().add(tabContents[0]);
        
        for (int i = 0; i < 3; i++) {
            final int index = i;
            Button tab = new Button(tabNames[i]);
            tab.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + (i == 0 ? "white" : "#B3B3B3") + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;" +
                "-fx-border-width: 0 0 2 0;" +
                "-fx-border-color: " + (i == 0 ? "white" : "transparent") + ";"
            );
            
            tab.setOnAction(e -> {
                // Update all tabs
                for (int j = 0; j < tabButtons.length; j++) {
                    tabButtons[j].setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + (j == index ? "white" : "#B3B3B3") + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0 0 2 0;" +
                        "-fx-border-color: " + (j == index ? "white" : "transparent") + ";"
                    );
                }
                
                // Switch content with fade
                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), tabContentArea);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> {
                    tabContentArea.getChildren().setAll(tabContents[index]);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), tabContentArea);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                });
                fadeOut.play();
            });
            
            tabButtons[i] = tab;
            tabs.getChildren().add(tab);
        }
        
        content.getChildren().addAll(tabs, tabContentArea);
        
        // ═══════════════════════════════════════════════════════════════
        // ASSEMBLE MODAL
        // ═══════════════════════════════════════════════════════════════
        modal.getChildren().addAll(header, actions, content);
        overlay.getChildren().add(modal);
        
        // Add to main container
        StackPane root = (StackPane) contentArea.getParent();
        root.getChildren().add(overlay);
        
        // Entrance animation
        overlay.setOpacity(0);
        modal.setScaleX(0.9);
        modal.setScaleY(0.9);
        
        ParallelTransition entrance = new ParallelTransition(
            createFadeTransition(overlay, 0, 1, 250),
            createScaleTransition(modal, 0.9, 1.0, 300)
        );
        entrance.play();
        
        // Close on background click
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                closeModal(overlay);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private VBox createAboutTab(Livre livre) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_LEFT);
        
        Label desc = new Label(
            "\"" + livre.getTitre() + "\" is a captivating work by " + livre.getAuteur() + 
            ". This " + livre.getCategorie() + " masterpiece is available in our collection " +
            "with " + livre.getQuantite() + " copies in stock."
        );
        desc.setWrapText(true);
        desc.setMaxWidth(780);
        desc.setStyle(
            "-fx-text-fill: #B3B3B3;" +
            "-fx-font-size: 14px;" +
            "-fx-line-spacing: 4px;"
        );
        
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(15);
        
        addInfoRow(grid, 0, "📚 Category", livre.getCategorie());
        addInfoRow(grid, 1, "✍️ Author", livre.getAuteur());
        addInfoRow(grid, 2, "🔖 ISBN", livre.getIsbn());
        addInfoRow(grid, 3, "📦 Copies", livre.getQuantite() + " available");
        
        box.getChildren().addAll(desc, grid);
        return box;
    }

    private VBox createStatsTab(Livre livre) {
        VBox box = new VBox(20);
        
        // Progress bar
        VBox progBox = new VBox(8);
        Label progLabel = new Label("Availability: " + livre.getQuantite() + "/10");
        progLabel.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 13px;");
        
        ProgressBar prog = new ProgressBar(livre.getQuantite() / 10.0);
        prog.setPrefWidth(400);
        prog.setPrefHeight(8);
        prog.setStyle("-fx-accent: #1DB954;");
        
        progBox.getChildren().addAll(progLabel, prog);
        
        // Stats grid
        HBox statsRow = new HBox(30);
        statsRow.getChildren().addAll(
            createStatCard("23", "Times Borrowed"),
            createStatCard("4.5", "Avg Rating"),
            createStatCard(String.valueOf(livre.getQuantite()), "In Stock")
        );
        
        box.getChildren().addAll(progBox, statsRow);
        return box;
    }

    private VBox createReviewsTab(Livre livre) {
        VBox box = new VBox(12);
        
        Label noReviews = new Label("No reviews yet. Be the first!");
        noReviews.setStyle(
            "-fx-text-fill: #535353;" +
            "-fx-font-size: 14px;" +
            "-fx-font-style: italic;"
        );
        
        box.getChildren().add(noReviews);
        box.setAlignment(Pos.CENTER);
        box.setPrefHeight(150);
        return box;
    }

    private HBox createInteractiveStars(Livre livre) {
        HBox stars = new HBox(6);
        stars.setAlignment(Pos.CENTER_LEFT);
        
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " + (i < livre.getRating() ? "#1DB954" : "#535353") + ";" +
                "-fx-cursor: hand;"
            );
            
            final int rating = i + 1;
            star.setOnMouseClicked(e -> {
                livre.setRating(rating);
                updateStars(stars, rating);
            });
            
            stars.getChildren().add(star);
        }
        
        return stars;
    }

    private void updateStars(HBox starsBox, int rating) {
        for (int i = 0; i < starsBox.getChildren().size(); i++) {
            Label star = (Label) starsBox.getChildren().get(i);
            star.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " + (i < rating ? "#1DB954" : "#535353") + ";" +
                "-fx-cursor: hand;"
            );
        }
    }

    private VBox createStatCard(String value, String label) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 8;"
        );
        
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 12px;");
        
        card.getChildren().addAll(val, lbl);
        return card;
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #535353; -fx-font-size: 13px;");
        
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600;");
        
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private Label createIcon(String text, int size) {
        Label icon = new Label(text);
        icon.setStyle("-fx-font-size: " + size + "px;");
        return icon;
    }

    private void addSpotifyButtonHover(Button btn) {
        String originalStyle = btn.getStyle();
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(originalStyle + "-fx-scale-x: 1.04; -fx-scale-y: 1.04;");
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(originalStyle);
        });
    }

    private void closeModal(StackPane overlay) {
        Node modal = overlay.getChildren().get(0);
        
        ParallelTransition exit = new ParallelTransition(
            createFadeTransition(overlay, 1, 0, 200),
            createScaleTransition(modal, 1.0, 0.95, 200)
        );
        
        exit.setOnFinished(e -> {
            StackPane root = (StackPane) overlay.getParent();
            root.getChildren().remove(overlay);
        });
        
        exit.play();
    }

    private FadeTransition createFadeTransition(Node node, double from, double to, int duration) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }

    private ScaleTransition createScaleTransition(Node node, double from, double to, int duration) {
        ScaleTransition st = new ScaleTransition(Duration.millis(duration), node);
        st.setFromX(from);
        st.setFromY(from);
        st.setToX(to);
        st.setToY(to);
        return st;
    }

    // ==================== HEADER CREATION ====================
    private VBox createPremiumHeader(Livre livre) {
        // --- CONTENU DU HEADER ---
        VBox headerContent = new VBox(15);
        headerContent.setPadding(new Insets(30, 35, 25, 35));
        headerContent.setStyle(
            "-fx-background-color: rgba(0,0,0,0.45);" // léger overlay sombre
        );

        HBox titleRow = new HBox(20);
        titleRow.setAlignment(Pos.CENTER_LEFT);
 
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(80, 80);
        iconContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );

        Label bookIcon = new Label("📚");
        bookIcon.setStyle("-fx-font-size: 42px;");
        iconContainer.getChildren().add(bookIcon);
        animateIcon(iconContainer);

        VBox titleBox = new VBox(8);

        Label titleLabel = new Label(livre.getTitre());
        titleLabel.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 3, 0, 0, 2);"
        );
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(500);

        Label subtitleLabel = new Label("by " + livre.getAuteur());
        subtitleLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-style: italic;" +
            "-fx-text-fill: rgba(255, 255, 255, 0.9);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);"
        );

        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        titleRow.getChildren().addAll(iconContainer, titleBox);

        HBox statusBadge = createStatusBadge(livre);

        Label categoryTag = new Label("🏷️ " + livre.getCategorie());
        categoryTag.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.25);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 16;" +
            "-fx-background-radius: 20;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );

        headerContent.getChildren().addAll(titleRow, statusBadge, categoryTag);

        // --- IMAGE DE FOND À PARTIR DU LIVRE ---
        Image bgImg = null;
        String path = livre.getImagePath();
        try {
            if (path != null && !path.isBlank()) {
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + path);
                }
                bgImg = new Image(is);
            } else {
                String autoPath = "/images/" + livre.getTitre() + ".jpeg";
                InputStream is = getClass().getResourceAsStream(autoPath);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + autoPath);
                }
                bgImg = new Image(is);
            }
        } catch (NullPointerException ex) {
            InputStream is = getClass().getResourceAsStream("/images/book.jpeg");
            if (is != null) {
                bgImg = new Image(is);
            }
        }

        // --- STACKPANE POUR SUPERPOSER IMAGE + CONTENU ---
        StackPane headerRoot = new StackPane();

        if (bgImg != null) {
            ImageView bgView = new ImageView(bgImg);
            bgView.setPreserveRatio(true);
            bgView.setFitWidth(720);
            bgView.setFitHeight(200);   // un peu plus bas pour limiter le zoom
            bgView.setSmooth(true);

            // Effet léger (moins flou, moins sombre)
            ColorAdjust adjust = new ColorAdjust();
            adjust.setBrightness(-0.25);
            adjust.setSaturation(-0.15);
            GaussianBlur blur = new GaussianBlur(5); // au lieu de 18
            adjust.setInput(blur);
            bgView.setEffect(adjust);

            Rectangle clip = new Rectangle(720, 200);
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            bgView.setClip(clip);

            headerRoot.getChildren().add(bgView);
        }

        headerRoot.getChildren().add(headerContent);
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);

        VBox wrapper = new VBox(headerRoot);
        wrapper.setStyle(
            "-fx-background-radius: 20 20 0 0;" +
            "-fx-border-radius: 20 20 0 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );

        return wrapper;
    }
    


    // ==================== STATUS BADGE ====================
    private HBox createStatusBadge(Livre livre) {
        HBox badge = new HBox(10);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(10, 18, 10, 18));
        
        String bgColor, icon, statusText;
        if (livre.isDisponible()) {
            bgColor = "rgba(76, 175, 80, 0.9)";
            icon = "✓";
            statusText = "Available Now";
        } else {
            bgColor = "rgba(244, 67, 54, 0.9)";
            icon = "✗";
            statusText = "Currently Unavailable";
        }
        
        badge.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label textLabel = new Label(statusText);
        textLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        badge.getChildren().addAll(iconLabel, textLabel);
        
        if (livre.isDisponible()) {
            animateBadgePulse(badge);
        }
        
        return badge;
    }

    // ==================== DETAILS CONTENT ====================
    private VBox createDetailsContent(Livre livre) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // --- Ligne principale: cover à gauche, infos à droite ---
        HBox mainRow = new HBox(25);
        mainRow.setAlignment(Pos.TOP_LEFT);

        // COVER GRANDE, COMME ALBUM SPOTIFY
        StackPane coverWrapper = new StackPane();
        coverWrapper.setPrefSize(220, 220);
        coverWrapper.setStyle(
            "-fx-background-color: linear-gradient(135deg, #282828, #181818);" +
            "-fx-background-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 16, 0.3, 0, 6);"
        );

        ImageView coverView = new ImageView();
        coverView.setFitWidth(200);
        coverView.setFitHeight(200);
        coverView.setPreserveRatio(true);
        coverView.setSmooth(true);

        // Reuse même logique que createBookCard pour charger l'image
        Image image = null;
        String path = livre.getImagePath();
        try {
            if (path != null && !path.isBlank()) {
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + path);
                }
                image = new Image(is);
            } else {
                String autoPath = "images/" + livre.getTitre() + ".jpeg";
                InputStream is = getClass().getResourceAsStream(autoPath);
                if (is == null) {
                    throw new NullPointerException("Image not found: " + autoPath);
                }
                image = new Image(is);
            }
        } catch (NullPointerException ex) {
            InputStream is = getClass().getResourceAsStream("images/book.jpeg");
            if (is != null) {
                image = new Image(is);
            }
        }
        if (image != null) {
            coverView.setImage(image);
        }

        coverWrapper.getChildren().add(coverView);

        // --- Colonne droite: infos détaillées (tes cards existantes) ---
        VBox rightColumn = new VBox(15);

        VBox infoCards = new VBox(15);
        infoCards.getChildren().addAll(
            createInfoCard("🔖", "ISBN", livre.getIsbn(), "#3498db"),
            createInfoCard("👤", "Author", livre.getAuteur(), "#9b59b6"),
            createInfoCard("📚", "Category", livre.getCategorie(), "#e74c3c"),
            createInfoCard("📦", "Quantity", String.valueOf(livre.getQuantite()) + " copies", "#f39c12"),
            createInfoCard("✅", "Status", livre.isDisponible() ? "Available for borrowing" : "Currently borrowed", "#27ae60")
        );

        VBox descriptionBox = new VBox(10);
        descriptionBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);"
        );

        Label descTitle = new Label("Description");
        descTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );

        Label descText = new Label(
            "This book is part of our collection. ISBN " + livre.getIsbn() +
            ". Perfect for readers interested in " + livre.getCategorie() + "."
        );
        descText.setWrapText(true);
        descText.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-line-spacing: 3px;"
        );

        descriptionBox.getChildren().addAll(descTitle, descText);

        rightColumn.getChildren().addAll(infoCards, descriptionBox);

        mainRow.getChildren().addAll(coverWrapper, rightColumn);

        root.getChildren().add(mainRow);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        return root;
    }

    // ==================== INFO CARD CREATION ====================
    private HBox createInfoCard(String icon, String label, String value, String accentColor) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);" +
            "-fx-cursor: hand;"
        );
        
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(45, 45);
        iconContainer.setStyle(
            "-fx-background-color: " + accentColor + ";" +
            "-fx-background-radius: 10;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        iconContainer.getChildren().add(iconLabel);
        
        VBox textBox = new VBox(4);
        
        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #95a5a6;" +
            "-fx-font-weight: 600;"
        );
        
        Label valueText = new Label(value);
        valueText.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-weight: bold;"
        );
        valueText.setWrapText(true);
        valueText.setMaxWidth(500);
        
        textBox.getChildren().addAll(labelText, valueText);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        card.getChildren().addAll(iconContainer, textBox);
        
        addCardHoverEffect(card);
        
        return card;
    }

    // ==================== RATING CONTENT ====================
    private VBox createRatingContent(Livre livre) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30, 35, 30, 35));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #f8f9fa;");
        
        VBox ratingDisplay = new VBox(15);
        ratingDisplay.setAlignment(Pos.CENTER);
        ratingDisplay.setPadding(new Insets(30));
        ratingDisplay.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 5);"
        );
        
        Label ratingTitle = new Label("Rate this book");
        ratingTitle.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        Label ratingSubtitle = new Label("Share your opinion with other readers");
        ratingSubtitle.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7f8c8d;"
        );
        
        HBox starsBox = createEnhancedStarRating(livre);
        
        HBox avgRatingBox = new HBox(10);
        avgRatingBox.setAlignment(Pos.CENTER);
        avgRatingBox.setPadding(new Insets(20, 0, 0, 0));
        
        Label avgLabel = new Label("Average Rating:");
        avgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        Label avgValue = new Label("4.5 / 5.0");
        avgValue.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #f39c12;"
        );
        
        avgRatingBox.getChildren().addAll(avgLabel, avgValue);
        
        ratingDisplay.getChildren().addAll(ratingTitle, ratingSubtitle, starsBox, avgRatingBox);
        
        VBox reviewsBox = new VBox(10);
        reviewsBox.setPadding(new Insets(20));
        reviewsBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);"
        );
        
        Label reviewsTitle = new Label("💬 Recent Reviews");
        reviewsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label noReviews = new Label("No reviews yet. Be the first to review this book!");
        noReviews.setStyle("-fx-font-size: 13px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
        
        reviewsBox.getChildren().addAll(reviewsTitle, noReviews);
        
        content.getChildren().addAll(ratingDisplay, reviewsBox);
        
        return content;
    }

    // ==================== ENHANCED STAR RATING ====================
    private HBox createEnhancedStarRating(Livre livre) {
        HBox starsContainer = new HBox(8);
        starsContainer.setAlignment(Pos.CENTER);
        
        Label[] stars = new Label[5];
        int[] currentRating = {0};
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            Label star = new Label("★");
            star.setStyle(
                "-fx-font-size: 42px;" +
                "-fx-text-fill: #e0e0e0;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
            );
            
            stars[i] = star;
            
            star.setOnMouseEntered(e -> {
                for (int j = 0; j <= index; j++) {
                    stars[j].setStyle(
                        "-fx-font-size: 48px;" +
                        "-fx-text-fill: #ffd700;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 12, 0.8, 0, 0);"
                    );
                    
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), stars[j]);
                    st.setToX(1.15);
                    st.setToY(1.15);
                    st.play();
                }
            });
            
            star.setOnMouseExited(e -> {
                for (int j = 0; j < 5; j++) {
                    String color = j < currentRating[0] ? "#ffd700" : "#e0e0e0";
                    stars[j].setStyle(
                        "-fx-font-size: 42px;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
                    );
                    
                    ScaleTransition st = new ScaleTransition(Duration.millis(150), stars[j]);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                }
            });
            
            star.setOnMouseClicked(e -> {
                currentRating[0] = index + 1;
                showRatingConfirmation(currentRating[0], livre);
            });
            
            starsContainer.getChildren().add(star);
        }
        
        return starsContainer;
    }

    // ==================== AVAILABILITY CONTENT ====================
    private VBox createAvailabilityContent(Livre livre) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 35, 30, 35));
        content.setStyle("-fx-background-color: #f8f9fa;");
        
        VBox chartBox = new VBox(15);
        chartBox.setPadding(new Insets(25));
        chartBox.setAlignment(Pos.CENTER);
        chartBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 5);"
        );
        
        Label chartTitle = new Label("📊 Stock Status");
        chartTitle.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        VBox progressBox = new VBox(10);
        progressBox.setPrefWidth(500);
        
        Label qtyLabel = new Label("Available Copies: " + livre.getQuantite() + " / 10");
        qtyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        ProgressBar progressBar = new ProgressBar(livre.getQuantite() / 10.0);
        progressBar.setPrefWidth(500);
        progressBar.setPrefHeight(20);
        progressBar.setStyle(
            "-fx-accent: " + (livre.getQuantite() > 5 ? "#27ae60" : "#e74c3c") + ";"
        );
        
        progressBox.getChildren().addAll(qtyLabel, progressBar);
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(20, 0, 0, 0));
        
        statsGrid.add(createStatBox("📚", "Total Copies", "10"), 0, 0);
        statsGrid.add(createStatBox("✅", "Available", String.valueOf(livre.getQuantite())), 1, 0);
        statsGrid.add(createStatBox("📖", "Borrowed", String.valueOf(10 - livre.getQuantite())), 2, 0);
        
        chartBox.getChildren().addAll(chartTitle, progressBox, statsGrid);
        
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(20));
        historyBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);"
        );
        
        Label historyTitle = new Label("📅 Borrowing History");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label historyText = new Label("This book has been borrowed 23 times this month.");
        historyText.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        
        historyBox.getChildren().addAll(historyTitle, historyText);
        
        content.getChildren().addAll(chartBox, historyBox);
        
        return content;
    }

    // ==================== STAT BOX CREATION ====================
    private VBox createStatBox(String icon, String label, String value) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(150);
        box.setPadding(new Insets(15));
        box.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #7f8c8d;"
        );
        
        box.getChildren().addAll(iconLabel, valueLabel, labelText);
        return box;
    }

    // ==================== ACTION BUTTONS ====================
    private HBox createActionButtons(Dialog<Void> dialog, Livre livre) {
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(20, 30, 25, 30));
        buttons.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1 0 0 0;"
        );
        
        if (livre.isDisponible()) {
            Button borrowBtn = new Button("📖 Borrow Book");
            borrowBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12 28;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(76,175,80,0.4), 8, 0, 0, 3);"
            );
            
            addButtonHoverEffect(borrowBtn, "#4CAF50", "#45a049");
            
            borrowBtn.setOnAction(e -> {
                showSuccessNotification("Book borrowed successfully!");
                dialog.close();
            });
            
            buttons.getChildren().add(borrowBtn);
        }
        
        Button closeBtn = new Button("✕ Close");
        closeBtn.setStyle(
            "-fx-background-color: #e0e0e0;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 28;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        addButtonHoverEffect(closeBtn, "#e0e0e0", "#d0d0d0");
        addCardHoverEffect(closeBtn);
        closeBtn.setOnAction(e -> dialog.close());
        
        buttons.getChildren().add(closeBtn);
        
        return buttons;
    }

    // ==================== MISSING HELPER METHODS (COMPLETED) ====================

    private void showRatingConfirmation(int rating, Livre livre) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✨ Rating Submitted");
        alert.setHeaderText(null);
        
        // Custom content
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        
        Label thankYou = new Label("Thank you for your rating!");
        thankYou.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2c3e50;"
        );
        
        // Display stars
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-text-fill: " + (i < rating ? "#ffd700" : "#e0e0e0") + ";"
            );
            starsBox.getChildren().add(star);
        }
        
        Label message = new Label("You rated \"" + livre.getTitre() + "\" " + rating + " out of 5 stars!");
        message.setWrapText(true);
        message.setMaxWidth(350);
        message.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #7f8c8d;" +
            "-fx-text-alignment: center;"
        );
        
        content.getChildren().addAll(thankYou, starsBox, message);
        alert.getDialogPane().setContent(content);
        
        // Style the alert
        alert.getDialogPane().setStyle(
            "-fx-background-color: white;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;"
        );
        
        // Animate
        alert.getDialogPane().setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), alert.getDialogPane());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        alert.showAndWait();
    }

    private void addButtonHoverEffect(Button btn, String normalColor, String hoverColor) {
        String originalStyle = btn.getStyle();
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(originalStyle.replace(normalColor, hoverColor));
            
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(originalStyle);
            
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void showSuccessNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ Success");
        alert.setHeaderText(null);

        // Root content
        VBox content = new VBox(18);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(26));
        content.setStyle(
            "-fx-background-color: linear-gradient(to bottom right,#22c55e,#16a34a);" +
            "-fx-background-radius: 18;"
        );

        // Icon
        Label icon = new Label("✓");
        icon.setStyle(
            "-fx-font-size: 60px;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.40), 18, 0, 0, 6);"
        );

        Label title = new Label("Action completed");
        title.setStyle(
            "-fx-text-fill: rgba(255,255,255,0.90);" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 700;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(360);
        messageLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: rgba(255,255,255,0.92);" +
            "-fx-text-alignment: center;"
        );

        content.getChildren().addAll(icon, title, messageLabel);

        DialogPane pane = alert.getDialogPane();
        pane.setContent(content);
        applyPremiumDialogStyle(pane);
        playEntranceAnimation(pane);
        animateIcon(icon);
        animateBadgePulse(title);

        alert.show();
    }
    private void applyPremiumDialogStyle(DialogPane pane) {
        pane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 0;" +
            "-fx-border-width: 0;"
        );

        // Remove default buttons’ strong background (Spotify‑like)
        pane.getButtonTypes().forEach(type -> {
            Button btn = (Button) pane.lookupButton(type);
            if (btn != null) {
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #e5e7eb;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-radius: 999;"
                );
                addCardHoverEffect(btn);
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────
    // Shared styling helpers
    // ────────────────────────────────────────────────────────────────────

 // Replace the old version with this:

    private void applyPremiumDialogStyle(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();

        pane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 0;" +
            "-fx-border-width: 0;"
        );

        pane.getButtonTypes().forEach(type -> {
            Button btn = (Button) pane.lookupButton(type);
            if (btn != null) {
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #e5e7eb;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: 600;" +
                    "-fx-background-radius: 999;"
                );
                addCardHoverEffect(btn);
            }
        });
    }


    private void playEntranceAnimation(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.92);
        node.setScaleY(0.92);
        node.setTranslateY(-10);

        FadeTransition fade = new FadeTransition(Duration.millis(260), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(260), node);
        scale.setFromX(0.92);
        scale.setFromY(0.92);
        scale.setToX(1.0);
        scale.setToY(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(260), node);
        slide.setFromY(-10);
        slide.setToY(0);

        new ParallelTransition(fade, scale, slide).play();
    }

    private void animateIcon(Node icon) {
        RotateTransition rotate = new RotateTransition(Duration.millis(520), icon);
        rotate.setFromAngle(-180);
        rotate.setToAngle(0);
        rotate.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scale = new ScaleTransition(Duration.millis(520), icon);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(rotate, scale).play();
    }

    private void animateBadgePulse(Node badge) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(420), badge);
        pulse.setFromX(0.96);
        pulse.setFromY(0.96);
        pulse.setToX(1.03);
        pulse.setToY(1.03);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }

    private void addCardHoverEffect(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(160), node);
            scale.setToX(1.04);
            scale.setToY(1.04);

            DropShadow glow = new DropShadow();
            glow.setRadius(14);
            glow.setSpread(0.35);
            glow.setColor(Color.web("#22c55e", 0.55));
            node.setEffect(glow);

            scale.play();
        });

        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(160), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            node.setEffect(null);
            scale.play();
        });
    }

    // Méthode utilitaire pour créer une ligne d'info
    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelText = new Label(label);
        labelText.setPrefWidth(150);
        labelText.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13;");
        valueText.setWrapText(true);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    // 👇 NOUVELLE MÉTHODE : créer les étoiles de notation
    private HBox createStarRating(Livre livre) {
        HBox starsBox = new HBox(8);
        starsBox.setAlignment(Pos.CENTER_LEFT);

        // Récupère la note actuelle (0 par défaut si elle n'existe pas)
        double currentRating = livre.getRating() != 0 ? livre.getRating() : 0;

        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            Label star = new Label(currentRating >= i ? "★" : "☆");
            star.setStyle("-fx-font-size: 28; -fx-text-fill: #f39c12; -fx-cursor: hand;");

            int index = i;
            star.setOnMouseEntered(e -> {
                // Au survol, affiche les étoiles pleines jusqu'à celle-ci
                for (int j = 0; j < starsBox.getChildren().size(); j++) {
                    Label s = (Label) starsBox.getChildren().get(j);
                    if (j < index) {
                        s.setText("★");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #f39c12; -fx-cursor: hand;");
                    } else {
                        s.setText("☆");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #bdc3c7; -fx-cursor: hand;");
                    }
                }
            });

            star.setOnMouseExited(e -> {
                // Au départ du survol, remet à jour selon la note actuelle
                double rating = livre.getRating();
                for (int j = 0; j < starsBox.getChildren().size(); j++) {
                    Label s = (Label) starsBox.getChildren().get(j);
                    if (j < rating) {
                        s.setText("★");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #f39c12; -fx-cursor: hand;");
                    } else {
                        s.setText("☆");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #bdc3c7; -fx-cursor: hand;");
                    }
                }
            });

            star.setOnMouseClicked(e -> {
                // Au clic, enregistre la note
                livre.setRating(starValue);

                // Met à jour l'affichage des étoiles
                for (int j = 0; j < starsBox.getChildren().size(); j++) {
                    Label s = (Label) starsBox.getChildren().get(j);
                    if (j < starValue) {
                        s.setText("★");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #f39c12; -fx-cursor: hand;");
                    } else {
                        s.setText("☆");
                        s.setStyle("-fx-font-size: 28; -fx-text-fill: #bdc3c7; -fx-cursor: hand;");
                    }
                }

                System.out.println("Notation de '" + livre.getTitre() + "' : " + starValue + " étoiles");
            });

            starsBox.getChildren().add(star);
        }

        return starsBox;
    }

    // Méthode utilitaire pour créer une ligne d'info



    // ═══════════════════════════════════════════════════════════════════
    // LOGOUT
    // ═══════════════════════════════════════════════════════════════════
    
    @FXML
    private void onLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("👋 Are you sure you want to logout?");
        confirm.setContentText("User: " + username);
        
        // Customize alert
        DialogPane dialogPane = confirm.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performLogoutWithAnimation();
            }
        });
    }

    private void performLogoutWithAnimation() {
        // Fade out entire scene
        Scene currentScene = welcomeLabel.getScene();
        FadeTransition fade = new FadeTransition(Duration.millis(500), currentScene.getRoot());
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            sessionUsername = null;
            sessionRole = null;
            performLogout();
        });
        fade.play();
    }

    private void performLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            
            // Fade in new scene
            scene.getRoot().setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(500), scene.getRoot());
            fade.setToValue(1);
            
            stage.setScene(scene);
            stage.setTitle("GHARBI'S LIBRARY - Login");
            stage.centerOnScreen();
            
            fade.play();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANIMATION UTILITIES - PRO MAX ULTRA
    // ═══════════════════════════════════════════════════════════════════
    
    private void animateLabelWithGlow(Label label) {
        ParallelTransition parallel = new ParallelTransition();
        
        FadeTransition fade = new FadeTransition(Duration.millis(800), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), label);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        // Add glow
        Glow glow = new Glow(0.5);
        label.setEffect(glow);
        
        Timeline glowTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.5)),
            new KeyFrame(Duration.millis(800), new KeyValue(glow.levelProperty(), 0.0))
        );
        
        parallel.getChildren().addAll(fade, scale);
        parallel.play();
        glowTimeline.play();
    }

    private void animateFadeIn(Node node, int delay) {
        node.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delay));
        fade.play();
    }

    private void animateFadeInUp(Node node, int delay) {
        node.setOpacity(0);
        node.setTranslateY(20);
        
        ParallelTransition parallel = new ParallelTransition();
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(500), node);
        translate.setFromY(20);
        translate.setToY(0);
        
        parallel.getChildren().addAll(fade, translate);
        parallel.setDelay(Duration.millis(delay));
        parallel.play();
    }

    private void animateSlideIn(Node node) {
        node.setOpacity(0);
        node.setTranslateX(50);
        
        ParallelTransition parallel = new ParallelTransition();
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), node);
        slide.setFromX(50);
        slide.setToX(0);
        
        parallel.getChildren().addAll(fade, slide);
        parallel.play();
    }

    private void animateDashboardEntrance() {
        Node[] nodes = {quickAccessGrid, recentBooksContainer, categoriesGrid, availableBooksContainer};
        
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                animateFadeInUp(nodes[i], i * 150);
            }
        }
    }

    private void setupAnimations() {
        // Setup continuous ambient animations
        if (volumeSlider != null) {
            volumeSlider.setValue(75);
        }
    }

    private void setupAmbientEffects() {
        // Subtle ambient glow animation
        ambientAnimation = new Timeline(
            new KeyFrame(Duration.ZERO),
            new KeyFrame(Duration.seconds(3))
        );
        ambientAnimation.setCycleCount(Timeline.INDEFINITE);
        ambientAnimation.setAutoReverse(true);
        // ambientAnimation.play(); // Uncomment if needed
    }

    // ═══════════════════════════════════════════════════════════════════
    // RIPPLE EFFECT
    // ═══════════════════════════════════════════════════════════════════
    
    private void createRippleEffect(Region region, double x, double y, String color) {
        javafx.scene.shape.Circle ripple = new javafx.scene.shape.Circle(x, y, 0);
        ripple.setFill(Color.TRANSPARENT);
        ripple.setStroke(Color.web(color));
        ripple.setStrokeWidth(2);
        
        Pane parent = (Pane) region;
        parent.getChildren().add(ripple);
        
        ParallelTransition parallel = new ParallelTransition();
        
        // Expand ripple
        Timeline expand = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(ripple.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(500), new KeyValue(ripple.radiusProperty(), 100))
        );
        
        // Fade out
        FadeTransition fade = new FadeTransition(Duration.millis(500), ripple);
        fade.setFromValue(0.5);
        fade.setToValue(0);
        fade.setOnFinished(e -> parent.getChildren().remove(ripple));
        
        parallel.getChildren().addAll(expand, fade);
        parallel.play();
    }

    // ═══════════════════════════════════════════════════════════════════
    // COLOR UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    private String adjustColorBrightness(String hexColor, double factor) {
        try {
            Color color = Color.web(hexColor);
            double r = Math.max(0, Math.min(1, color.getRed() + factor));
            double g = Math.max(0, Math.min(1, color.getGreen() + factor));
            double b = Math.max(0, Math.min(1, color.getBlue() + factor));
            
            return String.format("#%02X%02X%02X",
                (int)(r * 255),
                (int)(g * 255),
                (int)(b * 255)
            );
        } catch (Exception e) {
            return hexColor;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // NOTIFICATION UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * ═══════════════════════════════════════════════════════════════════
     * 🔥 TOAST-STYLE ERROR NOTIFICATION – PRO MAX
     * ═══════════════════════════════════════════════════════════════════
     */
    private void showErrorNotification(String message) {
        System.err.println("❌ " + message);

        if (contentArea == null) {
            return;
        }

        StackPane root = contentArea;
        Label toast = new Label(message);
        toast.setWrapText(true);
        toast.setMaxWidth(320);
        toast.setStyle(
            "-fx-background-color: rgba(255, 69, 58, 0.95);" +  // iOS-style red
            "-fx-text-fill: white;" +
            "-fx-padding: 10 16 10 16;" +
            "-fx-background-radius: 999;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0, 0, 6);"
        );

        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 24, 24, 0));

        toast.setOpacity(0);
        toast.setTranslateY(20);

        root.getChildren().add(toast);

        // Fade + slide in
        Timeline showTl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(toast.opacityProperty(), 0),
                new KeyValue(toast.translateYProperty(), 20)
            ),
            new KeyFrame(Duration.millis(220),
                new KeyValue(toast.opacityProperty(), 1),
                new KeyValue(toast.translateYProperty(), 0)
            )
        );

        // Stay visible a bit
        PauseTransition stay = new PauseTransition(Duration.millis(2000));

        // Fade out
        Timeline hideTl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(toast.opacityProperty(), 1),
                new KeyValue(toast.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(220),
                new KeyValue(toast.opacityProperty(), 0),
                new KeyValue(toast.translateYProperty(), 10)
            )
        );
        hideTl.setOnFinished(e -> root.getChildren().remove(toast));

        SequentialTransition seq = new SequentialTransition(showTl, stay, hideTl);
        seq.play();
    }


    // ═══════════════════════════════════════════════════════════════════
    // STATIC UTILITY METHOD
    // ═══════════════════════════════════════════════════════════════════
    
    public static void returnToDashboard(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(DashboardController.class.getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(DashboardController.class.getResource("/css/dashboard.css").toExternalForm());
            
            DashboardController controller = loader.getController();
            if (sessionUsername != null && sessionRole != null) {
                controller.setUserInfo(sessionUsername, sessionRole);
            }
            
            // Fade in animation
            scene.getRoot().setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(500), scene.getRoot());
            fade.setToValue(1);
            
            stage.setScene(scene);
            stage.setTitle("GHARBI'S LIBRARY - Dashboard");
            stage.setMaximized(true);
            stage.centerOnScreen();
            
            fade.play();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
