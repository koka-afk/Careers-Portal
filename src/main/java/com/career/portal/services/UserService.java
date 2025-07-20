package com.career.portal.services;

import com.career.portal.dto.UserProfileUpdateRequest;
import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("User with this email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null){
            user.setRole(UserRole.USER);
        }
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        User registeredUser = userRepository.save(user);
        emailService.sendVerificationEmail(registeredUser.getEmail(), registeredUser.getVerificationToken());
        return registeredUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findCandidatesOrderedByScore(){
        return userRepository.findByRoleOrderByProfileScoreDesc(UserRole.USER);
    }

    public User updateProfileScore(Long userId, Double score){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfileScore(score);
        return userRepository.save(user);
    }

    public User changeUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public User updateUserProfile(Long userId, UserProfileUpdateRequest userProfileUpdateRequest){
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setFirstName(userProfileUpdateRequest.getFirstName());
        existingUser.setLastName(userProfileUpdateRequest.getLastName());
        existingUser.setPhone(userProfileUpdateRequest.getPhone());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public List<User> findUsersWithMinimumScore(Double minScore){
        return userRepository.findByProfileScoreGreaterThan(minScore);
    }

    public User uploadResume(Long userId, String resumePath){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setResumePath(resumePath);
        return userRepository.save(user);
    }

    public boolean verifyEmail(String token){
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if(user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Verification token has expired");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email does not exist."));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("This account has already been verified.");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email does not exist"));

        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());
    }

    public void resetPassword(String token, String password){
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

}
