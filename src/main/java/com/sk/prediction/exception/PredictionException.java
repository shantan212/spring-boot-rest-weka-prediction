package com.sk.prediction.exception;

public class PredictionException extends Exception {
    /**
     * 
     */

    private static final long serialVersionUID = 1L;
    private Exception excep;
    private String errorCode;
    private String errorMessage;
    private String errorDescription;

    public PredictionException(String errorCode) {

        super(errorCode);

        this.errorCode = errorCode;
        int intErrorCode = Integer.parseInt(errorCode);

    }

    public PredictionException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public PredictionException(Exception e, String errorCode) {
        this.errorCode = errorCode;
        this.excep = e;
    }

    public PredictionException(String errorCode, String errorMessage, String errorDescription) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public PredictionException(Exception excep, String errorCode, String errorMessage,
            String errorDescription) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public Exception getExcep() {
        return this.excep;
    }

    public void setExcep(Exception excep) {
        this.excep = excep;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
