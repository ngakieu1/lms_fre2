package com.topcit.aims.aims.application;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    public String generateOtp(String username){
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(username, otp);
        return otp;
    }
    public boolean validateOtp(String username, String otpInput){
        if (!otpStorage.containsKey(username)) return false;
        String storedOtp = otpStorage.get(username);
        return storedOtp.equals(otpInput);
    }
    public void clearOtp(String username){
        otpStorage.remove(username);
    }
    public void sendSms(String phoneNumber, String otp) {
        // TODO: Integrate Twilio or generic SMS API here
        System.out.println("========================================");
        System.out.println("SENDING SMS TO " + phoneNumber);
        System.out.println("YOUR OTP CODE IS: " + otp);
        System.out.println("========================================");
    }
}
