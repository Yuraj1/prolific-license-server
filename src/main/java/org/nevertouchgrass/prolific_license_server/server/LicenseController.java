package org.nevertouchgrass.prolific_license_server.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody Map<String, String> payload) {
        System.out.println("\n\nReceived verify request with payload: " + payload);

        String userId = payload.get("userId");
        String licenseKey = payload.get("licenseKey");

        System.out.println("userId: " + userId);
        System.out.println("licenseKey: " + licenseKey);

        boolean isValid = licenseService.verifyLicense(userId, licenseKey);

        System.out.println("License valid: " + isValid);

        return ResponseEntity.ok(isValid);
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> payload,
                                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("\n\nReceived create request with payload: " + payload);
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.equals("Bearer SECRET_TOKEN")) {
            System.out.println("Authorization failed: invalid token");
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        String userId = payload.get("userId");
        System.out.println("userId for license creation: " + userId);

        String licenseKey = licenseService.generateLicenseKey(userId);

        System.out.println("Generated licenseKey: " + licenseKey);

        return ResponseEntity.ok(Map.of("licenseKey", licenseKey));
    }

    @GetMapping("/show/{userId}")
    public ResponseEntity<Map<String, String>> show(@PathVariable String userId,
                                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("\n\nReceived show request for userId: " + userId);
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.equals("Bearer SECRET_TOKEN")) {
            System.out.println("Authorization failed: invalid token");
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        String licenseKey = licenseService.getLicenseStore(userId);

        if (licenseKey == null) {
            System.out.println("No license found for userId: " + userId);
            return ResponseEntity.status(404).body(Map.of("error", "License not found"));
        }

        return ResponseEntity.ok(Map.of("licenseKey", licenseKey));
    }

    @PostMapping("/change-key")
    public ResponseEntity<Map<String, String>> changeKey(@RequestBody Map<String, String> payload,
                                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.equals("Bearer SECRET_TOKEN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        String userId = payload.get("userId");
        String newLicenseKey = payload.get("newLicenseKey");

        licenseService.changeLicenseKey(userId, newLicenseKey);

        return ResponseEntity.ok(Map.of("message", "License key updated successfully"));
    }

    @PostMapping("/is-activated")
    public ResponseEntity<Boolean> isActivated(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        return ResponseEntity.ok(licenseService.isActivated(userId));
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

}
