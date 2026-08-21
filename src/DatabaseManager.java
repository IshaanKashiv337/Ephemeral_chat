import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class DatabaseManager {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("BookExchangePU");

    /**
     * Authenticates a user based on email and password.
     * If entered wrong password and if email is already present in database, return null or throw exception[cite: 1].
     */
    public static Models.User loginUser(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Models.User> query = em.createQuery(
                "SELECT u FROM Models$User u WHERE u.email = :email", Models.User.class);
            query.setParameter("email", email);
            
            List<Models.User> users = query.getResultList();
            if (users.isEmpty()) {
                return null; // Email not present in the database, redirect to sign-up page logic[cite: 1]
            }
            
            Models.User user = users.get(0);
            if (user.getPassword().equals(password)) {
                return user; // Password matches, redirect to home page[cite: 1]
            }
            return null; // Wrong password[cite: 1]
        } finally {
            em.close();
        }
    }

    /**
     * Creates a new community.
     * Filling at least the Name, city and pincode of the community is mandatory[cite: 1].
     */
    public static void createCommunity(Models.User creator, String name, String city, Integer pincode, String desc, boolean isProtected) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Models.Community community = new Models.Community();
            community.setCreator(creator);
            community.setName(name);
            community.setCity(city);
            community.setPincode(pincode);
            community.setDescription(desc);
            community.setIsProtected(isProtected); // Protected state logic[cite: 1]
            
            em.persist(community);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Adds a new book to the database.
     * It is mandatory to fill name, author, and edition fields[cite: 1].
     */
    public static void addBook(Models.User owner, String name, String author, Integer edition, String genre) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Models.Book book = new Models.Book();
            book.setOwner(owner);
            book.setName(name);
            book.setAuthor(author);
            book.setEdition(edition);
            book.setGenre(genre);
            book.setIsAvailable(true); // Default to available[cite: 1]
            
            em.persist(book);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Processes a book request acceptance.
     * If the requested is accepted, the book’s status changes to unavailable[cite: 1].
     */
    public static void acceptRequest(Integer requestId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Models.Request request = em.find(Models.Request.class, requestId);
            if (request != null) {
                request.setStatus("ACCEPTED");
                
                // Change book status to unavailable[cite: 1]
                Models.Book book = request.getBook();
                book.setIsAvailable(false);
                em.merge(book);
                em.merge(request);
            }
            
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Fetches the linear request history for a specific user.
     * In request history page, there will be a collection all the requests the user received[cite: 1].
     */
    public static List<Models.Request> getRequestHistory(Integer userId, boolean sortAscending) {
        EntityManager em = emf.createEntityManager();
        try {
            String order = sortAscending ? "ASC" : "DESC"; // Can decide if to sort the requests by ascending order or descending order[cite: 1]
            TypedQuery<Models.Request> query = em.createQuery(
                "SELECT r FROM Models$Request r WHERE r.owner.id = :userId ORDER BY r.createdAt " + order, 
                Models.Request.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}