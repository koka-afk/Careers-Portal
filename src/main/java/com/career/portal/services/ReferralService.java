package com.career.portal.services;

import com.career.portal.dto.ReferralRequest;
import com.career.portal.models.JobVacancy;
import com.career.portal.models.Referral;
import com.career.portal.models.ReferralStatus;
import com.career.portal.models.User;
import com.career.portal.repositories.JobApplicationRepository;
import com.career.portal.repositories.JobVacancyRepository;
import com.career.portal.repositories.ReferralRepository;
import com.career.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferralService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ReferralRepository referralRepository;
    private final JobVacancyRepository jobVacancyRepository;
    private final UserRepository userRepository;

    public Referral createReferral(ReferralRequest referralRequest, Long referrerId){
        User referrer = userRepository.findById(referrerId)
                .orElseThrow(() -> new IllegalArgumentException("Referrer not found."));

        User referredUser = userRepository.findByEmail(referralRequest.getCandidateEmail())
                .orElseThrow(() -> new IllegalArgumentException("Candidate with this email does not have an account."));

        JobVacancy jobVacancy = jobVacancyRepository.findById(referralRequest.getJobVacancyId())
                .orElseThrow(() -> new IllegalArgumentException("Job Vacancy not found."));


        if(referralRepository.existsByReferrerIdAndReferredUserIdAndJobVacancyId(
                referrer.getId(),
                referredUser.getId(),
                jobVacancy.getId())){
            throw new IllegalArgumentException("Referral already exists for this user and this job.");
        }

        Referral newReferral = new Referral();
        newReferral.setReferrer(referrer);
        newReferral.setReferredUser(referredUser);
        newReferral.setJobVacancy(jobVacancy);
        newReferral.setMessage(referralRequest.getMessage());

        return referralRepository.save(newReferral);
    }

    public List<Referral> findReferralsByReferrer(Long referrerId){
        return referralRepository.findByReferrerId(referrerId);
    }

    public List<Referral> findPendingReferralsForUser(Long userId) {
        return referralRepository.findPendingReferralsForUser(userId);
    }

    public Optional<Referral> findByReferredUserAndJobVacancy(Long referredUserId, Long jobVacancyId) {
        return referralRepository.findByReferredUserIdAndJobVacancyId(referredUserId, jobVacancyId);
    }

    public Referral acceptReferral(Long referralId){
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new IllegalArgumentException("Referral not found."));

        if(referral.getStatus() != ReferralStatus.PENDING){
            throw new IllegalStateException("Referral status is not Pending status.");
        }

        referral.setStatus(ReferralStatus.ACCEPTED);
        referral.setAcceptedAt(LocalDateTime.now());

        return referralRepository.save(referral);
    }

    public Referral declineReferral(Long referralId){
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new IllegalArgumentException("Referral not found."));

        if(referral.getStatus() != ReferralStatus.PENDING){
            throw new IllegalArgumentException("Referral status is not Pending status.");
        }

        referral.setStatus(ReferralStatus.DECLINED);
        return referralRepository.save(referral);
    }

    public Optional<Referral> findById(Long id){
        return referralRepository.findById(id);
    }

    public List<Referral> findByReferrerAndJobVacancy(Long referrerId, Long jobVacancyId){
        return referralRepository.findByReferrerAndJobVacancy(referrerId, jobVacancyId);
    }

    public void expireOldReferrals(int daysOld){
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(daysOld);
        List<Referral> expiredReferrals = referralRepository.findExpiredReferrals(expiryDate);

        expiredReferrals.forEach(referral -> referral.setStatus(ReferralStatus.EXPIRED));
        referralRepository.saveAll(expiredReferrals);
    }


}
