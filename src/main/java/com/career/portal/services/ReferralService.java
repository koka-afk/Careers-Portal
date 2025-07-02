package com.career.portal.services;

import com.career.portal.models.Referral;
import com.career.portal.models.ReferralStatus;
import com.career.portal.repositories.JobApplicationRepository;
import com.career.portal.repositories.ReferralRepository;
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

    public Referral createReferral(Referral referral){
        if(referralRepository.existsByReferrerIdAndReferredUserIdAndJobVacancyId(
                referral.getReferrer().getId(),
                referral.getReferredUser().getId(),
                referral.getJobVacancy().getId())){
            throw new IllegalArgumentException("Referral already exists for this user and this job.");
        }

        return referralRepository.save(referral);
    }

    public List<Referral> findReferralByReferrer(Long referrerId){
        return referralRepository.findByReferrerId(referrerId);
    }

    public List<Referral> findPendingReferralsForUser(Long userId) {
        return referralRepository.findPendingReferralsForUser(userId);
    }

    public Referral acceptReferral(Long referralId){
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new IllegalArgumentException("Referral not found."));

        if(referral.getStatus() != ReferralStatus.PENDING){
            throw new IllegalArgumentException("Referral status is not Pending status.");
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
