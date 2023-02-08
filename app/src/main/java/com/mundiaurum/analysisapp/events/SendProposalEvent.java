package com.mundiaurum.analysisapp.events;

public class SendProposalEvent {
    private String envioId;
    public SendProposalEvent(String envio){this.envioId = envio;}
    public String getEnvioId() {return envioId;}
}
