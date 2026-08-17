package com.example.backend_siop.utilisateur.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void envoyerEmailActivation(String email, String prenom, String token) {
        String lien = frontendUrl + "/activation-compte?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Activez votre compte SPELEV");
        message.setText("""
                Bonjour %s,

                Un compte a été créé pour vous sur la plateforme SIOP de SPELEV.
                Pour l'activer, veuillez définir votre mot de passe en cliquant sur le lien ci-dessous :

                %s

                Ce lien est valable 48 heures.

                Cordialement,
                L'équipe SPELEV
                """.formatted(prenom, lien));

        mailSender.send(message);
    }

    public void envoyerEmailReinitialisation(String email, String prenom, String token) {
        String lien = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe - SPELEV");
        message.setText("""
                Bonjour %s,

                Vous avez demandé la réinitialisation de votre mot de passe sur la plateforme SIOP de SPELEV.
                Cliquez sur le lien ci-dessous pour choisir un nouveau mot de passe :

                %s

                Ce lien est valable 1 heure.

                Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.

                Cordialement,
                L'équipe SPELEV
                """.formatted(prenom, lien));

        mailSender.send(message);
    }
}