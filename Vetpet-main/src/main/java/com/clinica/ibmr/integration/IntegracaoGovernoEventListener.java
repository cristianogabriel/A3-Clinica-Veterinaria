package com.clinica.ibmr.integration;

import com.clinica.ibmr.service.IntegracaoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class IntegracaoGovernoEventListener {
    private final IntegracaoService integracoes;

    public IntegracaoGovernoEventListener(IntegracaoService integracoes) {
        this.integracoes = integracoes;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoAplicarVacinaAntirrabica(VacinaAntirrabicaAplicadaEvent event) {
        integracoes.processarPorVacina(event.vacinaId());
    }
}
