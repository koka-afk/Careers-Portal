package com.career.portal.services;

import com.career.portal.dto.EducationRequest;
import com.career.portal.dto.ExperienceRequest;
import com.career.portal.dto.UserProfileUpdateRequest;
import com.career.portal.models.Education;
import com.career.portal.models.Experience;
import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.repositories.EducationRepository;
import com.career.portal.repositories.ExperienceRepository;
import com.career.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;


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
        existingUser.setLinkedinProfile(userProfileUpdateRequest.getLinkedinProfile());
        existingUser.setGithubProfile(userProfileUpdateRequest.getGithubProfile());

        return userRepository.save(existingUser);
    }

    public Education addEducation(Long userId, EducationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Education education = new Education();
        education.setUser(user);
        education.setSchool(request.getSchool());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        return educationRepository.save(education);
    }

    public Education updateEducation(Long educationId, EducationRequest request) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education record not found"));
        education.setSchool(request.getSchool());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        return educationRepository.save(education);
    }

    public void deleteEducation(Long educationId) {
        if (!educationRepository.existsById(educationId)) {
            throw new IllegalArgumentException("Education record not found");
        }
        educationRepository.deleteById(educationId);
    }

    public Experience addExperience(Long userId, ExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Experience experience = new Experience();
        experience.setUser(user);
        experience.setTitle(request.getTitle());
        experience.setCompany(request.getCompany());
        experience.setLocation(request.getLocation());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setDescription(request.getDescription());
        return experienceRepository.save(experience);
    }

    public Experience updateExperience(Long experienceId, ExperienceRequest request) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience record not found"));
        experience.setTitle(request.getTitle());
        experience.setCompany(request.getCompany());
        experience.setLocation(request.getLocation());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setDescription(request.getDescription());
        return experienceRepository.save(experience);
    }

    public void deleteExperience(Long experienceId) {
        if (!experienceRepository.existsById(experienceId)) {
            throw new IllegalArgumentException("Experience record not found");
        }
        experienceRepository.deleteById(experienceId);
    }

    public boolean canModifyEducation(Long educationId, Long userId) {
        Education education = educationRepository.findById(educationId).orElse(null);
        return education != null && education.getUser().getId().equals(userId);
    }

    public boolean canModifyExperience(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findById(experienceId).orElse(null);
        return experience != null && experience.getUser().getId().equals(userId);
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
