package com.centerm.quickcrypt.models;

public class TerminalCertResult {
    public int responseCode;
    public String responseText;
    public String terminalCertPem;
    public String rkiData;

    public boolean isSuccess() {
        return responseCode == 0;
    }
}
