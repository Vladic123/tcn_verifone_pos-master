package com.tcn.sdk.liftdemo;

/**
 * verifone commands wrapper
 */

public class tcn_verifone_VXLinkWrapper {

    public static final String COMMAND_PURCHASE = "PR";
    public static final String COMMAND_PURCHASE_PLUS_CASH = "PR";
    public static final String COMMAND_CASH_OUT = "CO";
    public static final String COMMAND_REFUND = "RF";
    public static final String COMMAND_LOGON = "LO";
    public static final String COMMAND_SETTLEMENT_CUTOVER = "SC";
    public static final String COMMAND_REPRINT_RECEIPT = "RR";
    public static final String COMMAND_DISPLAY_ADMINISTRATION_MENU = "DA";
    public static final String COMMAND_GET_RECEIPT_REQUEST = "GR?";
    public static final String COMMAND_GET_RECEIPT_RESPONSE = "GR";
    public static final String COMMAND_RESULT_REQUEST = "RS?";
    public static final String COMMAND_RESULT_RESPONSE = "RS";
    public static final String COMMAND_CONFIGURE_PRINTING = "CP?";
    public static final String COMMAND_CONFIGURE_PRINTING_RESPONSE = "CP";
    public static final String COMMAND_READY_TO_PRINT_REQUEST = "RP?";
    public static final String COMMAND_READY_TO_PRINT_RESPONSE = "RP";
    public static final String COMMAND_PRINT_REQUEST = "PT?";
    public static final String COMMAND_PRINT_RESPONSE = "PT";


    public static final String CONFIGURE_ON = "ON";
    public static final String CONFIGURE_OFF = "OFF";
    public static final String CONFIGURE_OK = "OK";

    public static String ConvertStringHEX2Char(String inData){

        StringBuilder builder = new StringBuilder();

        try {
            for (int i = 0; i < inData.length(); i = i + 2) {
                // Step-1 Split the hex string into two character group
                String s = inData.substring(i, i + 2);
                // Step-2 Convert the each character group into integer using valueOf method
                int n = Integer.valueOf(s, 16);
                // Step-3 Cast the integer value to char
                builder.append((char) n);
            }
        }catch(Exception ex){
            int a=0;
            a=a+1;
        }

        return builder.toString();
    }

    public static String ConvertStringChar2Hex(String inData){

        if(inData == null){
            return null;
        }

        byte [] bytes = inData.getBytes();

        String result="";

        for(int a=0;a<inData.length();a++){
            result = String.format("%02x",bytes[a]);
        }
        return result;
    }

    private String calcCRC(String inData){

        if(inData==null){
            return null;
        }

        byte [] bytes = inData.getBytes();

        byte CRC = bytes[0];

        for(int a=1;a<inData.length();a++){
            CRC=(byte)(CRC ^ bytes[a]);
        }

        String result=String.format("%02x",CRC);

        return ConvertStringHEX2Char(result);
    }

    private String prepareCommand(String vxLinkMessage){

        if(vxLinkMessage==null){
            return null;
        }

        String result="";

        try {
            // add header
            result = "V2";

            // add payload length
            Integer payloadLength = vxLinkMessage.length();
            result += ConvertStringHEX2Char(Integer.toHexString(0x10000 |payloadLength).substring(1));

            // add payload as is
            result += vxLinkMessage;

            // add CRC
            result += calcCRC(vxLinkMessage);
        }catch (Exception ex){

        }


        return result;
    }

    public String preparePurchase(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_PURCHASE+","+eTrans.getTxid()+","+eTrans.getMid()+","+eTrans.getAmount());
    }

    public String preparePurchasePlusCash(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_PURCHASE_PLUS_CASH+","+eTrans.getTxid()+","+eTrans.getMid()+","+eTrans.getAmount()+","+eTrans.getCash());
    }

    public String prepareCashOut(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_CASH_OUT+","+eTrans.getTxid()+","+eTrans.getMid()+","+eTrans.getAmount());
    }

    public String prepareRefund(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_REFUND+","+eTrans.getTxid()+","+eTrans.getMid()+","+eTrans.getAmount());
    }

    public String prepareLogon(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_LOGON+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareSettlmentCutover(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_SETTLEMENT_CUTOVER+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareReprintReceipt(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_REPRINT_RECEIPT+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareDisplayAdministrationMen(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_DISPLAY_ADMINISTRATION_MENU+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareGetReceiptRequest(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_GET_RECEIPT_REQUEST+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareResultRequest(tcn_verifone_EFTPOStransaction eTrans){
        return prepareCommand(COMMAND_RESULT_REQUEST+","+eTrans.getTxid()+","+eTrans.getMid());
    }

    public String prepareConfigurePrinting(Boolean onOff){

        if(onOff) {
            return prepareCommand(COMMAND_CONFIGURE_PRINTING + ","+CONFIGURE_ON);
        }
        return prepareCommand(COMMAND_CONFIGURE_PRINTING + ","+CONFIGURE_OFF);
    }

    public String prepareReadyToPrintResponse(){
            return prepareCommand(COMMAND_READY_TO_PRINT_RESPONSE+","+CONFIGURE_OK);
    }

    public String preparePrintResponse(){
        return prepareCommand(COMMAND_PRINT_RESPONSE+","+CONFIGURE_OK);
    }


    public tcn_verifone_EFTPOStransaction parseResponse(String inResponse){

        tcn_verifone_EFTPOStransaction eTrans = new tcn_verifone_EFTPOStransaction();
        // check for "V2"
        if(inResponse.substring(0,4).equals("5632")) {
            String decoded = ConvertStringHEX2Char(inResponse);
            decoded = decoded.substring(4, decoded.length() - 1);
            String[] params = decoded.split(",");

            // check params
            if (params.length == 0 || params[0] == null) {
                return null;
            }

            // fill operation
            eTrans.setOper(params[0].toUpperCase());

            switch (params[0].toUpperCase()) {
                case COMMAND_GET_RECEIPT_RESPONSE:{
                    eTrans.setTxid(params[1]);
                    eTrans.setMid(params[2]);
                    eTrans.setReceipt_text(params[3]);
                }
                break;

                case COMMAND_RESULT_RESPONSE: {
                    // ok, it is result response
                    // fill transaction
                    eTrans.setTxid(params[1]);
                    eTrans.setMid(params[2]);
                    eTrans.setRespcode(params[3]);
                    eTrans.setResp_text(params[4]);
                    eTrans.setCard_type(params[5]);
                    eTrans.setOnline_flag(params[6]);
                }
                break;

                case COMMAND_READY_TO_PRINT_REQUEST: {
                    // ok, it is result response
                    // fill transaction
                }
                break;

                case COMMAND_PRINT_REQUEST: {
                    // ok, it is result response
                    // fill transaction
                    eTrans.setPrint_text(decoded.substring(4));
                }
            }
        }


        return eTrans;

    }
}
