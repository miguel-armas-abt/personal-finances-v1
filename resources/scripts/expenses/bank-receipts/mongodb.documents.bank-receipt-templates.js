(function () {
  const COLLECTION_NAME = "bank_receipt_templates";

  const DICTIONARIES = {"MONTHS_ES_FULL":{"enero":"01","febrero":"02","marzo":"03","abril":"04","mayo":"05","junio":"06","julio":"07","agosto":"08","septiembre":"09","octubre":"10","noviembre":"11","diciembre":"12"},"MONTHS_ES_SHORT":{"Ene":"01","Feb":"02","Mar":"03","Abr":"04","May":"05","Jun":"06","Jul":"07","Ago":"08","Sep":"09","Oct":"10","Nov":"11","Dic":"12"}};

  const templates = [{"code":"BBVA_DEBIT_CARD","enabled":true,"description":"Consumo con tarjeta BBVA","gmail":{"from":"procesos@bbva.com.pe","subjects":["Has realizado un consumo con tu tarjeta BBVA"]},"extraction":{"recipient":{"regex":"Comercio\\s*:?\\s*(.+?)(?=\\s+(?:Monto|Fecha|Forma|Este))","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"Monto\\s*:?\\s*([0-9]+(?:[.,][0-9]{2})?)\\s*Moneda\\s*:?\\s*(PEN|USD|S/|US\\$)","amountGroup":1,"currencyGroup":2},"date":{"regex":"Fecha\\s*:?\\s*(\\d{2}/\\d{2}/\\d{4})\\s*Hora\\s*:?\\s*(\\d{2}:\\d{2}:\\d{2})","mode":"SPLIT_DATE_TIME","dateGroup":1,"timeGroup":2,"dateTimeSeparator":" ","pattern":"dd/MM/yyyy HH:mm:ss","locale":"es-PE","zoneId":"America/Lima"}}},{"code":"BBVA_PLIN","enabled":true,"description":"Transferencia PLIN BBVA","gmail":{"from":"procesos@bbva.com.pe","subjects":["Constancia de operación transferencia PLIN"]},"extraction":{"recipient":{"regex":"Plineaste\\s*(?:S/|\\$)\\s*[0-9.,]+\\s*a\\s+(.+?)(?=\\s+Detalles)","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"Plineaste\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{1,2})?)","currencyGroup":1,"amountGroup":2},"date":{"regex":"Fecha y hora:\\s*(\\d+ de \\w+, \\d{4} \\d{2}:\\d{2})","group":1,"mode":"LOCAL_DATE_TIME","pattern":"d 'de' MM, yyyy HH:mm","locale":"es-PE","zoneId":"America/Lima","replacementsRef":"MONTHS_ES_FULL"}}},{"code":"BBVA_PLIN_MERCHANT_QR","enabled":true,"description":"Pago a comercio con QR BBVA","gmail":{"from":"procesos@bbva.com.pe","subjects":["BBVA - Constancia de pago a comercios con QR","Constancia de pago a comercios con QR"]},"extraction":{"recipient":{"regex":"Comercio\\s+(.+?)(?=\\s+Forma de pago)","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)","currencyGroup":1,"amountGroup":2},"date":{"regex":"Fecha(?:\\s+de\\s+la\\s+operación|\\s+y\\s+hora)?\\s*(\\d{1,2} de \\p{L}+, \\d{4}(?: \\d{2}:\\d{2})?)","group":1,"mode":"LOCAL_DATE","pattern":"d 'de' MM, yyyy","locale":"es-PE","zoneId":"America/Lima","replacementsRef":"MONTHS_ES_FULL"}}},{"code":"BBVA_SERVICE_PAYMENT","enabled":true,"description":"Pago de servicios BBVA","gmail":{"from":"procesos@bbva.com.pe","subjects":["BBVA - Constancia Pago de servicios"]},"extraction":{"recipient":{"regex":"Nombre\\s+(?:de\\s+)?servicio\\s+(.+?)(?=\\s+Descripción)","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)","currencyGroup":1,"amountGroup":2},"date":{"regex":"Fecha y hora de la operación\\s*(\\d{2}\\s+\\w+,\\s+\\d{4}\\s+\\d{2}:\\d{2})","group":1,"mode":"LOCAL_DATE_TIME","pattern":"dd MM, yyyy HH:mm","locale":"es-PE","zoneId":"America/Lima","replacementsRef":"MONTHS_ES_FULL"}}},{"code":"IBK_PLIN","enabled":true,"description":"Pago Plin Interbank","gmail":{"from":"servicioalcliente@netinterbank.com.pe","subjects":["Constancia de Pago Plin"]},"extraction":{"recipient":{"regex":"Destinatario\\s*:?\\s*(.+?)(?=\\s+Destino)","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"Moneda\\s*y\\s*monto\\s*:?\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{2})?)","currencyGroup":1,"amountGroup":2},"date":{"regex":"(\\d{1,2}\\s+[A-Za-z]{3}\\s+\\d{4}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))","group":1,"mode":"LOCAL_DATE_TIME","pattern":"d MM yyyy hh:mm a","locale":"en","zoneId":"America/Lima","replacementsRef":"MONTHS_ES_SHORT"}}},{"code":"BCP_DEBIT_CARD","enabled":true,"description":"Consumo con Tarjeta de Débito BCP","gmail":{"from":"notificaciones@notificacionesbcp.com.pe","subjects":["Realizaste un consumo con tu Tarjeta de Débito BCP - Servicio de Notificaciones BCP"]},"extraction":{"recipient":{"regex":"Empresa\\s+(.+?)(?=\\s+N[úu]mero\\s+de\\s+operaci[oó]n)","group":1,"defaultValue":"unknown"},"amountCurrency":{"regex":"(?:Realizaste\\s+un\\s+consumo\\s+de|Total\\s+del\\s+consumo)\\s*(S/|\\$)\\s*([0-9]+(?:[.,][0-9]{1,2})?)","currencyGroup":1,"amountGroup":2},"date":{"regex":"Fecha\\s+y\\s+hora\\s*(\\d{1,2}\\s+de\\s+\\p{L}+\\s+de\\s+\\d{4}\\s+-\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))","group":1,"mode":"LOCAL_DATE_TIME","pattern":"d 'de' MM 'de' yyyy - hh:mm a","locale":"en","zoneId":"America/Lima","replacementsRef":"MONTHS_ES_FULL"}}}];

  const collection = db.getCollection(COLLECTION_NAME);

  function resolveDictionaryReferences(template) {
    if (!template.extraction || !template.extraction.date) {
      return template;
    }

    const date = template.extraction.date;
    const ref = date.replacementsRef;

    if (!ref) {
      return template;
    }

    const dictionary = DICTIONARIES[ref];

    if (!dictionary) {
      throw new Error("Dictionary not found: " + ref + " for template: " + template.code);
    }

    date.replacements = dictionary;
    delete date.replacementsRef;

    return template;
  }

  templates
    .map(resolveDictionaryReferences)
    .forEach((template) => {
      template.updatedAt = new Date();

      collection.updateOne(
        { code: template.code },
        {
          $set: template,
          $setOnInsert: {
            createdAt: new Date()
          }
        },
        { upsert: true }
      );

      print("Upserted bank receipt template: " + template.code);
    });

  print("Bank receipt templates were generated successfully.");
}());
