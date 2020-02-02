package com.studia.mappapplication;

import android.content.Intent;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class AuthenticationService {

    private FirebaseAuth firebaseAuth;
    private LoginActivity context;

    public AuthenticationService(LoginActivity context) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void signIn(String email, String password) {
        List<String> validationErrors = Lists.newArrayList();
        validateEmail(email, validationErrors);
        validatePassword(password, validationErrors);

        if (!validationErrors.isEmpty()) {
            context.setErrorMessage(String.join(", ", validationErrors));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        navigateToMainActivity();
                    } else {
                        context.clearTextFields();
                        String error = "Blad autentykacji ";
                        if (task.getException() != null && !Strings.isNullOrEmpty(task.getException().getMessage())) {
                            error += task.getException().getMessage();
                        }
                        context.setErrorMessage(error);
                    }
                });
    }

    public void signUpAndInsertUser(final String firstName, final String lastName, final String email, String password, String confirmedPassword) {
        List<String> validationErrors = Lists.newArrayList();
        validateFirstName(firstName, validationErrors);
        validateLastName(lastName, validationErrors);
        validateEmail(email, validationErrors);
        validatePassword(password, validationErrors);
        validatePasswordsIdentity(password, confirmedPassword, validationErrors);

        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join(", ", validationErrors);
            context.setErrorMessage(errorMessage);
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        context.clearTextFields();
                        insertUser(firstName, lastName, email);
                        context.setSignInMode();
                    } else {
                        context.clearTextFields();
                        String error = "Account creation failed! ";
                        if (task.getException() != null && !Strings.isNullOrEmpty(task.getException().getMessage())) {
                            error = task.getException().getMessage();
                        }
                        context.setErrorMessage(error);
                    }
                });
    }


    private void validateLastName(String lastName, List<String> validationErrors) {
        if (Strings.isNullOrEmpty(lastName)) {
            validationErrors.add("Prosze podac nazwisko!");
        }
    }

    private void validateFirstName(String firstName, List<String> validationErrors) {
        if (Strings.isNullOrEmpty(firstName)) {
            validationErrors.add("Prosze podac imie!");
        }
    }

    private void validateEmail(String email, List<String> validationErrors) {
        if (Strings.isNullOrEmpty(email)) {
            validationErrors.add("Prosze podac mail");
            return;
        }

        if (email.length() <= 4) {
            validationErrors.add("Email musi byc dluzszy niz 3 znaki");
        }
    }

    private void validatePassword(String password, List<String> validationErrors) {
        if (Strings.isNullOrEmpty(password)) {
            validationErrors.add("Prosze podac haslo!");
            return;
        }

        if (password.length() <= 5) {
            validationErrors.add("Haslo musi miec co najmniej 5 znakow");
        }
    }

    private void validatePasswordsIdentity(String password, String confirmedPassword, List<String> validationErrors) {
        if (!password.equals(confirmedPassword)) {
            validationErrors.add("Podane hasła różnią się");
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private void insertUser(String firstName, String lastName, String email) {
        User user = new User(firstName, lastName, email);
        FirestoreDatabaseService databaseService = new FirestoreDatabaseService();
        databaseService.insertUser(user);
    }
}