# SPID Android SDK

Questo SDK consente agli sviluppatori una rapida integrazione dell'autenticazione basata sul protocollo SPID nella propria applicazione mobile su piattaforma Android.

## Struttura del repository
Il repository risulta così strutturato:
* **mobile**: componente client-side, a sua volta è suddivisa in
  * **app**: app mobile di test
  * **timspid**: libreria da includere su nuove app mobili
* **serviceprovider**: componente server-side per svolge la funzionalità di Service Provider

## Ambiente di test
Per consentire i test applicativi è stato completato il delivery di un ambiente di test composto da:
* **Identity Provider**: accessibile all'URL https://idpagid.spidtest.nuvolaitaliana.it:8080/#/ sfruttando i container docker messi a disposizione dal Team per la Trasformazione Digitale su cui è stata completata la registrazione della componente di SP.
* **Service Provider**: accessibile all'URL http://spagid.spidtest.nuvolaitaliana.it:8080/
