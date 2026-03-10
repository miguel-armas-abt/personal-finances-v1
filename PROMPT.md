Quiero quedarme solo con el query param para filtrar pagos de servicios bbva

```
%28%28from%3Aprocesos%40bbva.com.pe+subject%3A%22Constancia+de+operaci%C3%B3n+transferencia+PLIN%22%29+OR+%28from%3Anotificaciones%40notificacionesbcp.com.pe+subject%3A%22Realizaste+un+consumo+con+tu+Tarjeta+de+Cr%C3%A9dito+BCP+-+Servicio+de+Notificaciones+BCP%22%29+OR+%28from%3Aprocesos%40bbva.com.pe+subject%3A%22Has+realizado+un+consumo+con+tu+tarjeta+BBVA%22%29+OR+%28from%3Aservicioalcliente%40netinterbank.com.pe+subject%3A%22Constancia+de+Pago+Plin%22%29+OR+%28from%3Anotificaciones%40notificacionesbcp.com.pe+subject%3A%22Realizaste+un+consumo+con+tu+Tarjeta+de+D%C3%A9bito+BCP+-+Servicio+de+Notificaciones+BCP%22%29+OR+%28from%3Aprocesos%40bbva.com.pe+subject%3A%22Constancia+de+pago+a+comercios+con+QR%22%29+OR+%28from%3Anotificaciones%40yape.pe+subject%3A%22Pago+exitoso%22%29+OR+%28from%3Aprocesos%40bbva.com.pe+subject%3A%22BBVA+-+Constancia+Pago+de+servicios%22%29+OR+%28from%3Anoreply%40yape.pe+subject%3A%22Tu+yapeo+de+servicio+ha+sido+confirmado%22%29%29+after%3A2026%2F03%2F08
```

```
      "IBK_PLIN":
        from: servicioalcliente@netinterbank.com.pe
        subject: "Constancia de Pago Plin"
```