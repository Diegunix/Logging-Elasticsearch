package com.elasticsearch.commons.restlogger;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Clase encargada de imprimir los mensajes de log formateados al filtro rest
 */
public class OperationLog {

    private static final String OPERATION = "operation";
    private static final String COMPONENT = "component";
    private static final String URL = "url";
    private static final String BACK_RESPONSE_STATUS = "service-status";
    private static final String BACK_EXECUTION_TIME = "service-time";
    
    private static final  String HTTP_LOGGER = "HTTP_LOGGER";
    private static Marker loggerMarker = MarkerFactory.getMarker(HTTP_LOGGER);

    /**
     * Tipo de mensaje que se mostrar por pantalla. Puede tener los valores como traza de datos de entrada de una request (REST_REQUEST) o
     * traza de datos de salida (REST_RESPONSE)
     */
    public enum OperationLogType {
        REQUEST, RESPONSE
    }

    private static final int CALLER_FUNCTION_NAME_INDEX = 4;

    /**
     * Método que imprime una traza de las peticiones Rest con la información facilitada en los campos de entrada, en formato JSON. En el
     * caso de no especificar un objeto de tipo {@link Logger}, el método no imprime ninguna traza. En el caso de no especificar un objeto
     * de tipo OperationLogType, se utilizará el definido con valor REQUEST
     *
     * @param log              Logger que será utilziado para imprimir la traza. Este campo es obligatorio
     * @param message          Campo opcional con el message que se quiere mostrar asociada a la traza
     * @param url              Campo opcional con la url de acceso al recurso que imprime la traza
     * @param operationLogType Tipo de operación con el que se imprimirá la taza
     */
    public static void log(Logger log, String message, String url, OperationLogType operationLogType) {
        log(log, message, url, operationLogType, 0, 0);
    }

    /**
     * Método que imprime una traza de las peticiones Rest con la información facilitada en los campos de entrada, en formato JSON. En el
     * caso de no especificar un objeto de tipo {@link Logger}, el método no imprime ninguna traza. En el caso de no especificar un objeto
     * de tipo OperationLogType, se utilizará el definido con valor REQUEST
     *
     * @param log              Logger que será utilziado para imprimir la traza. Este campo es obligatorio
     * @param message          Campo opcional con el message que se quiere mostrar asociada a la traza
     * @param url              Campo opcional con la url de acceso al recurso que imprime la traza
     * @param operationLogType Tipo de operación con el que se imprimirá la taza
     * @param status           status operación
     */
    public static void log(Logger log, String message, String url, OperationLogType operationLogType, int status, long time) {

        switch (getOperationLogType(operationLogType)) {
            case REQUEST:
                printLogRequest(log, message, url);
                break;
            case RESPONSE:
                printLogResponse(log, message, url, status, time);
                break;
        }

    }

    /**
     * Método genérico para la impresión de trazas
     *
     * @param log     Logger que será utilziado para imprimir la traza. Este campo es obligatorio
     * @param message Campo opcional con el message que se quiere mostrar asociada a la traza
     * @param url     Campo opcional con la url de acceso al recurso que imprime la traza
     * @param status  status operación
     * @param time    tiempo ejecución
     */
    private static void printLogResponse(Logger log, String message, String url, int status, long time) {
        if (log != null) {
            String component = getCallerFunctionName(log);
            MDC.put(OPERATION, OperationLogType.RESPONSE.name());
            MDC.put(URL, url);
            MDC.put(COMPONENT, component);
            MDC.put(BACK_RESPONSE_STATUS, String.valueOf(status));
            MDC.put(BACK_EXECUTION_TIME, String.valueOf(time));

            if (status < 400) {
                log.info(loggerMarker,message);
            } else if (status < 500) {
                log.warn(loggerMarker,message);
            } else {
                log.error(loggerMarker,message);
            }
            cleanMDC();
        }
    }

    /**
     * Método genérico para la impresión de trazas
     *
     * @param log     Logger que será utilziado para imprimir la traza. Este campo es obligatorio
     * @param message Campo opcional con el message que se quiere mostrar asociada a la traza
     * @param url     Campo opcional con la url de acceso al recurso que imprime la traza
     */
    private static void printLogRequest(Logger log, String message, String url) {
        if (log != null) {
            String component = getCallerFunctionName(log);
            MDC.put(OPERATION, OperationLogType.REQUEST.name());
            MDC.put(URL, url);
            MDC.put(COMPONENT, component);
            log.info(loggerMarker,message);
            cleanMDC();
        }
    }

    /**
     * Se limpian del MDC los parámetros añadidos, de modo que no estén presentes en trazas futuras del mismo hilo ya se loguen con esta
     * clase OperationLog o con cualquier otro logger
     */
    private static void cleanMDC() {
        MDC.remove(URL);
        MDC.remove(COMPONENT);
        MDC.remove(OPERATION);
        MDC.remove(BACK_RESPONSE_STATUS);
        MDC.remove(BACK_EXECUTION_TIME);
    }

    /**
     * Método que devuelve el tipo de traza que se va a imprimir. Si el parámtero operationLogType es null, entonces se devuelve el de tipo
     * REQUEST
     *
     * @param operationLogType Tipo de operación con el que se imprimirá la taza
     * @return OperationLogType
     */
    private static OperationLogType getOperationLogType(OperationLogType operationLogType) {
        OperationLogType result = OperationLogType.REQUEST;
        if (operationLogType != null) {
            result = operationLogType;
        }
        return result;
    }

    /**
     * Método que recupera la clase y el método que es invocado para imprimir la traza. Si se produce un error al tratar de recuperar esta
     * información se pinta una traza de error y se devuelve una cadena vacía.
     *
     * @param log Logger que será utilziado para imprimir la traza. Este campo es obligatorio
     * @return String
     */
    private static String getCallerFunctionName(Logger log) {
        String result = null;
        try {
            result = String.join(".", Thread.currentThread().getStackTrace()[CALLER_FUNCTION_NAME_INDEX].getClassName(),
                    Thread.currentThread().getStackTrace()[CALLER_FUNCTION_NAME_INDEX].getMethodName());
        } catch (Exception e) {
            log.error("No se puede recuperar el nombre del método que ha invocado la traza de OperadorLog", e);
            result = "";
        }
        return result;
    }

}
