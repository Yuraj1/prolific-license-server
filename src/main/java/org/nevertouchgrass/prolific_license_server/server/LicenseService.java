package org.nevertouchgrass.prolific_license_server.server;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LicenseService {

    private final Map<String, String> licenseStore = new ConcurrentHashMap<>();
    private final Map<String, Boolean> activationStore = new ConcurrentHashMap<>();
    private final String filePath = "licenses.dat";
    private final String activationPath = "activations.dat";

    public LicenseService() {
        loadLicenseData();
        loadActivationData();
    }

    public String generateLicenseKey(String userId) {
        String licenseKey = "LICENSE-" + userId + "-" + System.currentTimeMillis();
        licenseStore.put(userId, licenseKey);
        System.out.println("Generated license key for " + userId + ": " + licenseKey);

        saveLicenseData();
        return licenseKey;
    }

    public boolean verifyLicense(String userId, String licenseKey) {
        // Проверяем наличие ключа в хранилище
        String storedLicenseKey = licenseStore.get(userId);
        if (storedLicenseKey != null) {
            System.out.println("Verifying license for " + userId + ": " + storedLicenseKey + " == " + licenseKey);
            boolean valid = storedLicenseKey.equals(licenseKey);
            if (valid) {
                activationStore.put(userId, true);
                saveActivationData();
            }
            return valid;
        } else {
            System.out.println("No license found for user: " + userId);
            return false; // Нет ключа для этого пользователя
        }
    }

    private void saveLicenseData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(licenseStore);
            System.out.println("License data saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isActivated(String userId) {
        return activationStore.getOrDefault(userId, false);
    }
    private void loadLicenseData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<String, String> savedLicenseStore = (Map<String, String>) ois.readObject();
            licenseStore.putAll(savedLicenseStore);
            System.out.println("License data loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous license data found.");
        }
    }

    public String getLicenseStore(String userId) {
        return licenseStore.get(userId);
    }

    public void changeLicenseKey(String userId, String newLicenseKey) {
        licenseStore.put(userId, newLicenseKey);
    }


    private void saveActivationData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(activationPath))) {
            oos.writeObject(activationStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadActivationData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(activationPath))) {
            Map<String, Boolean> loaded = (Map<String, Boolean>) ois.readObject();
            activationStore.putAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous activation data found.");
        }
    }
}
