package com.sk.prediction.model;

import javax.xml.bind.annotation.XmlRootElement;

/***
 * A sample class for adding error information in the response
 */
@XmlRootElement
public class RestErrorInfo {

    public RestErrorInfo() {

    }

    public RestErrorInfo(String errorcode) {
        this.errorcode = errorcode;

    }

    public RestErrorInfo(String errorcode, String errorMessage) {
        this.errorcode = errorcode;
        this.errorMessage = errorMessage;

    }

    public RestErrorInfo(Exception ex, String detail) {

        this.errorMessage = detail;
    }

    private String errorMessage;

    private String errorcode;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return errorMessage;
    }

    public void setMessage(String message) {
        this.errorMessage = message;
    }

}
