package com.example.pfe_backend.repository;

import com.example.pfe_backend.model.Message;
import com.example.pfe_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrderByTimestampDesc(User sender, User receiver);
    List<Message> findBySenderOrReceiverOrderByTimestampDesc(User user1, User user2);
    List<Message> findByReceiverOrderByTimestampDesc(User receiver);
    void deleteBySender(User sender);  // Supprime par objet User
    void deleteByReceiver(User receiver);
}
