package com.clinica.ibmr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final ObjectProvider<JavaMailSender> mailSender;
    @Value("${app.mail.enabled:false}")
    private boolean enabled;
    @Value("${app.mail.from:}")
    private String from;
    @Value("${spring.mail.username:}")
    private String username;

    public EmailService(ObjectProvider<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    /** Retorna true se o e-mail foi entregue ao servidor SMTP; false se desligado ou com falha. */
    public boolean enviarCodigoConfirmacao(String para, String nome, String codigo) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (!enabled || sender == null) return false;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            String remetente = (from == null || from.isBlank()) ? username : from;
            if (remetente != null && !remetente.isBlank()) msg.setFrom(remetente);
            msg.setTo(para);
            msg.setSubject("VetPet - código de confirmação");
            msg.setText("Olá, " + nome + "!\n\nSeu código de confirmação do VetPet é: " + codigo
                    + "\n\nEle expira em 20 minutos.\nSe você não solicitou este cadastro, ignore esta mensagem.");
            sender.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail de confirmação para {}: {}", para, e.getMessage());
            return false;
        }
    }
}
