package com.career.portal.controllers;

import com.career.portal.dto.ReferralRequest;
import com.career.portal.models.Referral;
import com.career.portal.models.User;
import com.career.portal.services.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> createReferral(@RequestBody ReferralRequest referralRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User referrer = (User) authentication.getPrincipal();

            Referral createdReferral = referralService.createReferral(referralRequest, referrer.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReferral);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<Referral>> getSentReferrals(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User referrer = (User) authentication.getPrincipal();
        List<Referral> referrals = referralService.findReferralsByReferrer(referrer.getId());
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('USER', 'CANDIDATE')")
    public ResponseEntity<List<Referral>> getPendingReferrals(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Referral> referrals = referralService.findPendingReferralsForUser(user.getId());
        return ResponseEntity.ok(referrals);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Referral> acceptReferral(@PathVariable Long id){
        try {
            Referral acceptReferral = referralService.acceptReferral(id);
            return ResponseEntity.ok(acceptReferral);
        }catch (IllegalArgumentException | IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Referral> declineReferral(@PathVariable Long id){
        try{
            Referral declinedReferral = referralService.declineReferral(id);
            return ResponseEntity.ok(declinedReferral);
        }catch (IllegalArgumentException | IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/{jobId}/referrer/{referrerId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<Referral>> getReferralsByJobAndReferrer(
            @PathVariable Long jobId,
            @PathVariable Long referrerId){

        List<Referral> referrals = referralService.findByReferrerAndJobVacancy(referrerId, jobId);
        return ResponseEntity.ok(referrals);
    }
}
