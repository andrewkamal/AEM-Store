package com.aemstore.core.models;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = EmailService.class, immediate = true)
@Designate(ocd = EmailServiceConfiguration.class)
public class EmailService {

    private String smtpHost;
    private int smtpPort;
    private String smtpUser;
    private String smtpPassword;

    @Activate
    @Modified
    protected void activate(EmailServiceConfiguration config) {
        this.smtpHost = config.smtpHost();
        this.smtpPort = config.smtpPort();
        this.smtpUser = config.smtpUser();
        this.smtpPassword = config.smtpPassword();
    }

    public void sendEmail(String to, String subject, String message) throws EmailException {
        Email email = new HtmlEmail();
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);
        email.setAuthenticator(new DefaultAuthenticator(smtpUser, smtpPassword));
        email.setSSLOnConnect(true);
        email.setFrom(smtpUser);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to);
        email.send();
    }
}
