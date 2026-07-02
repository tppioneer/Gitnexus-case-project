package com.example.telecom.notification.template;

public class NotificationTemplate {
    private String templateId;
    private String name;
    private String channel;
    private String subjectTemplate;
    private String bodyTemplate;

    public NotificationTemplate() {}

    public NotificationTemplate(String templateId, String name, String channel,
                                 String subjectTemplate, String bodyTemplate) {
        this.templateId = templateId;
        this.name = name;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getSubjectTemplate() { return subjectTemplate; }
    public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }
    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
}
