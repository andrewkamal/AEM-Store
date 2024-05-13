package com.aemstore.core.models;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Email Service Configuration", description = "Configuration for Email Service")
public @interface EmailServiceConfiguration {

    @AttributeDefinition(name = "SMTP Host", description = "SMTP server host")
    String smtpHost() default "localhost";

    @AttributeDefinition(name = "SMTP Port", description = "SMTP server port")
    int smtpPort() default 25;

    @AttributeDefinition(name = "SMTP User", description = "SMTP server username")
    String smtpUser() default "";

    @AttributeDefinition(name = "SMTP Password", description = "SMTP server password")
    String smtpPassword() default "";

}