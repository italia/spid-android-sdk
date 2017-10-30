# SPID Android SDK

Questo SDK consente agli sviluppatori una rapida integrazione dell'autenticazione basata sul protocollo SPID nella propria applicazione mobile su piattaforma Android.
Il progetto è stato realizzato dal team **Need for SPID**.

## Struttura del repository

Il repository risulta così strutturato:
* **mobile**: componente client-side, a sua volta è suddivisa in
  * **app**: app mobile di test
  * **timspid**: libreria da includere su nuove app mobili
* **serviceprovider**: componente server-side per svolge la funzionalità di Service Provider

## Ambiente di Staging

Per consentire il testing del dell'integrazione SPID senza la necessità di impiegare delle identità SPID reali, è stato completato il delivery di un ambiente di test su piattaforma Docker composto dai seguenti elementi architetturali:
* **Identity Provider AgID**: realizzato impiegando i container Docker condivisi da AgID
  * **Back-Office**: accessibile all'URL https://idpagid.spidtest.nuvolaitaliana.it:8080/#/
  * **Identity Provider**: accessibile all'URL https://idpagid.spidtest.nuvolaitaliana.it:8080/#/ basato su WSO2-is, su cui è stata completata la registrazione del Service Provider.
* **Service Provider**: accessibile all'URL http://spagid.spidtest.nuvolaitaliana.it:8080/

## Informazioni

Per la documentazione tecnica su SPID si può far riferimento ai seguenti URI:
* https://developers.italia.it/it/spid/ : portale dedicato agli sviluppatori da cui risultano accessibili tutti i repository del progetto SPID
* http://spid-regole-tecniche.readthedocs.io/en/latest/index.html : contenente la documentazione tecnica
 
