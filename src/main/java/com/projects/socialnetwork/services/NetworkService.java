package com.projects.socialnetwork.services;

import com.projects.socialnetwork.DTO.UserFriendDTO;
import com.projects.socialnetwork.ENUM.FriendshipRequestStatus;
import com.projects.socialnetwork.exceptions.FriendshipConflictException;
import com.projects.socialnetwork.exceptions.UserConflictException;
import com.projects.socialnetwork.models.Friendship;
import com.projects.socialnetwork.models.Message;
import com.projects.socialnetwork.models.User;
import com.projects.socialnetwork.DTO.UserFriendshipDTO;
import com.projects.socialnetwork.repositories.databaseRepository.FriendshipDBRepository;
import com.projects.socialnetwork.repositories.databaseRepository.MessageDBRepository;
import com.projects.socialnetwork.repositories.databaseRepository.UserDBRepository;
import com.projects.socialnetwork.repositories.paging.Page;
import com.projects.socialnetwork.repositories.paging.Pageable;
import com.projects.socialnetwork.repositories.pagingRepository.FriendshipDBPagingRepository;
import com.projects.socialnetwork.repositories.pagingRepository.UserDBPagingRepository;
import com.projects.socialnetwork.utils.observers.Observable;
import com.projects.socialnetwork.utils.observers.Observer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NetworkService implements Service, Observable {

    private List<Observer> observers = new ArrayList<>();
    private UserDBRepository userRepository;

    private UserDBPagingRepository userDBPagingRepository;

    private FriendshipDBPagingRepository friendshipDBPagingRepository;

    private FriendshipDBRepository friendshipRepository;

    private MessageDBRepository messageDBRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public NetworkService(UserDBRepository userRepository, FriendshipDBRepository friendshipRepository, MessageDBRepository messageDBRepository, UserDBPagingRepository userDBPagingRepository, FriendshipDBPagingRepository friendshipDBPagingRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageDBRepository = messageDBRepository;
        this.userDBPagingRepository = userDBPagingRepository;
        this.friendshipDBPagingRepository = friendshipDBPagingRepository;
    }

    @Override
    public Optional<User> addUser(String firstName, String lastName, String username, String email, String password) {
        User user = new User(firstName, lastName, username, email, password);
        if (alreadyExists(user))
            throw new UserConflictException("There is already a user with this username/password");

        return userRepository.save(user);
    }

    /**
     * Checks if a user with the same username/email exists
     *
     * @return true if a user with these credentials exists, false otherwise
     */
    private boolean alreadyExists(User user) {
        return userRepository.getUserByEmail(user.getEmail()).isPresent() || userRepository.getUserByUsername(user.getUsername()).isPresent();
    }

    @Override
    public Optional<User> deleteUserByUsername(String username) {
        if (username == null)
            throw new IllegalArgumentException("The username cannot be null");

        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new UserConflictException("The user does not exist"));

        return userRepository.delete(user.getId());
    }

    @Override
    public Optional<User> deleteUserById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("The id cannot be null");
        User user = userRepository.delete(id).orElseThrow(() -> new UserConflictException("The user does not exist"));
        return Optional.of(user);
    }

    @Override
    public void sendFriendRequest(UUID senderID, UUID receiverID) {
        User sender = userRepository.getById(senderID).orElseThrow(() -> new UserConflictException("The user does not exist"));
        User receiver = userRepository.getById(receiverID).orElseThrow(() -> new UserConflictException("The user does not exist"));

        if (existsFriendship(sender, receiver))
            throw new FriendshipConflictException("The friendship already exists");

        Friendship friendship = new Friendship(sender, receiver, LocalDateTime.now(), FriendshipRequestStatus.PENDING);
        friendshipRepository.save(friendship);
        notifyObservers();
    }

    @Override
    public void acceptFriendRequest(UUID senderID, UUID receiverID) {
        User sender = userRepository.getById(senderID).orElseThrow(() -> new UserConflictException("The user does not exist"));
        User receiver = userRepository.getById(receiverID).orElseThrow(() -> new UserConflictException("The user does not exist"));

        if (!existsFriendship(sender, receiver))
            throw new FriendshipConflictException("The friendship does not exist");

        Friendship friendship = StreamSupport.stream(friendshipRepository.getAll().spliterator(), false).filter(friendship1 ->
                (friendship1.getUser1().equals(sender) && friendship1.getUser2().equals(receiver)) ||
                        (friendship1.getUser1().equals(receiver) && friendship1.getUser2().equals(sender))
        ).findFirst().orElseThrow();

        friendship.setFriendshipStatus(FriendshipRequestStatus.ACCEPTED);
        friendshipRepository.update(friendship);
        notifyObservers();
    }

    @Override
    public void declineFriendRequest(UUID senderID, UUID receiverID) {
        User sender = userRepository.getById(senderID).orElseThrow(() -> new UserConflictException("The user does not exist"));
        User receiver = userRepository.getById(receiverID).orElseThrow(() -> new UserConflictException("The user does not exist"));

        if (!existsFriendship(sender, receiver))
            throw new FriendshipConflictException("The friendship does not exist");

        Friendship friendship = StreamSupport.stream(friendshipRepository.getAll().spliterator(), false).filter(friendship1 ->
                (friendship1.getUser1().equals(sender) && friendship1.getUser2().equals(receiver)) ||
                        (friendship1.getUser1().equals(receiver) && friendship1.getUser2().equals(sender))
        ).findFirst().orElseThrow();

        friendship.setFriendshipStatus(FriendshipRequestStatus.REJECTED);

        friendshipRepository.delete(friendship.getId());
        notifyObservers();
    }


    /**
     * Checks if a friendship between two users exists
     *
     * @param user1 - first user
     * @param user2 - second user
     * @return true if the friendship exists, false otherwise
     */
    private boolean existsFriendship(User user1, User user2) {
        Iterable<Friendship> friendships = friendshipRepository.getAll();
        return StreamSupport.stream(friendships.spliterator(), false).anyMatch(x ->
                (x.getUser1().equals(user1) && x.getUser2().equals(user2)) ||
                        (x.getUser1().equals(user2) && x.getUser2().equals(user1))
        );


    }

    @Override
    public void deleteFriendship(UUID user1ID, UUID user2ID) {

        User user1 = userRepository.getById(user1ID).orElseThrow(() -> new UserConflictException("The user does not exist"));
        User user2 = userRepository.getById(user2ID).orElseThrow(() -> new UserConflictException("The user does not exist"));
        if (!existsFriendship(user1, user2))
            throw new FriendshipConflictException("The friendship does not exist");

        Friendship friendshipToDelete = StreamSupport.stream(friendshipRepository.getAll().spliterator(), false).filter(friendship ->
                (friendship.getUser1().equals(user1) && friendship.getUser2().equals(user2)) ||
                        (friendship.getUser1().equals(user2) && friendship.getUser2().equals(user1))
        ).findFirst().orElseThrow();

        user2.removeFriend(user1);
        user1.removeFriend(user2);
        friendshipRepository.delete(friendshipToDelete.getId());
        notifyObservers();
    }


    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.getAll();
    }

    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepository.getAll();
    }

    private void dfs(User user, Set<User> visited, Set<User> community) {
        visited.add(user);
        community.add(user);

        for (User friend : user.getFriends()) {
            if (!visited.contains(friend)) {
                dfs(friend, visited, community);
            }
        }
    }

    @Override
    public int getNumberOfCommunities() {
        AtomicInteger numberOfCommunities = new AtomicInteger();
        Set<User> visited = new HashSet<>();

        StreamSupport.stream(userRepository.getAll().spliterator(),false)
                .filter(user -> !visited.contains(user))
                .forEach(user -> {
                    numberOfCommunities.getAndIncrement();
                    Set<User> community = new HashSet<>();
                    dfs(user, visited, community);
                });

        return numberOfCommunities.get();
    }

    @Override
    public Iterable<Iterable<User>> getAllCommunities() {
        Set<Iterable<User>> allCommunities = new HashSet<>();
        Set<User> visited = new HashSet<>();

        StreamSupport.stream(userRepository.getAll().spliterator(), false)
                .filter(user -> !visited.contains(user))
                .forEach(user -> {
                    Set<User> community = new HashSet<>();
                    dfs(user, visited, community);
                    allCommunities.add(community);
                });


        return allCommunities;
    }

    @Override
    public Iterable<User> getMostSociableCommunity() {
        return StreamSupport.stream(getAllCommunities().spliterator(),false)
                .max(Comparator.comparingInt(community -> StreamSupport.stream(community.spliterator(), false)
                        .mapToInt(user -> user.getFriends().size())
                        .sum()))
                .orElse(null);
    }

