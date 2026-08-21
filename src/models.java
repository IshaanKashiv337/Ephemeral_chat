import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class Models {

    @Entity
    @Table(name = "users")
    public static class User {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        @Column(unique = true, nullable = false)
        private String email;
        
        @Column(nullable = false)
        private String password;
        
        private String name;
        
        @Column(name = "phone_no", nullable = false)
        private Long phoneNo; // Integer form[cite: 1]
        
        private String state;
        private String city;
        
        @Column(nullable = false)
        private Integer pincode; // Integer form[cite: 1]
        
        @Column(name = "street_no")
        private String streetNo;
        
        @Column(name = "building_no")
        private String buildingNo;

        // Getters and Setters omitted for brevity
    }

    @Entity
    @Table(name = "communities")
    public static class Community {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        @ManyToOne
        @JoinColumn(name = "creator_id", nullable = false)
        private User creator;
        
        @Column(nullable = false)
        private String name; // Mandatory[cite: 1]
        
        @Column(nullable = false)
        private String city; // Mandatory[cite: 1]
        
        private String locality;
        
        @Column(nullable = false)
        private Integer pincode; // Mandatory[cite: 1]
        
        private String description;
        
        @Column(name = "is_protected")
        private Boolean isProtected = false; // Protected state[cite: 1]

        @ManyToMany
        @JoinTable(
            name = "community_members",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
        )
        private List<User> members;

        // Getters and Setters omitted
    }

    @Entity
    @Table(name = "books")
    public static class Book {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        @ManyToOne
        @JoinColumn(name = "owner_id", nullable = false)
        private User owner;
        
        @Column(nullable = false)
        private String name; // Mandatory[cite: 1]
        
        @Column(nullable = false)
        private String author; // Mandatory[cite: 1]
        
        @Column(nullable = false)
        private Integer edition; // Mandatory[cite: 1]
        
        private String genre;
        private String description;
        
        @Column(name = "image_path")
        private String imagePath;
        
        private String language;
        
        @Column(name = "condition_desc")
        private String conditionDesc;
        
        @Column(name = "times_lent")
        private Integer timesLent = 0;
        
        @Column(name = "is_available")
        private Boolean isAvailable = true; // Available toggle[cite: 1]

        // Getters and Setters omitted
    }

    @Entity
    @Table(name = "requests")
    public static class Request {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        @ManyToOne
        @JoinColumn(name = "book_id", nullable = false)
        private Book book;
        
        @ManyToOne
        @JoinColumn(name = "borrower_id", nullable = false)
        private User borrower;
        
        @ManyToOne
        @JoinColumn(name = "owner_id", nullable = false)
        private User owner;
        
        private String status = "PENDING"; // State management for transaction[cite: 1]
        
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_at")
        private Date createdAt = new Date();
        
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "exchange_deadline")
        private Date exchangeDeadline; // For the 48 hr window[cite: 1]
        
        @Column(name = "borrower_acknowledged")
        private Boolean borrowerAcknowledged = false;
        
        @Column(name = "owner_acknowledged")
        private Boolean ownerAcknowledged = false;

        // Getters and Setters omitted
    }

    @Entity
    @Table(name = "notifications")
    public static class Notification {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        
        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Column(nullable = false)
        private String message;
        
        @Column(name = "is_read")
        private Boolean isRead = false;
        
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_at")
        private Date createdAt = new Date();

        // Getters and Setters omitted
    }
}