//    @Override
//    public Iterable<UserFriendshipDTO> friendshipsOfUserOnMonth(String username, Month month) {
//        User user = getUserByUsername(username).get();
//
//        Set<UserFriendshipDTO> userFriendshipDTOS = new HashSet<>();
//        for (Friendship f : friendshipRepository.getAll()) {
//            if (f.getFriendsSince().getMonth().equals(month)) {
//                if (f.getUser1().equals(user)) {
//                    userFriendshipDTOS.add(new UserFriendshipDTO(f.getUser2().getFirstName(),f.getUser2().getLastName(), f.getFriendsSince()));
//                } else if (f.getUser2().equals(user)) {
//                    userFriendshipDTOS.add(new UserFriendshipDTO(f.getUser1().getFirstName(),f.getUser1().getLastName(), f.getFriendsSince()));
//                }
//            }
//        }
//        return userFriendshipDTOS;
//
//    }

    @Override
    public Iterable<UserFriendshipDTO> friendshipsOfUserOnMonth(String username, Month month) {
        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return StreamSupport.stream(friendshipRepository.getAll().spliterator(),false)
                .filter(f -> f.getFriendsSince().getMonth().equals(month))
                .filter(f -> f.getUser1().equals(user) || f.getUser2().equals(user))
                .map(f -> f.getUser1().equals(user) ?
                        new UserFriendshipDTO(f.getUser2().getFirstName(), f.getUser2().getLastName(), f.getFriendsSince()) :
                        new UserFriendshipDTO(f.getUser1().getFirstName(), f.getUser1().getLastName(), f.getFriendsSince()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<User> updateUser(UUID id, String firstName, String lastName, String username, String email, String password) {
        User user = new User(id, firstName, lastName, username, email, password);
        return userRepository.update(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new IllegalArgumentException("Password is incorrect");
        return user;
    }

    public Iterable<UserFriendDTO> getFriendsOfUser(UUID id) {
        User user = userRepository.getById(id).orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        Iterable<Friendship> friendships = StreamSupport.stream(friendshipRepository.getAll().spliterator(), false)
                .filter(friendship -> friendship.getFriendshipStatus().equals(FriendshipRequestStatus.ACCEPTED))
                .collect(Collectors.toSet());


         Iterable<UserFriendDTO> test = StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> friendship.getUser1().equals(user) || friendship.getUser2().equals(user))
                .map(friendship -> friendship.getUser1().equals(user) ?
                        new UserFriendDTO(friendship.getUser2().getUsername(), friendship.getUser2().getEmail(), friendship.getFriendsSince()) :
                        new UserFriendDTO(friendship.getUser1().getUsername(), friendship.getUser1().getEmail(), friendship.getFriendsSince()))
                .collect(Collectors.toSet());

        System.out.println(test);

        return test;

    }

    public Page<UserFriendDTO> getFriendsOfUserPaged(UUID id, Pageable pageable) {

        User user = userRepository.getById(id).orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        Iterable<Friendship> friendships = StreamSupport.stream(friendshipRepository.getAll().spliterator(), false)
                .filter(friendship -> friendship.getFriendshipStatus().equals(FriendshipRequestStatus.ACCEPTED))
                .collect(Collectors.toSet());

        return friendshipDBPagingRepository.getFriendsOfUser(user.getId(), pageable);

    }


    public Iterable<Friendship> getPendingFriendRequests(UUID id) {
        User user = userRepository.getById(id).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        return StreamSupport.stream(friendshipRepository.getAll().spliterator(), false)
                .filter(friendship -> friendship.getUser2().equals(user) && friendship.getFriendshipStatus().equals(FriendshipRequestStatus.PENDING))
                .collect(Collectors.toSet());
    }

    public Iterable<User> getAllUsersExcept(UUID id) {
        User user = userRepository.getById(id).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        return StreamSupport.stream(userRepository.getAll().spliterator(), false)
                .filter(user1 -> !user1.equals(user))
                .collect(Collectors.toSet());
    }



    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
    }

    public void sendOneToOneMessage(User sender, User receiver, String message) {

        List<User> to = List.of(receiver);

        Message message1 = new Message(sender, to, message, LocalDateTime.now());
        messageDBRepository.save(message1);
        notifyObservers();
    }

    public void sendOneToManyMessage(User sender, List<User> receivers, String message) {
        Message message1 = new Message(sender, receivers, message, LocalDateTime.now());
        messageDBRepository.save(message1);
        notifyObservers();
    }

    public Iterable<Message> getAllMessages() {
        return messageDBRepository.getAll();
    }

    public Iterable<Message> getMessagesBetweenUsers(User user1, User user2) {

        return StreamSupport.stream(messageDBRepository.getAll().spliterator(), false)
                .filter(message -> message.getFrom().equals(user1) && message.getTo().contains(user2) ||
                        message.getFrom().equals(user2) && message.getTo().contains(user1))
                .collect(Collectors.toSet());

    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers)
            o.update();
    }



    public Page<User> getAllUsersPaged(Pageable pageable) {
        return userDBPagingRepository.findAll(pageable);
    }
}
